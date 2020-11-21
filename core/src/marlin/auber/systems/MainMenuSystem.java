package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Assets;
import marlin.auber.common.System;

public class MainMenuSystem implements System {
    private final Texture pad = new Texture(Gdx.files.internal("graphics/teleportHighlight.png"));
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
        guiBatch.draw(
                pad,
                50f,
                50f,
                100f, 100f
        );
        font.draw(
                guiBatch,
                "Press L to begin",
                50, 50
        );
        guiBatch.end();
    }

    public boolean checkStartGame() {
        if (this.startGame) {
            this.startGame = false;
            return true;
        }
        else {return false;}
    }
}
