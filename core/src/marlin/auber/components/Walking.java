package marlin.auber.components;

import marlin.auber.common.Component;

public class Walking extends Component {
    public enum WalkDirection {
        IDLE,
        LEFT,
        RIGHT
    }
    public WalkDirection direction = WalkDirection.IDLE;
}
