package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import marlin.auber.common.Assets;
import marlin.auber.common.System;

public class PauseMenuSystem implements System {
    private final SpriteBatch guiBatch = new SpriteBatch();

    private BitmapFont font = Assets.fonts.fixedsys18;

    private boolean isPaused;

    @Override
    public void tick() {
        float ssScreenW = Gdx.graphics.getWidth();
        float ssScreenH = Gdx.graphics.getHeight();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.isPaused = !this.isPaused;
        }
        if (this.isPaused) {
            if (!guiBatch.isDrawing()) {
                Gdx.gl.glViewport(0, 0, (int) ssScreenW, (int) ssScreenH);
                guiBatch.begin();
            }
            //font.setColor(0.0f, 0.0f, 0.0f, 1.0f);
            font.draw(
                    guiBatch,
                    "Press ESC to continue",
                   Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f
            );
        }
    }

    public boolean checkIsPaused() {
        return this.isPaused;
    }
}
