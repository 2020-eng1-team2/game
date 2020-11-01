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
import marlin.auber.controllers.InfiltratorAIController;
import marlin.auber.models.Auber;
import marlin.auber.models.Infiltrator;
import marlin.auber.models.Map;
import marlin.auber.models.World;
import marlin.auber.renderers.AuberRenderer;
import marlin.auber.renderers.InfiltratorRenderer;
import marlin.auber.renderers.MapBaseRenderer;
import marlin.auber.renderers.MapTopRenderer;

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
	
	@Override
	public void create () {
		this.world = new World(
				Map.loadMap(Gdx.files.internal("maps/map1/map1.json"))
		);
		this.auber = new Auber(this.world);

		activeControllers.add(new AuberKeyboardController(this.auber));

		AuberRenderer auberRenderer = new AuberRenderer(this.auber);

		activeRenderers.add(new MapBaseRenderer(this.world));
		activeRenderers.add(auberRenderer);

		for (int i = 0; i < 10; i++) {
			Infiltrator boris = new Infiltrator(this.world);
			activeControllers.add(new InfiltratorAIController(boris));
			activeRenderers.add(new InfiltratorRenderer(boris));
		}

		activeRenderers.add(new MapTopRenderer(this.world));

		activeGuiRenderers.add(auberRenderer);

		batch = new SpriteBatch();
		guiBatch = new SpriteBatch();
	}

	@Override
	public void render () {
		// Tick the controllers
		for (Controller controller : this.activeControllers) {
			controller.tick();
		}
		// Clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Set the camera to Auber and apply it to OpenGL
		this.world.viewport.getCamera().position.x = this.auber.position.x;
		this.world.viewport.getCamera().position.y = this.auber.position.y;
		this.world.viewport.apply();
		batch.setProjectionMatrix(this.world.viewport.getCamera().combined);
		// Render!
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
		guiBatch.dispose();
	}
}
