package marlin.auber.components;

import marlin.auber.common.Component;

public class TeleportTarget extends Component {
    public float activationRange;

    public TeleportTarget(float activationRange) {
        this.activationRange = activationRange;
    }
}
