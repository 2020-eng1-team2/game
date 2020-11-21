package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.System;

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
            scaleGui(pauseScreen, 0.75f, guiBatch);
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

    private void scaleGui(Texture texture, float cover, SpriteBatch batch) {
        float mapAspectRatio = (texture.getWidth() * 1f)/(texture.getHeight() * 1f); // texture width / texture height
        float currentAspectRatio = (Gdx.graphics.getWidth() * 1f)/(Gdx.graphics.getHeight() * 1f);
        float defaultAspectRatio = 16f/9f;
        // If aspect ratio of screen is less than the aspect ratio of map texture, then the width of the texture
        // needs to be 90% the width of the screen and vice versa
        if (currentAspectRatio > mapAspectRatio) {
            // float drawMapWidth is used to store the draw width of the texture. This is calculated using the draw height, this is then divided
            // by the ratios of the current and default aspect ratios before being converted into the draw width
            float drawMapHeight = 720f * cover;
            float drawMapWidth = (drawMapHeight/(currentAspectRatio/defaultAspectRatio)) * mapAspectRatio;
            Vector2 drawMapOrigin = new Vector2((1280f / 2f) - (0.5f * drawMapWidth), 720f * ((1f - cover)/2f));
            batch.draw(
                    texture,
                    drawMapOrigin.x,
                    drawMapOrigin.y,
                    drawMapWidth,
                    drawMapHeight
            );
        }
        else{
            // same as the process to calculate float drawMapWidth, except we know the draw width this time, and so are calculating the draw height
            float drawMapWidth = 1280f * cover;
            float drawMapHeight = (drawMapWidth*(currentAspectRatio/defaultAspectRatio)) / mapAspectRatio;
            Vector2 drawMapOrigin = new Vector2(1280f * ((1f - cover)/2f), (720f / 2f) - (0.5f * drawMapHeight));
            batch.draw(
                    texture,
                    drawMapOrigin.x,
                    drawMapOrigin.y,
                    drawMapWidth,
                    drawMapHeight
            );
        }
    }
}
