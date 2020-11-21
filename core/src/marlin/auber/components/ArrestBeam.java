package marlin.auber.components;

import marlin.auber.common.Component;

public class ArrestBeam extends Component {
    private int mag_capacity = 5;
    private int beams;

    public ArrestBeam() {
        this.beams = this.mag_capacity;
    }

    public int beamsLeft() {
        return this.beams;
    }

    public void shootBeam() {
        this.beams -= 1;
    }

    public void reloadBeam() {
        this.beams = this.mag_capacity;
    }
}
