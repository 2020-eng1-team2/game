package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Assets;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.ActivePlayerCharacter;
import marlin.auber.components.Health;
import marlin.auber.components.KeypadTarget;
import marlin.auber.components.Position;
import marlin.auber.models.World;

public class KeypadSystem implements System {
    private final SpriteBatch guiBatch = new SpriteBatch();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private boolean fixingPad = false;
    private boolean beginFixing = false;
    private Entity kpEntity;
    private final float fixTime = 3f;
    public void tick() {
        // TODO: Not working full, will need tidy up once game logic is implemented
        /**
         * Player is not able to interact with keypad unless it is broken {@link KeypadTarget}.
         */
        if (!guiBatch.isDrawing()) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            guiBatch.begin();
        }
        Entity player = Entity
                .getAllEntitiesWithComponents(Health.class)
                .get(0);
        if (!beginFixing) {
            for (Entity kp : Entity.getAllEntitiesWithComponents(KeypadTarget.class)) {
                if (kp.getComponent(KeypadTarget.class).isBroken) {
                    if (guiBatch.isDrawing()) {
                        guiBatch.end();
                    }
                    Vector2 entPosition = kp.getComponent(Position.class).position;
                    Vector2 size = new Vector2(1f, 1f);
                    // Draw warning
                    shapeRenderer.setProjectionMatrix(World.getWorld().viewport.getCamera().combined);
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(1, 0, 0, 1);
                    shapeRenderer.rect(entPosition.x, entPosition.y + size.y * 1.05f, size.x - (size.x * (player.getComponent(ActivePlayerCharacter.class).keypadTime.getRemaining() / fixTime)), size.y / 10f);
                    shapeRenderer.end();
                    if (!guiBatch.isDrawing()) {
                        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                        guiBatch.begin();
                    }
                    if (kp.getComponent(Position.class).position.dst2(player.getComponent(Position.class).position) <= Math.pow(kp.getComponent(KeypadTarget.class).activationRange, 2)) {
                        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
                            kpEntity = kp;
                            beginFixing = true;
                            player.getComponent(ActivePlayerCharacter.class).keypadTime.reset(fixTime);
                            break;
                        } else {
                            fixingPad = false;
                            Assets.fonts.cnr.draw(
                                    guiBatch,
                                    "Hold F to Fix Pad",
                                    50, 50
                            );
                        }
                    }
                }
            }
        }
        else if (beginFixing) {
                if (guiBatch.isDrawing()) {
                    guiBatch.end();
                }
                Vector2 entPosition = kpEntity.getComponent(Position.class).position;
                Vector2 size = new Vector2(1f, 1f);
                // Draw fixing bar
                shapeRenderer.setProjectionMatrix(World.getWorld().viewport.getCamera().combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0, 1, 0, 1);
                shapeRenderer.rect(entPosition.x, entPosition.y + size.y * 1.05f, size.x - (size.x * (player.getComponent(ActivePlayerCharacter.class).keypadTime.getRemaining() / fixTime)), size.y / 10f);
                shapeRenderer.end();
                // End Beaming bar
                if (!guiBatch.isDrawing()) {
                    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    guiBatch.begin();
                }
                if (player.getComponent(ActivePlayerCharacter.class).keypadTime.isOver()) {
                    // Keypad Fixed
                    kpEntity.getComponent(KeypadTarget.class).fixPad();
                    Gdx.app.log("kp", "kp fixed!!!");
                    beginFixing = false;
                    fixingPad = false;
                }
                else {
                    Assets.fonts.cnr.draw(
                            guiBatch,
                            "Fixing...",
                            50, 50
                    );
                }
            }
            if (guiBatch.isDrawing()) {
                guiBatch.end();
            }
        }
    }
