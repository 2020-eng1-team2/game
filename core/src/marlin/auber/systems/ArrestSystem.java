package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import marlin.auber.common.Assets;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.*;
import marlin.auber.models.World;

public class ArrestSystem implements System {
    private Viewport cam = World.getWorld().viewport;
    private final SpriteBatch guiBatch = new SpriteBatch();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private Entity arrestingEntity;
    private boolean arresting = false;
    private boolean beginTimer = false;

    private final float beamTime = 1f;

    private float wWidth;
    private float wHeight;
    private float sHeight;
    private float sWidth;

    /**
     * Used to set the reload timer once when wither reload button is pressed, or ammo goes down to zero.
     */
    private boolean beginReload = false;

    /**
     * Used once when the reload timer reaches zero to reset the beam count of the entity.
     */
    private boolean reloadBeams = false;

    public float reloadTime = 4f;
    public float ARREST_BEAM_RANGE = 6.0f;

    public void tick() {
        if (!guiBatch.isDrawing()) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            guiBatch.begin();
        }
        // TODO: Add reload button
        ActivePlayerCharacter player = Entity
                .getAllEntitiesWithComponents(ActivePlayerCharacter.class)
                .get(0)
                .getComponent(ActivePlayerCharacter.class);
        ArrestBeam arrestBeam = Entity
                .getAllEntitiesWithComponents(ArrestBeam.class)
                .get(0)
                .getComponent(ArrestBeam.class);

        wWidth = cam.getWorldWidth();
        wHeight = cam.getWorldHeight();
        sHeight = (float) cam.getScreenHeight();
        sWidth = (float) cam.getScreenWidth();

        // Calculate middle of screen
        Vector2 middle = new Vector2(sWidth*0.5f, sHeight*0.5f);
        // Auber position in world space
        Vector2 auber = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).getComponent(Position.class).position;
        // Get Mouse position in screen space
        Vector2 click = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        // Find difference between click position and middle of the screen
        Vector2 delta = new Vector2(click).sub(middle);
        // Convert delta to game space
        Vector2 gsDelta = new Vector2((this.wWidth / this.sWidth) * delta.x, (this.wHeight / this.sHeight) * delta.y);
        // Add delta to auber position
        Vector2 clickPos = new Vector2(auber).add(gsDelta);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && arrestBeam.beamsLeft() != 0) {
            arrestBeam.shootBeam();
            Gdx.app.log("beams left", Integer.toString(arrestBeam.beamsLeft()));
            if (new Vector2().dst2(gsDelta) <= Math.pow(ARREST_BEAM_RANGE, 2)) {
                for (Entity ent : Entity.getAllEntitiesWithComponents(NPCAI.class)) {
                    // Check if click hits NPC
                    Position pos = ent.getComponent(Position.class);
                    if (clickPos.x <= pos.position.x + ent.getComponent(AABB.class).size.x && clickPos.x >= pos.position.x) {
                        // x is in bounds of NPC
                        if (clickPos.y <= pos.position.y + ent.getComponent(AABB.class).size.y && clickPos.y >= pos.position.y) {
                            // y is in bounds of NPC
                            // Begin arresting NPC
                            arrestingEntity = ent;
                            arresting = true;
                            beginTimer = true;
                            // Can only arrest one NPC per beam
                            break;
                        }
                    }
                }
            }
        }
        else if (reloadBeams && player.reload.isOver()) {
            arrestBeam.reloadBeam();
            reloadBeams = false;
        }
        else if (arrestBeam.beamsLeft() == 0 && player.reload.isOver()) {
            beginReload = true;
        }
        else if (!player.reload.isOver()) {
            // TODO: Fix reloading text
            Assets.fonts.cnr.draw(
                    guiBatch,
                    "Reloading...",
                    50, 50
            );
            reloadBeams = true;
        }
        else if (arresting == true) {
            if (beginTimer) {
                player.beamTime.reset(beamTime);
                beginTimer = false;
            }
            Vector2 entPosition = arrestingEntity.getComponent(Position.class).position;
            Vector2 entSize = arrestingEntity.getComponent(AABB.class).size;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (clickPos.x <= entPosition.x + entSize.x && clickPos.x >= entPosition.x) {
                    // x is in bounds of NPC
                    if (clickPos.y <= entPosition.y + entSize.y && clickPos.y >= entPosition.y) {
                        // y is in bounds of NPC
                        if (player.beamTime.isOver()) {
                            // Arrest that man at once!
                            Gdx.app.log("arrest", "lock him away");
                            arresting = false;
                            arrest(arrestingEntity, new Vector2((550f / 64f), 71f - (355f / 32f)));
                        }
                        else {
                            if (guiBatch.isDrawing()) {
                                guiBatch.end();
                            }
                            // Draw beaming bar
                            shapeRenderer.setProjectionMatrix(World.getWorld().viewport.getCamera().combined);
                            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                            shapeRenderer.setColor(0, 1, 0, 1);
                            shapeRenderer.rect(entPosition.x, entPosition.y + entSize.y * 1.05f, entSize.x - (entSize.x * (player.beamTime.getRemaining() / beamTime)), entSize.y / 10f);
                            shapeRenderer.end();
                            // End Beaming bar
                            if (!guiBatch.isDrawing()) {
                                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                                guiBatch.begin();
                            }
                        }
                    }
                }
            }
            else {
                arresting = false;
                player.beamTime.reset(0f);
            }
        }
        if (beginReload) {
            player.reload.reset(reloadTime);
            beginReload = false;
        }
        if (guiBatch.isDrawing()) {
            guiBatch.end();
        }
    }

    /**
     * Send entity to prison cell and change its navigation mesh to the prisoner mesh
     * @param ent Entity which has been arrested
     * @param prison Vector2 (position) of the cell
     */
    private void arrest(Entity ent, Vector2 prison) {
        // TODO: Arrest function (respawn in cell (No movement)/Teleport to cell (No movement)) and change navigation mesh
        // Destroy navigation entity and give it the prison one
        ent.removeComponent(NPCAI.class);
        ent.attachComponent(new CellNPCAI(3.0f));
        ent.getComponent(Position.class).position = prison;
    }
}