package marlin.auber.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import marlin.auber.common.Assets;
import marlin.auber.common.GuiRenderer;
import marlin.auber.common.Renderer;
import marlin.auber.models.Auber;

// Commented code is legacy code pre-animations

public class AuberRenderer implements Renderer, GuiRenderer {
    private final Auber auber;
    //private final Texture auberTexture;
    private static final int FRAME_COLS = 6, FRAME_ROWS = 5;
    Texture walkSheet;
    private Animation<TextureRegion> auberAnimation;
    float stateTime;

    public AuberRenderer(Auber auber) {
//        this.auber = auber;
//        this.auberTexture = new Texture(Gdx.files.internal("char.png"));
          this.auber = auber;
          this.walkSheet = new Texture(Gdx.files.internal("spritesheettest.png"));
          TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);
          TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
          int index = 0;
          for (int i = 0; i < FRAME_ROWS; i++) {
              for (int j = 0; j < FRAME_COLS; j++) {
                  walkFrames[index++] = tmp[i][j];
              }
          }
          auberAnimation = new Animation<TextureRegion>(0.025f, walkFrames);
          stateTime = 0f;
    }

    @Override
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = auberAnimation.getKeyFrame(stateTime, true);
        batch.draw(
                currentFrame,
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
