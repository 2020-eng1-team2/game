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

// TODO: Fix infiltrator despawning on keypad fix
public class EventSystem implements System, Resetable {

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
    private boolean toggleAbility = false;
    private boolean abilityOn = false;
    private int lastInfiltratorNum = 0;

    private final float meltdownTimer = 60f;
    private final float eventCooldownTimer = 15f;
    private final float abilityCooldownTimer = 10f;
    private final float abilityDurationTimer = 3f;

    private final SpriteBatch guiBatch = new SpriteBatch();
    private final GlyphLayout layout = new GlyphLayout();

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
            Gdx.app.log("reset", "new game");
            player.eventCooldown.reset(eventCooldownTimer);
            player.abilityDuration.reset(abilityDurationTimer);
        }

        // Start ability activation
        if (player.abilityDuration.isOver() && abilityOn) {
            abilityOn = false;
            toggleAbility = true;
            player.abilityCooldown.reset(abilityCooldownTimer);
        }
        else if (player.abilityCooldown.isOver() && !abilityOn) {
            abilityOn = true;
            toggleAbility = true;
            player.abilityDuration.reset(abilityDurationTimer);
        }
        if (toggleAbility) {
            toggleAbility = false;
            for (Entity ent : Entity.getAllEntitiesWithComponents(Infiltrator.class)) {
                if (ent.hasComponent(InvisAbility.class)) {
                    ent.getComponent(InvisAbility.class).toggleAbility();
                }
                else if (ent.hasComponent(SpeedAbility.class)) {
                    ent.getComponent(SpeedAbility.class).toggleAbility();
                }
                else if (ent.hasComponent(StunAbility.class)) {
                    ent.getComponent(StunAbility.class).toggleAbility();
                }
            }
        }

        // Update event status
        updateArrests();
        updateKeypad();
        if (!keypadLastFrame && keypadFixed) {
            // Keypad just fixed
            eventPart1 = true;
        }
        if (!infiltratorLastFrame && infilArrested) {
            // Infiltrator just arrested
            Gdx.app.log("EventSystem", "Infil arrested; resetti");
            player.eventCooldown.reset(eventCooldownTimer);
        }
        // Check is event is happening
        if (noEvent() && player.eventCooldown.isOver()) {
            // No event is happening, start event
            Gdx.app.log("EventSystem", "Starting event");
            this.startEvent = true;
        }
        else if (player.meltdownTime.isOver() && !keypadLastFrame) {
            // Player lost, ship destroyed. End game
            // Reduces Auber's health by max health to end game
            Gdx.app.log("EventSystem", "Ship melted down, boo!");
            Gdx.app.log("EventSystem", String.format(
                    "State: meltdown remaining %f, keypad %b, infil %b, noEvent %b",
                    player.meltdownTime.getRemaining(),
                    this.keypadLastFrame,
                    this.infiltratorLastFrame,
                    this.noEvent()
            ));
            Health ent = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).getComponent(Health.class);
            ent.decreaseHealth(ent.getMaxHealth());
        }
        else if (!this.keypadFixed) {
            layout.setText(Assets.fonts.cnr, String.format("Time to meltdown: %.1f", player.meltdownTime.getRemaining()));
            float width = layout.width;
            float height = layout.height;
            Assets.fonts.cnr.setColor(0, 0, 0, 1);
            Assets.fonts.cnr.draw(
                    guiBatch,
                    String.format("Time to meltdown: %.1f", player.meltdownTime.getRemaining()),
                    (Gdx.graphics.getWidth() * 0.5f) - (width * 0.5f) - 2, (Gdx.graphics.getHeight() - (height * 1.5f)) - 2
            );
            Assets.fonts.cnr.setColor(1, 1, 1, 1);
            Assets.fonts.cnr.draw(
                    guiBatch,
                    String.format("Time to meltdown: %.1f", player.meltdownTime.getRemaining()),
                    (Gdx.graphics.getWidth() * 0.5f) - (width * 0.5f), (Gdx.graphics.getHeight() - (height * 1.5f))
            );
        }
        else if (!this.infilArrested) {
            // May use this to indicate there is a killer on the loose
            layout.setText(Assets.fonts.cnr, "Find and Arrest the Infiltrator");
            float width = layout.width;
            float height = layout.height;
            Assets.fonts.cnr.setColor(0, 0, 0, 1);
            Assets.fonts.cnr.draw(
                    guiBatch,
                    "Find and Arrest the Infiltrator",
                    (Gdx.graphics.getWidth() * 0.5f) - (width * 0.5f) - 2, (Gdx.graphics.getHeight() - (height * 1.5f)) - 2
            );
            Assets.fonts.cnr.setColor(1, 1, 1, 1);
            Assets.fonts.cnr.draw(
                    guiBatch,
                    "Find and Arrest the Infiltrator",
                    (Gdx.graphics.getWidth() * 0.5f) - (width * 0.5f), (Gdx.graphics.getHeight() - (height * 1.5f))
            );
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

    /**
     * Checks if there exists an Infiltrator on the run
     */
    private void updateArrests() {
        if (Entity.getAllEntitiesWithComponents(Infiltrator.class).size() <= 0) {
            // No Infiltrator on the run
            this.infilArrested = true;
        }
    }

    /**
     * Checks if there exists a broken keypad
     */
    private void updateKeypad() {
        for (Entity ent : Entity.getAllEntitiesWithComponents(KeypadTarget.class)) {
            if (ent.getComponent(KeypadTarget.class).isBroken) {
                return;
            }
        }
        // No broken keypads on map
        this.keypadFixed = true;
    }

    /**
     * Begins a broken keypad event
     */
    private void startKeypadEvent() {
        this.keypadFixed = false;
        ActivePlayerCharacter player = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).getComponent(ActivePlayerCharacter.class);
        player.meltdownTime.reset(this.meltdownTimer);
        // RNG pick a keypad and break it
        Random random = new Random();
        int rng = random.nextInt(World.getWorld().map.keypads.size());
        Entity.getAllEntitiesWithComponents(KeypadTarget.class).get(rng).getComponent(KeypadTarget.class).breakPad();
    }

    /**
     * Starts an Infiltrator event
     */
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
        int rng = random.nextInt(4);
         Entity infil = Entity.create(
                "Infiltrator" + lastInfiltratorNum++,
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
        );
         if (rng == 0) {
             // Attach Speed Ability
             infil.attachComponent(new SpeedAbility());
         }
         else if (rng == 1) {
             // Attach Invisibility ability
             infil.attachComponent(new InvisAbility());
         }
         else {
             // Stun ability twice as likely to appear than the other two
             // Attach stun ability
             infil.attachComponent(new StunAbility());
         }
    }

    @Override
    public void reset() {
        List<Entity> playerMaybe = Entity
                .getAllEntitiesWithComponents(Health.class);
        if (playerMaybe.size() > 0) {
            ActivePlayerCharacter player = playerMaybe.get(0).getComponent(ActivePlayerCharacter.class);
            player.meltdownTime.reset(0f);
            player.eventCooldown.reset(eventCooldownTimer);
            player.abilityCooldown.reset(abilityCooldownTimer);
            player.abilityDuration.reset(abilityDurationTimer);
        }
        keypadFixed = true;
        infilArrested = true;
        keypadLastFrame = true;
        infiltratorLastFrame = true;
        startGame = true;
        startEvent = false;
        eventPart1 = false;
        toggleAbility = false;
        abilityOn = false;
    }
}
