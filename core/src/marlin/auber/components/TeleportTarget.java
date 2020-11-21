package marlin.auber.components;

import marlin.auber.common.Component;

/**
 * A location that the player can teleport from and to.
 *
 * Entities with this component MUST also have {@link Position}.
 */
public class TeleportTarget extends Component {
    public float activationRange;

    public TeleportTarget(float activationRange) {
        this.activationRange = activationRange;
    }
}
