package marlin.auber.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import marlin.auber.common.AnimSheet;
import marlin.auber.common.Component;

public class InvisAbility extends Component {
    public void toggleAbility() {
        if (this.parent.hasComponent(Renderer.class)) {
            // Remove component
            this.parent.removeComponent(Renderer.class);
        }
        else {
            // Add component
            this.parent.attachComponent(new Renderer(8));
        }
    }
}
