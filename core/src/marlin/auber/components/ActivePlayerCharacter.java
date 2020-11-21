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

    public Timer reload = Timer.createTimer(0f);

    public Timer healCooldown = Timer.createTimer(0f);
}
