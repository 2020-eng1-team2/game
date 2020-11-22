package marlin.auber.components;

import marlin.auber.common.Component;

/**
 * A location that the player can find a keypad.
 *
 * Entities with this component MUST also have {@link Position}.
 */
public class KeypadTarget extends Component {
    public float activationRange;
    public boolean isBroken = false;

    public KeypadTarget(float activationRange) {
        this.activationRange = activationRange;
    }

    public void breakPad() {
        this.isBroken = true;
    }

    public void fixPad() {
        this.isBroken = false;
    }
}
