package marlin.auber.systems;

import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.Health;
import marlin.auber.components.Position;
import marlin.auber.models.World;

public class KeypadSystem implements System {
    public void tick() {
        Entity player = Entity
                .getAllEntitiesWithComponents(Health.class)
                .get(0);
        for (Vector2 kp : World.getWorld().map.keypads) {
            if (kp.dst2(player.getComponent(Position.class).position) <= Math.pow(6, 2)) {
                // TODO: Keypad Component, similar to teleport pad
            }
        }
    }
}
