package marlin.auber.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Renderer;
import marlin.auber.models.Auber;

public class AuberRenderer implements Renderer {
    private Auber auber;
    private Texture auberTexture;

    public AuberRenderer(Auber auber) {
        this.auber = auber;
        this.auberTexture = new Texture(Gdx.files.internal("char.png"));
    }

    @Override
    public void tick(SpriteBatch batch) {
        batch.draw(
                this.auberTexture,
                this.auber.position.x,
                this.auber.position.y,
                0.6f,
                1.8f
        );
    }
}
