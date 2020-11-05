package marlin.auber.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import marlin.auber.common.DebugRenderer;

import java.util.*;
import java.util.stream.Collectors;

public class Map implements Json.Serializable, DebugRenderer {
    public Texture mapTexture;
    public Texture topCoatTexture;
    public Texture collisionTexture;

    private Pixmap collisionPixmap;

    public float width;
    public float height;

    public Vector2 auberSpawn;

    public List<Vector2> teleportPads = new ArrayList<>();
    public static final float TELEPORT_PAD_USE_RANGE = 2f;

    private static class NavNode {
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

    private static class NavStep implements Comparable<NavStep> {
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

    private final java.util.Map<String, NavNode> navMesh = new HashMap<>();

    /**
     * Returns if a character would not collide with the world at <i>position</i>.
     * @param position a coordinate in world space
     * @return whether it's walkable
     */
    public boolean inBounds(Vector2 position) {
        Vector2 goalInPixelSpace = this.gameSpaceToPixelSpace(position);
        int u = Math.round(goalInPixelSpace.x);
        int v = Math.round(goalInPixelSpace.y);
        // And get the alpha
        return (collisionPixmap.getPixel(u, v) & 0x000F) != 0x000F;
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
        for (NavNode test : this.navMesh.values()) {
            float dist = from.dst2(test.position);
            if (dist < nTSDist) {
                nearestToStart = test;
                nTSDist = dist;
            }
        }
        assert nearestToStart != null;

        NavNode nearestToTarget = null;
        float nTTDist = Float.MAX_VALUE;
        for (NavNode test : this.navMesh.values()) {
            float dist = target.dst2(test.position);
            if (dist < nTTDist) {
                nearestToTarget = test;
                nTTDist = dist;
            }
        }
        assert nearestToTarget != null;

        // Avoid doing A* if we don't need to
        if (navMesh.get(nearestToStart.name).links.contains(nearestToTarget)) {
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

    public Vector2 pixelSpaceToGameSpace(Vector2 pixel) {
        return pixelSpaceToGameSpace(pixel.x, pixel.y);
    }

    public Vector2 pixelSpaceToGameSpace(float x, float y) {
        // note that pixel-space has its origin top-left, while world-space is bottom-left
        return new Vector2(
                (width / mapTexture.getWidth()) * x,
                (height / mapTexture.getHeight()) * (mapTexture.getHeight() - y)
        );
    }

    public Vector2 gameSpaceToPixelSpace(Vector2 game) {
        return gameSpaceToPixelSpace(game.x, game.y);
    }

    public Vector2 gameSpaceToPixelSpace(float x, float y) {
        // note that pixel-space has its origin top-left, while world-space is bottom-left
        return new Vector2(
                (mapTexture.getWidth() / width) * x,
                mapTexture.getHeight() - ((mapTexture.getHeight() / height) * y)
        );
    }

    public static Map loadMap(FileHandle file) {
        Json json = new Json();
        return json.fromJson(Map.class, file);
    }

    @Override
    public void write(Json json) {
        throw new RuntimeException("Don't serialize Map!");
    }

    @Override
    public void read(Json json, JsonValue val) {
        mapTexture = new Texture(Gdx.files.internal(val.getString("mapTexture")));
        topCoatTexture = new Texture(Gdx.files.internal(val.getString("topcoatTexture")));
        collisionTexture = new Texture(Gdx.files.internal(val.getString("collisionTexture")));

        TextureData texData = collisionTexture.getTextureData();
        texData.prepare();
        collisionPixmap = texData.consumePixmap();

        width = val.getFloat("width");
        height = val.getFloat("height");

        JsonValue auberSpawnLoc = val.get("auberSpawn");
        auberSpawn = pixelSpaceToGameSpace(
            auberSpawnLoc.getFloat(0),
            auberSpawnLoc.getFloat(1)
        );

        JsonValue navNodes = val.get("navNodes");
        for (JsonValue child = navNodes.child; child != null; child = child.next) {
            float[] pos = child.asFloatArray();
            NavNode node = new NavNode(child.name, pixelSpaceToGameSpace(
                    pos[0],
                    pos[1]
            ));
            this.navMesh.put(node.name, node);
        }

        JsonValue navLinks = val.get("navLinks");
        for (JsonValue link = navLinks.child; link != null; link = link.next) {
            NavNode source = this.navMesh.get(link.name);
            if (source == null) {
                throw new RuntimeException(String.format(
                        "Tried to build link from %s but it doesn't exist",
                        link.name
                ));
            }
            for (JsonValue child = link.child; child != null; child = child.next) {
                NavNode target = this.navMesh.get(child.asString());
                if (target == null) {
                    throw new RuntimeException(String.format(
                            "Tried to build link from %s to %s but the target doesn't exist",
                            link.name,
                            child.asString()
                    ));
                }
                source.links.add(target);
            }
        }

        JsonValue teleportPads = val.get("teleportPads");
        for (JsonValue child = teleportPads.child; child != null; child = child.next) {
            float[] pos = child.asFloatArray();
            this.teleportPads.add(
                    this.pixelSpaceToGameSpace(
                            pos[0],
                            pos[1]
                    )
            );
        }

    }

    @Override
    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.PINK);
        for (NavNode node : this.navMesh.values()) {
            for (NavNode link : node.links) {
                shapeRenderer.line(
                        node.position,
                        link.position
                );
            }
        }

        shapeRenderer.setColor(Color.ORANGE);
        for (Vector2 pad : this.teleportPads) {
            shapeRenderer.x(
                    pad,
                    TELEPORT_PAD_USE_RANGE
            );
        }
    }
}
