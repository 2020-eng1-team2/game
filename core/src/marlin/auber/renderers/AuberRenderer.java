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
                this.auber.position.x,
                this.auber.position.y,
                0.6f,
                1.8f
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
        }
    }
}
