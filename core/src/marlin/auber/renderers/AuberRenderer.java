package marlin.auber.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Assets;
import marlin.auber.common.GuiRenderer;
import marlin.auber.common.Renderer;
import marlin.auber.models.Auber;

public class AuberRenderer implements Renderer, GuiRenderer {
    private final Auber auber;
    private final Texture auberTexture;

    public AuberRenderer(Auber auber) {
        this.auber = auber;
        this.auberTexture = new Texture(Gdx.files.internal("char.png"));
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(
                this.auberTexture,
                this.auber.position.x,// JJ TEST (Add bottom Walls to top render layer)
                this.auber.position.y - (Auber.HEIGHT/3),// JJ TEST (For Oxygen walls, maybe check if player y coord is less than the y coord of the walls & draw on top layer if necessary)
                Auber.WIDTH,
                Auber.HEIGHT
        );
    }

    @Override
    public void renderGui(SpriteBatch batch) {
        if (auber.world.debugMode) {
            Assets.fonts.fixedsys18.draw(
                    batch,
                    "Auber = " + this.auber.position.toString(),
                    10,
                    18
            );
            Assets.fonts.fixedsys18.draw(
                    batch,
                    "FPS = " + Gdx.graphics.getFramesPerSecond(),
                    10,
                    36
            );
        }
    }
}
