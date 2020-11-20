package marlin.auber.components;

import com.badlogic.gdx.graphics.Texture;
import marlin.auber.common.Component;

/**
 * An entity that should have a static texture rendered at its {@link Position} no matter what.
 */
public class StaticRenderer extends Component {
    public Texture tex;

    public StaticRenderer(Texture tex) {
        this.tex = tex;
    }
}
