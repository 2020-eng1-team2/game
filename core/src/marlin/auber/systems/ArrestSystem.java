package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.*;
import marlin.auber.models.World;

public class ArrestSystem implements System {
    private Vector2 middle = new Vector2(Gdx.graphics.getWidth()*0.5f, Gdx.graphics.getHeight()*0.5f);

    public void tick() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Auber position in world space
            Vector2 auber = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).getComponent(Position.class).position;
            // Get Mouse position in screen space
            Vector2 click = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            // Find difference between click position and middle of the screen and convert to world space
            Vector2 delta = ps2gs(new Vector2(click).sub(middle));
            // Add delta to auber position
            Vector2 clickPos = new Vector2(auber).add(delta);
            for (Entity ent : Entity.getAllEntitiesWithComponents(NPCAI.class)) {
                // Check if click hits NPC
                Position pos = ent.getComponent(Position.class);
                // TODO: Fix hit detection
                if (clickPos.x <= pos.position.x + ent.getComponent(AABB.class).size.x && clickPos.x >= pos.position.x) {
                    // x is in bounds of NPC
                    if (clickPos.y <= pos.position.y + ent.getComponent(AABB.class).size.y && clickPos.y >= pos.position.y) {
                        // y is in bounds of NPC
                        // Arrest NPC
                        //arrest(ent);
                        Gdx.app.log("coords", clickPos.toString());
                    }
                }
            }
        }
    }

    private void arrest(Entity ent) {
        // TODO: Arrest function (respawn in cell (No movement)/Teleport to cell (No movement))
    }

    private Vector2 ps2gs(Vector2 ps) {
        // Pixel to Game space function (World.map.pixelToGameSpace() doesn't work with this)
        return new Vector2(
                (World.getWorld().map.width / World.getWorld().map.mapTexture.getWidth()) * ps.x,
                (World.getWorld().map.height / World.getWorld().map.mapTexture.getHeight()) * ps.y
        );
    }
}
