package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Assets;
import marlin.auber.common.System;

public class WinScreenSystem implements System {
    private boolean changeScreen = false;
    private final SpriteBatch guiBatch = new SpriteBatch();

    public void tick() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            // go to main menu
            changeScreen = true;
        }
        if (!guiBatch.isDrawing()) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            guiBatch.begin();
        }
        Assets.fonts.cnr.draw(
                guiBatch,
                "You Win! Press F to return to menu",
                50, 50
        );
        if (guiBatch.isDrawing()) {
            guiBatch.end();
        }
    }

    public boolean toMainMenu() {
        if (this.changeScreen) {
            // So it won't break on multiple plays
            this.changeScreen = false;
            return true;
        }
        return false;
    }
}
