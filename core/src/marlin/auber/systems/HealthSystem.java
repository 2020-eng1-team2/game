package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import marlin.auber.common.Assets;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.ActivePlayerCharacter;
import marlin.auber.components.Health;
import marlin.auber.components.Position;
import marlin.auber.models.World;

public class HealthSystem implements System {
    private final SpriteBatch guiBatch = new SpriteBatch();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private float healCooldown = 5f;

    public void tick() {
        if (!guiBatch.isDrawing()) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            guiBatch.begin();
        }
        Entity player = Entity
                .getAllEntitiesWithComponents(Health.class)
                .get(0);
        if (player.getComponent(Health.class).gameOver()){
            // TODO: Game Over (return to menu?)
        }
        // TODO: Heal distance
        if (World.getWorld().map.healPoint.dst2(player.getComponent(Position.class).position) <= Math.pow(1, 2)) {
            if (!player.getComponent(ActivePlayerCharacter.class).healCooldown.isOver()) {
                Assets.fonts.cnr.draw(
                        guiBatch,
                        String.format("Healing recharged in %.1f", player.getComponent(ActivePlayerCharacter.class).healCooldown.getRemaining()),
                        50, 50
                );
            }
            else {
                Assets.fonts.cnr.draw(
                        guiBatch,
                        "Press F to heal",
                        50, 50
                );
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F) && player.getComponent(ActivePlayerCharacter.class).healCooldown.isOver()) {
                player.getComponent(Health.class).resetHealth();
                player.getComponent(ActivePlayerCharacter.class).healCooldown.reset(healCooldown);
            }
        }
        else {
            Assets.fonts.cnr.draw(
                    guiBatch,
                    "Health: " + (int) player.getComponent(Health.class).getHealth(),
                    50, 50
            );
        }
        // For debug uses
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            player.getComponent(Health.class).decreaseHealth(5f);
        }
        // Draw Health bar
        int ssX = Gdx.graphics.getWidth();
        int ssH = Gdx.graphics.getHeight();
        if (guiBatch.isDrawing()) {
            guiBatch.end();
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(ssX / 40f, ssH - ((ssH / 20f) * 3f), ssX / 5f * (player.getComponent(Health.class).getHealth() / player.getComponent(Health.class).getMaxHealth()), ((ssH / 20f) * 2f));
        shapeRenderer.end();
    }
}
