package marlin.auber.components;

import marlin.auber.common.Component;

public class SpeedAbility extends Component {
    public void toggleAbility() {
        // late bug fix VV
        float defaultSpeed = 3f;
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
