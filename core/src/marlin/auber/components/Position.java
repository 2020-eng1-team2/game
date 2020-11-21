package marlin.auber.components;

import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Component;

/**
 * A component for a thing in-game that has a 2d position.
 */
public class Position extends Component {
    public Vector2 position;

    /**
     * Create a Position component, <b>copying</b> <i>Position</i>
     * @param position initial position
     */
    public Position(Vector2 position) {
        this.position = new Vector2(position);
    }

    public Position() {
        this(Vector2.Zero);
    }

}
