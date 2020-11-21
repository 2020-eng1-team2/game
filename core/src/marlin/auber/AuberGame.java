package marlin.auber;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import marlin.auber.common.*;
import marlin.auber.common.System;
import marlin.auber.components.*;
import marlin.auber.components.Renderer;
import marlin.auber.models.Map;
import marlin.auber.models.World;
import marlin.auber.systems.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuberGame extends ApplicationAdapter {
	List<System> pauseSystems;
	List<System> menuSystems;
	List<System> systems;
	List<Disposable> disposables;
	PauseMenuSystem pauseMenuSystem;
	MainMenuSystem mainMenuSystem;

	boolean oneTimeMenu = false;
	boolean oneTimeGame = false;

	public enum State{
		RUNNING, PAUSED, MENU
	}

	// Initial Game state
	State game_state = State.MENU;
	
	@Override
	public void create () {
		World.init(
				Map.loadMap(Gdx.files.internal("maps/map1/map1.json"))
		);

		Entity.create(
				"auber",
				new Position(World.getWorld().map.auberSpawn),
				new AABB((883f/637f), 2.25f, AABB.TAG_RENDER | AABB.TAG_COLLISION_X_ONLY),
				new KeyboardMovement(3.0f),
				new Walking(),
				new Renderer(10),
				new WalkingRenderer(
					new Texture(Gdx.files.internal("graphics/auberStatic.png")),
					AnimSheet.create(Gdx.files.internal("graphics/auberWalkLeft.json")),
					AnimSheet.create(Gdx.files.internal("graphics/auberWalkRight.json"))
				),
				new ViewportTarget(),
				new ActivePlayerCharacter()
		);

		for (int i = 0; i < 10; i++) {
			Entity.create(
					"boris" + i,
					new Position(World.getWorld().map.auberSpawn),
					new AABB(1.8f, 1.8f, AABB.TAG_RENDER | AABB.TAG_COLLISION_X_ONLY),
					new Walking(),
					new NPCAI(3.0f),
					new Renderer(8),
					new StaticRenderer(
							new Texture(Gdx.files.internal("testChar2.png"))
					)
			);
		}

		int i = 0;
		for (Vector2 pad : World.getWorld().map.teleportPads) {
			Entity.create(
					"pad" + i++,
					new Position(pad),
					new TeleportTarget(3.0f)
			);
		}

		RenderSystem renderSystem = new RenderSystem();
		pauseMenuSystem = new PauseMenuSystem();
		mainMenuSystem = new MainMenuSystem();

		this.systems = Arrays.asList(
				pauseMenuSystem,
				new ArrestSystem(),
				new KeyboardMovementSystem(),
				new ViewportTargetSystem(),
				renderSystem,
				new NavMeshDebuggingSystem(),
				new NPCAISystem(),
				new TeleportPadSystem()
		);

		this.pauseSystems = Arrays.asList(
				renderSystem,
				pauseMenuSystem
		);

		this.menuSystems = Arrays.asList(
				mainMenuSystem
		);

		this.disposables = Collections.singletonList(renderSystem);
	}

	@Override
	public void render () {
		if (game_state == State.RUNNING || game_state == State.PAUSED) {
			// If escape key is pressed, change game_state to State.PAUSED
			if (pauseMenuSystem.checkIsPaused()) {
				game_state = State.PAUSED;
				if (pauseMenuSystem.checkMenu()) {
					game_state = State.MENU;
					this.oneTimeMenu = true;
				}
			} else {
				game_state = State.RUNNING;
			}
		}
		else {
			// In Main menu
			if (mainMenuSystem.checkStartGame()) {
				this.oneTimeGame = true;
				game_state = State.RUNNING;
			}
		}

		// Clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		switch (game_state) {
			case RUNNING:
				if (this.oneTimeGame) {
					this.oneTimeGame = false;
					// TODO: Reset Game state here
				}
				// Tick timers
				Timer.tickAll();
				// Tick the systems
				for (System syst : systems) {
					syst.tick();
				}
				break;
			case PAUSED:
				// Pause screen render code
				for (System syst : pauseSystems) {
					syst.tick();
				}
				break;
			case MENU:
				if (this.oneTimeMenu) {
					this.oneTimeMenu = false;
					// TODO: Set up demo environment
				}
				for (System syst : menuSystems) {
					syst.tick();
				}
				break;
		}
	}

	@Override
	public void resize(int width, int height) {
		World.getWorld().viewport.update(width, height);
	}

	@Override
	public void dispose () {
		for (Disposable d : this.disposables) {
			d.dispose();
		}
	}
}
