package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import marlin.auber.common.Assets;
import marlin.auber.common.Entity;
import marlin.auber.common.Resetable;
import marlin.auber.common.System;
import marlin.auber.components.ActivePlayerCharacter;
import marlin.auber.components.Health;
import marlin.auber.components.Position;
import marlin.auber.models.World;

import java.util.List;

public class HealthSystem implements System, Resetable {
    private final SpriteBatch guiBatch = new SpriteBatch();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final GlyphLayout layout = new GlyphLayout();
    private float healCooldown = 5f;

    private boolean gameOver = false;

    public void tick() {
        if (!guiBatch.isDrawing()) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            guiBatch.begin();
        }
        Entity player = Entity
                .getAllEntitiesWithComponents(Health.class)
                .get(0);
        if (player.getComponent(Health.class).gameOver()){
            gameOver = true;
        }
        if (World.getWorld().map.healPoint.dst2(player.getComponent(Position.class).position) <= Math.pow(1, 2)) {
            if (!player.getComponent(ActivePlayerCharacter.class).healCooldown.isOver()) {
                layout.setText(Assets.fonts.cnr, String.format("Healing recharged in %.1f", player.getComponent(ActivePlayerCharacter.class).healCooldown.getRemaining()));
                float height = layout.height;
                Assets.fonts.cnr.setColor(0, 0, 0, 1);
                Assets.fonts.cnr.draw(
                        guiBatch,
                        String.format("Healing recharged in %.1f", player.getComponent(ActivePlayerCharacter.class).healCooldown.getRemaining()),
                        50 - 2, Gdx.graphics.getHeight() - (height * 1.5f) - 2
                );
                Assets.fonts.cnr.setColor(1, 1, 1, 1);
                Assets.fonts.cnr.draw(
                        guiBatch,
                        String.format("Healing recharged in %.1f", player.getComponent(ActivePlayerCharacter.class).healCooldown.getRemaining()),
                        50, Gdx.graphics.getHeight() - (height * 1.5f)
                );
            }
            else {
                layout.setText(Assets.fonts.cnr, "Press F to heal");
                float height = layout.height;
                Assets.fonts.cnr.setColor(0, 0, 0, 1);
                Assets.fonts.cnr.draw(
                        guiBatch,
                        "Press F to heal",
                        50 - 2, Gdx.graphics.getHeight() - (height * 1.5f) - 2
                );
                Assets.fonts.cnr.setColor(1, 1, 1, 1);
                Assets.fonts.cnr.draw(
                        guiBatch,
                        "Press F to heal",
                        50, Gdx.graphics.getHeight() - (height * 1.5f)
                );
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F) && player.getComponent(ActivePlayerCharacter.class).healCooldown.isOver()) {
                player.getComponent(Health.class).resetHealth();
                player.getComponent(ActivePlayerCharacter.class).healCooldown.reset(healCooldown);
            }
        }
        else {
            layout.setText(Assets.fonts.cnr, "Health: " + (int) player.getComponent(Health.class).getHealth());
            float height = layout.height;
            Assets.fonts.cnr.setColor(0, 0, 0, 1);
            Assets.fonts.cnr.draw(
                    guiBatch,
                    "Health: " + (int) player.getComponent(Health.class).getHealth(),
                    50 - 2, Gdx.graphics.getHeight() - (height * 1.5f) - 2
            );
            Assets.fonts.cnr.setColor(1, 1, 1, 1);
            Assets.fonts.cnr.draw(
                    guiBatch,
                    "Health: " + (int) player.getComponent(Health.class).getHealth(),
                    50, Gdx.graphics.getHeight() - (height * 1.5f)
            );
        }
        if (guiBatch.isDrawing()) {
            guiBatch.end();
        }
    }

    public void reset() {
        this.gameOver = false;
        List<Entity> playerMaybe = Entity
                .getAllEntitiesWithComponents(Health.class);
        if (playerMaybe.size() > 0) {
            playerMaybe.get(0).getComponent(Health.class).resetHealth();
        }
    }

    // if there is a bug, this may be causing it
    public boolean isGameOver() {
        return this.gameOver;
    }
}
