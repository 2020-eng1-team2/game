package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.ActivePlayerCharacter;
import marlin.auber.components.Health;
import marlin.auber.components.Position;
import marlin.auber.models.World;

public class HealthSystem implements System {
    private float healCooldown = 5f;

    public void tick() {
        Entity player = Entity
                .getAllEntitiesWithComponents(Health.class)
                .get(0);
        if (player.getComponent(Health.class).gameOver()){
            // TODO: Game Over (return to menu?)
        }
        Gdx.app.log("health", Float.toString(player.getComponent(Health.class).getHealth()));
        if (World.getWorld().map.healPoint.dst2(player.getComponent(Position.class).position) <= Math.pow(6, 2)) {
            // TODO: Heal distance
            if (Gdx.input.isKeyJustPressed(Input.Keys.F) && player.getComponent(ActivePlayerCharacter.class).healCooldown.isOver()) {
                player.getComponent(Health.class).resetHealth();
                player.getComponent(ActivePlayerCharacter.class).healCooldown.reset(healCooldown);
            }
        }
        // For debug uses
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            player.getComponent(Health.class).decreaseHealth(5f);
        }
    }
}
