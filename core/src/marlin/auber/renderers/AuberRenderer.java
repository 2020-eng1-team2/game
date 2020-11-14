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

public class AuberRenderer implements Renderer, GuiRenderer {
    private final Auber auber;
    private final Texture idle = new Texture(Gdx.files.internal("graphics/auberStatic.png"));
    private final AnimSheet walkLeft = AnimSheet.create(Gdx.files.internal("graphics/auberWalkLeft.json"));

    public AuberRenderer(Auber auber) {
        this.auber = auber;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (this.auber.isWalking) {
            batch.draw(
                    this.walkLeft.tickAndGet(true),
                    this.auber.position.x,
                    this.auber.position.y,
                    Auber.WIDTH,
                    Auber.HEIGHT
            );
        } else {
            batch.draw(
                    this.idle,
                    this.auber.position.x,
                    this.auber.position.y,
                    Auber.WIDTH,
                    Auber.HEIGHT
            );
        }
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