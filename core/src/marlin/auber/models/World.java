package marlin.auber.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.*;
import java.util.stream.Collectors;

public class World {
    private static World instance;
    public Viewport viewport;

    public Map map;

    public boolean debugMode = true;

    private World(Map map) {
        this.map = map;
        this.viewport = new ExtendViewport(20, 20);
    }

    public static void init(Map map) {
        instance = new World(map);
    }

    public static World getWorld() {
        return instance;
    }

    public static class NavNode {
        public String name;
        public Vector2 position;
        public Set<NavNode> links;

        public NavNode(String name, Vector2 position) {
            this.name = name;
            this.position = position;
            this.links = new HashSet<>();
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

    private final Vector2 inBounds_temp_bl = new Vector2(0, 0);
    private final Vector2 inBounds_temp_tl = new Vector2(0, 0);
    private final Vector2 inBounds_temp_br = new Vector2(0, 0);
    private final Vector2 inBounds_temp_tr = new Vector2(0, 0);

    /**
     * Returns if an axis-aligned-bounding-box <i>aabb</i> with its bottom-left corner at <i>position</i>
     * would be in bounds in the world.
     * @param position the bottom-left corner of <i>aabb</i>
     * @param aabb an axis-aligned-bounding-box to test
     * @return whether <i>aabb</i> at <i>position</i> is in bounds
     */
    public boolean inBounds(Vector2 position, Vector2 aabb) {
        inBounds_temp_bl.set(position.x, position.y);
        inBounds_temp_br.set(position.x + aabb.x, position.y);
        inBounds_temp_tl.set(position.x, position.y + aabb.y);
        inBounds_temp_tr.set(position.x+ aabb.x, position.y + aabb.y);
        return inBounds(inBounds_temp_bl)
                && inBounds(inBounds_temp_br)
                && inBounds(inBounds_temp_tl)
                && inBounds(inBounds_temp_tr);
    }

    /**
     * Find a path through the map from <i>from</i> to <i>to</i>.
     *
     * Uses A* pathfinding through the map's navigation nodes.
     * @param from the starting point
     * @param target the end goal
     * @param navigationMesh the navigation mesh being used
     * @return a list of points to walk through
     */
    public List<Vector2> findPathTo(Vector2 from, Vector2 target, java.util.Map<String, World.NavNode> navigationMesh) {
        /*
         * Pathfinding in a nutshell:
         * - First, find the nearest navnode
         * - Then, find paths between navnodes to the nearest to the goal
         * - Then, link to the goal
         */
        NavNode nearestToStart = null;
        float nTSDist = Float.MAX_VALUE;
        for (NavNode test : navigationMesh.values()) {
            float dist = from.dst2(test.position);
            if (dist < nTSDist) {
                nearestToStart = test;
                nTSDist = dist;
            }
        }
        assert nearestToStart != null;

        NavNode nearestToTarget = null;
        float nTTDist = Float.MAX_VALUE;
        for (NavNode test : navigationMesh.values()) {
            float dist = target.dst2(test.position);
            if (dist < nTTDist) {
                nearestToTarget = test;
                nTTDist = dist;
            }
        }
        assert nearestToTarget != null;

        // Avoid doing A* if we don't need to
        if (navigationMesh.get(nearestToStart.name).links.contains(nearestToTarget)) {
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
