package marlin.auber.components;

import com.badlogic.gdx.graphics.Texture;
import marlin.auber.common.Component;

public class StaticRenderer extends Component {
    public Texture tex;

    public StaticRenderer(Texture tex) {
        this.tex = tex;
    }
}
