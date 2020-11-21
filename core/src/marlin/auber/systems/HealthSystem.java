package marlin.auber.systems;

import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.Health;

public class HealthSystem implements System {
    public void tick() {
        Entity player = Entity
                .getAllEntitiesWithComponents(Health.class)
                .get(0);
        if (player.getComponent(Health.class).gameOver()){
            // TODO: Game Over (return to menu?)
        }
    }
}
