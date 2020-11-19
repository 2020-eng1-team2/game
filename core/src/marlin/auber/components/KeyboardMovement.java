package marlin.auber.components;

import marlin.auber.common.Component;

public class KeyboardMovement extends Component {
    public boolean frozen = false;
    public final float movementSpeed;

    public KeyboardMovement(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
