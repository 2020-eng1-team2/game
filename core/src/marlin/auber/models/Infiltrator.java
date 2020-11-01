package marlin.auber.models;

import com.badlogic.gdx.math.Vector2;

public class Infiltrator {
    public World world;
    public Vector2 position;

    /**
     * Infiltrator's movement speed, in metres (in-game units) *per second* (not per frame!)
     */
    public float movementSpeed = 3f;

    public static final float WIDTH = 1.0f;
    public static final float HEIGHT = 1.8f;

    public Infiltrator(World world) {
        this.world = world;
        this.position = new Vector2(37, 47);
    }
}
