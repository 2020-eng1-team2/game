package marlin.auber.components;

import marlin.auber.common.Component;

/**
 * A component for entities that can be moved by WASD.
 */
public class KeyboardMovement extends Component {
    public boolean frozen = false;
    public final float movementSpeed;

    public KeyboardMovement(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
