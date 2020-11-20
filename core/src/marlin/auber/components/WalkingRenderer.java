package marlin.auber.components;

import com.badlogic.gdx.graphics.Texture;
import marlin.auber.common.AnimSheet;
import marlin.auber.common.Component;

/**
 * An entity that should have a walking or idle animation rendered.
 *
 * Entities with this component MUST also have {@link Position} and {@link Walking}.
 */
public class WalkingRenderer extends Component {
    public Texture idle;
    public AnimSheet walkLeft;
    public AnimSheet walkRight;

    public WalkingRenderer(Texture idle, AnimSheet walkLeft, AnimSheet walkRight) {
        this.idle = idle;
        this.walkLeft = walkLeft;
        this.walkRight = walkRight;
    }
}
