package marlin.auber.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Renderer;
import marlin.auber.models.Auber;
import marlin.auber.models.Infiltrator;

public class InfiltratorRenderer implements Renderer {
    private final Infiltrator infil;
    private final Texture auberTexture;

    public InfiltratorRenderer(Infiltrator infiltrator) {
        this.infil = infiltrator;
        this.auberTexture = new Texture(Gdx.files.internal("testChar2.png"));
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(
                this.auberTexture,
                this.infil.position.x,
                this.infil.position.y,
                Infiltrator.WIDTH,
                Infiltrator.HEIGHT
        );
    }
}
