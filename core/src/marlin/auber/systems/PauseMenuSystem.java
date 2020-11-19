package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Assets;
import marlin.auber.common.System;

public class PauseMenuSystem implements System {
    private final SpriteBatch guiBatch = new SpriteBatch();

    private boolean isPaused;

    @Override
    public void tick() {
        //if (!guiBatch.isDrawing()) {
            //guiBatch.begin();
        //}
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.isPaused = !this.isPaused;
        }
        //if (this.isPaused) {
            //Assets.fonts.fixedsys18.draw(
            //        guiBatch,
            //        "Press ESC to continue",
            //       25, 25
            //);
        //}
    }

    public boolean checkIsPaused() {
        return this.isPaused;
    }
}
