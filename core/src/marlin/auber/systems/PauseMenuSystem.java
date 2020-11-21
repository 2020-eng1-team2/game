package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.System;
import marlin.auber.models.World;

public class PauseMenuSystem implements System {
    private final SpriteBatch guiBatch = new SpriteBatch();
    private final Texture pauseScreen = new Texture(Gdx.files.internal("graphics/pause_screen.png"));

    private boolean isPaused = false;
    private boolean inMenu = false;

    @Override
    public void tick() {
        float ssScreenW = Gdx.graphics.getWidth();
        float ssScreenH = Gdx.graphics.getHeight();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.isPaused = !this.isPaused;
        }
        if (this.isPaused) {
            // Check input for quit
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                Gdx.app.log("menu", "to menu!");
                this.inMenu = !this.inMenu;
            }
            // Draw GUI
            if (!guiBatch.isDrawing()) {
                Gdx.gl.glViewport(0, 0, (int) ssScreenW, (int) ssScreenH);
                guiBatch.begin();
            }
            //font.setColor(0.0f, 0.0f, 0.0f, 1.0f);
            World.getWorld().map.scaleGui(pauseScreen, 0.75f, guiBatch);
            guiBatch.end();
        }
    }

    public boolean checkIsPaused() {
        return this.isPaused;
    }

    public boolean checkMenu() {
        if(this.inMenu) {
            this.inMenu = false;
            this.isPaused = false;
            return true;
        }
        else {return false;}
    }
}
