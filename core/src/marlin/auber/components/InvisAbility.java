package marlin.auber.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import marlin.auber.common.AnimSheet;
import marlin.auber.common.Component;

public class InvisAbility extends Component {
    public void toggleAbility() {
        if (this.parent.hasComponent(WalkingRenderer.class)) {
            // Remove component
            this.parent.removeComponent(WalkingRenderer.class);
        }
        else {
            // Add component
            this.parent.attachComponent(new WalkingRenderer(
                    new Texture(Gdx.files.internal("graphics/infiltratorStatic.png")),
                    AnimSheet.create(Gdx.files.internal("graphics/infiltratorWalkLeft.json")),
                    AnimSheet.create(Gdx.files.internal("graphics/infiltratorWalkRight.json"))
            ));
        }
    }
}
