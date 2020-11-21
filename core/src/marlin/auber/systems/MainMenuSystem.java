package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Assets;
import marlin.auber.common.System;
import marlin.auber.models.World;

public class MainMenuSystem implements System {
    private final Texture title = new Texture(Gdx.files.internal("graphics/title.png"));
    private final SpriteBatch guiBatch = new SpriteBatch();
    private boolean startGame = false;

    private BitmapFont font = Assets.fonts.cnr;

    @Override
    public void tick() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            this.startGame = true;
        }
        if (!guiBatch.isDrawing()) {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            guiBatch.begin();
        }
        World.getWorld().map.scaleGui(title, 1.0f, guiBatch);
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
