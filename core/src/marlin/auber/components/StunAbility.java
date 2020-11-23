package marlin.auber.components;

import marlin.auber.common.Component;
import marlin.auber.common.Entity;

public class StunAbility extends Component {

    public void toggleAbility() {
        // Set Auber's movement speed to 0f
        Entity auber = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0);
        if (auber.getComponent(KeyboardMovement.class).movementSpeed == 0f) {
            // Set Auber's movement speed to 3f
            auber.getComponent(KeyboardMovement.class).movementSpeed = 3f;
        }
        else {
            // Set Auber's movement speed to 0f
            auber.getComponent(KeyboardMovement.class).movementSpeed = 0f;
            auber.getComponent(Health.class).decreaseHealth(10f);
        }
    }
}
