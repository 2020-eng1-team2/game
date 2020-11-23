package marlin.auber.components;

import marlin.auber.common.Component;

public class SpeedAbility extends Component {
    private float defaultSpeed = this.parent.getComponent(NPCAI.class).movementSpeed;

    public void toggleAbility() {
        if (this.parent.getComponent(NPCAI.class).movementSpeed == defaultSpeed) {
            // Speed up
            this.parent.getComponent(NPCAI.class).movementSpeed = defaultSpeed * 2f;
        }
        else {
            // Slow down
            this.parent.getComponent(NPCAI.class).movementSpeed = defaultSpeed;
        }
    }
}
