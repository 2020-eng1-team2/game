package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Assets;
import marlin.auber.common.System;

public class MainMenuSystem implements System {
    private final SpriteBatch guiBatch = new SpriteBatch();
    private boolean startGame = false;

    private BitmapFont font = Assets.fonts.fixedsys18;

    @Override
    public void tick() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            this.startGame = true;
        }
        if (!guiBatch.isDrawing()) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            guiBatch.begin();
        }
        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.draw(
                guiBatch,
                "Press L to begin",
                Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f
        );
    }

    public boolean checkStartGame() {
        return this.startGame;
    }
}
