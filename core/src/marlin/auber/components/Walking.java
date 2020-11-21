package marlin.auber.components;

import marlin.auber.common.Component;

/**
 * Stores the state of a walking character to use in conjunction with {@link WalkingRenderer}.
 */
public class Walking extends Component {
    public enum WalkDirection {
        IDLE,
        LEFT,
        RIGHT
    }
    public WalkDirection direction = WalkDirection.IDLE;
}
