package marlin.auber.components;

import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Component;
import marlin.auber.common.Timer;

import java.util.List;

/**
 * NPC artificial intelligence.
 */
public class NPCAI extends Component {
    public float movementSpeed;
    public NPCAI(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public enum State {
        WALKING,
        STANDING_AROUND
    }

    public State state = State.STANDING_AROUND;
    public Timer standingAroundTimer = Timer.createTimer(1f);

    public Vector2 target;
    public List<Vector2> path;
    public Vector2 next;
}
