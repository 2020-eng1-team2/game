package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.*;
import marlin.auber.common.System;
import marlin.auber.components.*;
import marlin.auber.models.World;

import java.util.List;
import java.util.Random;

public class EventSystem implements System {

    /**
     * True if the infiltrator has been arrested, true by default
     */
    private boolean infilArrested = true;

    /**
     * True if the keypad is fixed, true by default
     */
    private boolean keypadFixed = true;

    /**
     * Runs code to being an event once
     */
    private boolean startEvent = false;

    private boolean eventPart1 = false;
    private boolean keypadLastFrame;
    private boolean infiltratorLastFrame;
    private boolean startGame = true;

    private final float meltdownTimer = 60f;
    private final float eventCooldownTimer = 15f;

    private final SpriteBatch guiBatch = new SpriteBatch();
    private final GlyphLayout layout = new GlyphLayout();

    ActivePlayerCharacter player;

    public void tick() {
        if (!guiBatch.isDrawing()) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            guiBatch.begin();
        }
        keypadLastFrame = keypadFixed;
        infiltratorLastFrame = infilArrested;
        ActivePlayerCharacter player = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).getComponent(ActivePlayerCharacter.class);
        if (startGame) {
            startGame = false;
            player.eventCooldown.reset(eventCooldownTimer);
        }
        // Update event status
        updateArrests();
        updateKeypad();
        if (!keypadLastFrame && keypadFixed) {
            // Keypad just fixed
            Gdx.app.log("kp", "kp fixed!!!");
            eventPart1 = true;
        }
        if (!infiltratorLastFrame && infilArrested) {
            // Infiltrator just arrested
            player.eventCooldown.reset(eventCooldownTimer);
        }
        // Check is event is happening
        if (noEvent() && player.eventCooldown.isOver()) {
            // No event is happening, start event
            this.startEvent = true;
        }
        else if (player.meltdownTime.isOver() && !noEvent()) {
            // Player lost, ship destroyed. End game
            // TODO: Game over
        }
        else if (!this.keypadFixed) {
            layout.setText(Assets.fonts.cnr, String.format("Time to meltdown: %.1f", player.meltdownTime.getRemaining()));
            float width = layout.width;
            float height = layout.height;
            Assets.fonts.cnr.draw(
                    guiBatch,
                    String.format("Time to meltdown: %.1f", player.meltdownTime.getRemaining()),
                    (Gdx.graphics.getWidth() * 0.5f) - (width * 0.5f), (Gdx.graphics.getHeight() - (height * 1.5f))
            );
        }
        else if (!this.infilArrested) {
            Gdx.app.log("infil", "infil needs to be arrested");
        }
        if (this.startEvent && !eventPart1) {
            this.startEvent = false;
            startKeypadEvent();
            Gdx.app.log("kp", "kp started");
            // When keypad event ends, start infiltrator event by making eventPart1 true
        }
        else if (eventPart1) {
            this.keypadFixed = true;
            this.eventPart1 = false;
            startInfiltratorEvent();
        }
        if (guiBatch.isDrawing()) {
            guiBatch.end();
        }
    }

    /**
     * Check if there is either a keypad that needs fixing or an infiltrator on the run
     * @return true if both have been sorted by the player
     */
    public boolean noEvent() {
        if (this.infiltratorLastFrame && this.keypadLastFrame) {
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
        this.keypadFixed = false;
        ActivePlayerCharacter player = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).getComponent(ActivePlayerCharacter.class);
        player.meltdownTime.reset(this.meltdownTimer);
        // RNG pick a keypad and break it
        Random random = new Random();
        int rng = random.nextInt(World.getWorld().map.keypads.size());
        Entity.getAllEntitiesWithComponents(KeypadTarget.class).get(rng).getComponent(KeypadTarget.class).breakPad();
    }

    private void startInfiltratorEvent() {
        this.infilArrested = false;
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
                new AABB((883f/637f), 2.25f, AABB.TAG_RENDER | AABB.TAG_COLLISION_X_ONLY),
                new Walking(),
                new NPCAI(3.0f),
                new Renderer(8),
                new WalkingRenderer(
                        new Texture(Gdx.files.internal("graphics/infiltratorStatic.png")),
                        AnimSheet.create(Gdx.files.internal("graphics/infiltratorWalkLeft.json")),
                        AnimSheet.create(Gdx.files.internal("graphics/infiltratorWalkRight.json"))
                ),
                new Infiltrator()
                // Fix abilities
                //new SpeedAbility()
        );
    }
}
