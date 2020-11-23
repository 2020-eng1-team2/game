package marlin.auber.systems;

import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.Infiltrator;
import marlin.auber.components.KeypadTarget;

public class EventSystem implements System {

    /**
     * True if the infiltrator has been arrested
     */
    private boolean infilArrested = false;

    /**
     * True if the keypad is fixed
     */
    private boolean keypadFixed = false;

    public void tick() {
        // Update event status
        updateArrests();
        updateKeypad();
        // Check is event is happening
        if (noEvent()) {
            // No event is happening, start event
        }
    }

    /**
     * Check if there is either a keypad that needs fixing or an infiltrator on the run
     * @return true if both have been sorted by the player
     */
    public boolean noEvent() {
        if (this.infilArrested && this.keypadFixed) {
            return true;
        }
        else {
            return false;
        }
    }

    private void updateArrests() {
        if (Entity.getAllEntitiesWithComponents(Infiltrator.class).size() <= 0) {
            // No Infiltrator on the run
            this.infilArrested = true;
        }
    }

    private void updateKeypad() {
        for (Entity ent : Entity.getAllEntitiesWithComponents(KeypadTarget.class)) {
            if (ent.getComponent(KeypadTarget.class).isBroken) {
                return;
            }
        }
        // No broken keypads on map
        this.keypadFixed = true;
    }
}
