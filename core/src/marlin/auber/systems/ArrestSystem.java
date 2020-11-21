package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.*;
import marlin.auber.models.World;

public class ArrestSystem implements System {
    private Viewport cam = World.getWorld().viewport;

    private float wWidth;
    private float wHeight;
    private float sHeight;
    private float sWidth;

    public float ARREST_BEAM_RANGE = 6.0f;

    public void tick() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
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
            if (new Vector2().dst2(gsDelta) <= Math.pow(ARREST_BEAM_RANGE, 2)) {
                for (Entity ent : Entity.getAllEntitiesWithComponents(NPCAI.class)) {
                    // Check if click hits NPC
                    Position pos = ent.getComponent(Position.class);
                    if (clickPos.x <= pos.position.x + ent.getComponent(AABB.class).size.x && clickPos.x >= pos.position.x) {
                        // x is in bounds of NPC
                        if (clickPos.y <= pos.position.y + ent.getComponent(AABB.class).size.y && clickPos.y >= pos.position.y) {
                            // y is in bounds of NPC
                            // Arrest NPC
                            arrest(ent, new Vector2((550f / 64f), 71f - (355f / 32f)));
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Send entity to prison cell and change its navigation mesh to the prisoner mesh
     * @param ent Entity which has been arrested
     * @param prison Vector2 (position) of the cell
     */
    private void arrest(Entity ent, Vector2 prison) {
        // TODO: Arrest function (respawn in cell (No movement)/Teleport to cell (No movement))
        ent.getComponent(Position.class).position = prison;
    }
}
