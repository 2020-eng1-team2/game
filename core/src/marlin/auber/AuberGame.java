package marlin.auber;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Controller;
import marlin.auber.common.GuiRenderer;
import marlin.auber.common.Renderer;
import marlin.auber.controllers.AuberKeyboardController;
import marlin.auber.models.Auber;
import marlin.auber.models.World;
import marlin.auber.renderers.AuberRenderer;

import java.util.ArrayList;
import java.util.List;

public class AuberGame extends ApplicationAdapter {
	World world;
	Auber auber;

	List<Controller> activeControllers = new ArrayList<>();

	List<Renderer> activeRenderers = new ArrayList<>();
	List<GuiRenderer> activeGuiRenderers = new ArrayList<>();

	SpriteBatch batch;
	SpriteBatch guiBatch;
	Texture img;
	
	@Override
	public void create () {
		this.world = new World();
		this.auber = new Auber(this.world);
		activeControllers.add(new AuberKeyboardController(this.auber));
		AuberRenderer auberRenderer = new AuberRenderer(this.auber);
		activeRenderers.add(auberRenderer);
		activeGuiRenderers.add(auberRenderer);
		batch = new SpriteBatch();
		guiBatch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		for (Controller controller : this.activeControllers) {
			controller.tick();
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.world.viewport.apply();
		batch.setProjectionMatrix(this.world.viewport.getCamera().combined);
		batch.begin();
		for (Renderer renderer : this.activeRenderers) {
			renderer.render(batch);
		}
		batch.end();
		// Reset viewport for GUI
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		guiBatch.begin();
		for (GuiRenderer gui : this.activeGuiRenderers) {
			gui.renderGui(guiBatch);
		}
		guiBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		this.world.viewport.update(width, height);
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
