package marlin.auber.components;

import marlin.auber.common.Component;

/**
 * A component for Auber which tracks arrest beams.
 *
 * There should only be one of these in the scene.
 */
public class ArrestBeam extends Component {

    /**
     * The maximum beams the entity can have. (Set to desired value + 1) (Bug, needs fixing)
     */
    private int mag_capacity = 6;
    private int beams;

    public ArrestBeam() {
        this.beams = this.mag_capacity;
    }

    /**
     * Gets remaining beams of entity.
     * @return Remaining beams of the entity.
     */
    public int beamsLeft() {
        return this.beams;
    }

    /**
     * Reduces the beams of the entity by one.
     */
    public void shootBeam() {
        this.beams -= 1;
    }

    /**
     * Resets the arrest beams ammo to the maximum capacity
     */
    public void reloadBeam() {
        this.beams = this.mag_capacity;
    }
}
