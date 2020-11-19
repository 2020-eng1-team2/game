package marlin.auber.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.*;
import java.util.stream.Collectors;

public class World {
    public Viewport viewport;

    public Map map;

    public boolean debugMode = true;

    public World(Map map) {
        this.map = map;
        this.viewport = new ExtendViewport(20, 20);
    }

    static class NavNode {
        String name;
        Vector2 position;
        List<NavNode> links;

        public NavNode(String name, Vector2 position) {
            this.name = name;
            this.position = position;
            this.links = new ArrayList<>();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NavNode navNode = (NavNode) o;
            return name.equals(navNode.name) &&
                    position.equals(navNode.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, position);
        }

        @Override
        public String toString() {
            if (links.contains(this)) {
                return String.format("[%s infinite loop]", this.name);
            }
            return "NavNode{" +
                    "name='" + name + '\'' +
                    ", position=" + position +
                    ", links=[" + links.stream()
                    .map(x -> x.name)
                    .collect(Collectors.joining(", ")) +
                    "]}";
        }
    }

    static class NavStep implements Comparable<NavStep> {
        private final NavNode current;
        private NavStep previous;
        private final float costFromCurrentToTarget;

        public NavStep(NavNode current, float costFromCurrentToTarget) {
            this.current = current;
            this.costFromCurrentToTarget = costFromCurrentToTarget;
        }

        public NavStep(NavNode current, NavStep previous, float costFromCurrentToTarget) {
            this.current = current;
            this.previous = previous;
            this.costFromCurrentToTarget = costFromCurrentToTarget;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NavStep navStep = (NavStep) o;

            if (!Objects.equals(current, navStep.current)) return false;
            return Objects.equals(previous, navStep.previous);
        }

        private float cost() {
            if (this.previous == null) {
                return costFromCurrentToTarget;
            }
            return previous.current.position.dst2(current.position) + costFromCurrentToTarget;
        }

        @Override
        public int hashCode() {
            return Objects.hash(current, previous);
        }

        @Override
        public int compareTo(NavStep o) {
            return Float.compare(this.cost(), o.cost());
        }
    }

    /**
     * Returns if a character would not collide with the world at <i>position</i>.
     * @param position a coordinate in world space
     * @return whether it's walkable
     */
    public boolean inBounds(Vector2 position) {
        Vector2 goalInPixelSpace = map.gameSpaceToPixelSpace(position);
        int u = Math.round(goalInPixelSpace.x);
        int v = Math.round(goalInPixelSpace.y);
        // And get the alpha
        return (map.collisionPixmap.getPixel(u, v) & 0x000F) != 0x000F;
    }

    public List<Vector2> findPathTo(Vector2 from, Vector2 target) {
        /*
         * Pathfinding in a nutshell:
         * - First, find the nearest navnode
         * - Then, find paths between navnodes to the nearest to the goal
         * - Then, link to the goal
         */
        NavNode nearestToStart = null;
        float nTSDist = Float.MAX_VALUE;
        for (NavNode test : map.navMesh.values()) {
            float dist = from.dst2(test.position);
            if (dist < nTSDist) {
                nearestToStart = test;
                nTSDist = dist;
            }
        }
        assert nearestToStart != null;

        NavNode nearestToTarget = null;
        float nTTDist = Float.MAX_VALUE;
        for (NavNode test : map.navMesh.values()) {
            float dist = target.dst2(test.position);
            if (dist < nTTDist) {
                nearestToTarget = test;
                nTTDist = dist;
            }
        }
        assert nearestToTarget != null;

        // Avoid doing A* if we don't need to
        if (map.navMesh.get(nearestToStart.name).links.contains(nearestToTarget)) {
            return new ArrayList<>(Arrays.asList(from, nearestToStart.position, nearestToTarget.position, target));
        }

        // Now we do good old A* pathfinding between the nearests
        Queue<NavStep> openSet = new PriorityQueue<>();
        openSet.add(new NavStep(nearestToStart, nearestToStart.position.dst2(target)));
        Set<NavNode> closedSet = new HashSet<>();
        // And do Things
        while (!openSet.isEmpty()) {
            NavStep step = openSet.poll();
            if (step.current.equals(nearestToTarget)) {
                // Found path, reconstruct it
                List<Vector2> path = new ArrayList<>();
                path.add(target);
                NavStep current = step;
                do {
                    path.add(0, current.current.position);
                    current = current.previous;
                } while (current != null);
                return path;
            } else {
                // Explore our neighbours
                for (NavNode link : step.current.links) {
                    if (!closedSet.contains(link)) {
                        openSet.add(new NavStep(
                                link,
                                step,
                                link.position.dst2(target)
                        ));
                    }
                }
                closedSet.add(step.current);
            }
        }

        throw new IllegalStateException("no route found");
    }
}
