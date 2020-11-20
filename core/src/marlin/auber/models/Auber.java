package marlin.auber.models;

import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Timer;

public class Auber {
    public World world;
    public Vector2 position;

    public enum WalkDirection {
        IDLE,
        LEFT,
        RIGHT
    }

    public WalkDirection walkDirection = WalkDirection.IDLE;

    /**
     * Auber's movement speed, in metres (in-game units) *per second* (not per frame!)
     */
    public float movementSpeed = 3f;

    public static final float WIDTH = (883f/637f);
    public static final float HEIGHT = 2.25f;

    private float health = 100f;

    public static final float TELEPORT_COOLDOWN = 5f;
    public Timer teleportCooldown = Timer.createTimer(0f);

    public Auber(World world) {
        this.world = world;
        this.position = new Vector2(world.map.auberSpawn);
    }

    public float getHealth(){
        return this.health;
    }

    public void decrementHealth(float damage){
        this.health -= damage;
    }

    public void resetHealth(){
        this.health = 100f;
    }

    public boolean canTeleport() {
        return this.teleportCooldown.getRemaining() == 0f;
    }

    public void teleport(Vector2 to) {
        if (!this.canTeleport()) {
            return;
        }
        this.position.set(to);
        this.teleportCooldown.reset(TELEPORT_COOLDOWN);
    }
}