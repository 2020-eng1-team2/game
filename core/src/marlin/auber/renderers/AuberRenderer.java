package marlin.auber.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import marlin.auber.common.AnimSheet;
import marlin.auber.common.Assets;
import marlin.auber.common.GuiRenderer;
import marlin.auber.common.Renderer;
import marlin.auber.models.Auber;

// Commented code is legacy code pre-animations

public class AuberRenderer implements Renderer, GuiRenderer {
    private final Auber auber;
    private final AnimSheet walkLeft;

    public AuberRenderer(Auber auber) {
        this.auber = auber;
        this.walkLeft = AnimSheet.create(Gdx.files.internal("graphics/auberWalkLeft.json"));
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(
                this.walkLeft.tickAndGet(true),
                this.auber.position.x,
                this.auber.position.y,
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