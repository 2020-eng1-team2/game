package marlin.auber.components;

import com.badlogic.gdx.graphics.Texture;
import marlin.auber.common.AnimSheet;
import marlin.auber.common.Component;

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
