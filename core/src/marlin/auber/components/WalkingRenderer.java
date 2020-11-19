package marlin.auber.components;

import com.badlogic.gdx.graphics.Texture;
import marlin.auber.common.AnimSheet;
import marlin.auber.common.Component;

/**
 * A "marker component" for whichever entity should be the camera target.
 *
 * There should only be one of these in the world.
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
