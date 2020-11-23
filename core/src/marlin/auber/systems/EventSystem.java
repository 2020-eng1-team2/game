package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Component;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.*;
import marlin.auber.models.World;

import java.util.List;
import java.util.Random;

public class EventSystem implements System {

    /**
     * True if the infiltrator has been arrested
     */
    private boolean infilArrested = false;

    /**
     * True if the keypad is fixed
     */
    private boolean keypadFixed = false;

    /**
     * Runs code to being an event once
     */
    private boolean startEvent = false;

    private boolean eventPart1 = false;

    private List<Component> abilities;

    public EventSystem() {
        abilities.add(new SpeedAbility());
        abilities.add(new InvisAbility());
        abilities.add(new StunAbility());
    }

    public void tick() {
        ActivePlayerCharacter player = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).getComponent(ActivePlayerCharacter.class);
        // Update event status
        updateArrests();
        updateKeypad();
        // Check is event is happening
        if (noEvent()) {
            // No event is happening, start event
            this.startEvent = true;
        }
        else if (player.meltdownTime.isOver()) {
            // Player lost, ship destroyed. End game
            // TODO: Game over
        }
        if (this.startEvent) {
            this.startEvent = false;
            this.eventPart1 = true;
            startKeypadEvent();
            // When keypad event ends, start infiltrator event
        }
        if (eventPart1 == true) {
            this.eventPart1 = false;
            startInfiltratorEvent();
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

    private void startKeypadEvent() {
        // RNG pick a keypad and break it
        Random random = new Random();
        int rng = random.nextInt(World.getWorld().map.keypads.size());
        Entity.getAllEntitiesWithComponents(KeypadTarget.class).get(rng).getComponent(KeypadTarget.class).breakPad();
    }

    private void startInfiltratorEvent() {
        // Spawn in infiltrator away from Auber
        Vector2 max = Vector2.Zero;
        Vector2 auber = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).getComponent(Position.class).position;
        for (Vector2 pos : World.getWorld().map.keypads) {
            if (pos.dst2(auber) > max.dst2(auber)) {
                max = pos;
            }
        }
        // Generate random ability
        Random random = new Random();
        int rng = random.nextInt(3);
        Entity.create(
                "Infiltrator",
                new Position(max),
                new AABB(1.8f, 1.8f, AABB.TAG_RENDER | AABB.TAG_COLLISION_X_ONLY),
                new Walking(),
                new NPCAI(3.0f),
                new Renderer(8),
                new StaticRenderer(
                        new Texture(Gdx.files.internal("testChar2.png"))
                ),
                new Infiltrator(),
                abilities.get(rng)
        );
    }
}
