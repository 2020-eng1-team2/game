package marlin.auber.components;

import marlin.auber.common.Component;
import marlin.auber.common.Timer;

/**
 * A marker component for the active player character.
 *
 * There should only be one of these in the scene.
 */
public class ActivePlayerCharacter extends Component {
    /**
     * A Timer for when the player can teleport. If the timer is zero, teleporting is allowed.
     */
    public Timer teleportCooldown = Timer.createTimer(0f);

    /**
     * A Timer for when the player reloads. If the timer is zero, the arrest beam's ammo is refilled.
     */
    public Timer reload = Timer.createTimer(0f);

    /**
     * A Timer for when the player can heal. If the timer is zero, healing is allowed.
     */
    public Timer healCooldown = Timer.createTimer(0f);

    /**
     * A Timer for when the player is arresting an NPC. If the timer is zero, the NPC being arrested will be arrested.
     */
    public Timer beamTime = Timer.createTimer(0f);

    /**
     * A Timer for when the player is fixing a keypad. If the timer is zero, the keypad will be fixed.
     */
    public Timer keypadTime = Timer.createTimer(0f);

    /**
     * A Timer for how long the player has to save the ship from an attack, if it reaches zero and either an infiltrator hasn't been cuaght, or a keypad is broken, the game will end.
     */
    public Timer meltdownTime = Timer.createTimer(0f);

    /**
     * A Timer for the time inbetween Infiltrator attacks.
     */
    public Timer eventCooldown = Timer.createTimer(0f);

    /**
     * A Timer for the time inbetween Infiltrator ability uses.
     */
    public Timer abilityCooldown = Timer.createTimer(0f);

    /**
     * A Timer for the duration of Infiltrator ability uses.
     */
    public Timer abilityDuration = Timer.createTimer(0f);
}
