package marlin.auber.components;

import marlin.auber.common.Component;

public class SpeedAbility extends Component {
    public void toggleAbility() {
        float defaultSpeed = this.parent.getComponent(NPCAI.class).movementSpeed;
        if (this.parent.getComponent(NPCAI.class).movementSpeed == defaultSpeed) {
            // Speed up
            this.parent.getComponent(NPCAI.class).movementSpeed = defaultSpeed + 1f;
        }
        else {
            // Slow down
            this.parent.getComponent(NPCAI.class).movementSpeed = defaultSpeed;
        }
    }
}
