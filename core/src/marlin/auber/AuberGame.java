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
	List<System> winSystems;
	List<System> loseSystems;
	List<System> menuSystems;
	List<System> systems;
	List<Disposable> disposables;
	PauseMenuSystem pauseMenuSystem;
	MainMenuSystem mainMenuSystem;
	WinScreenSystem winGameSystem;
	LoseScreenSystem loseGameSystem;
	HealthSystem healthSystem;
	ScoreSystem scoreSystem;

	boolean oneTimeMenu = true;
	boolean oneTimeGame = false;

	public enum State{
		RUNNING, PAUSED, MENU, WIN, LOSE
	}

	// Initial Game state
	State game_state = State.MENU;
	
	@Override
	public void create () {
		// TODO: Reduce loading times (Don't create entities here, create systems in resetGame(), or reduce file size of sprites!)
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
				new ActivePlayerCharacter(),
				new ArrestBeam(),
				new Health()
		);

		for (int i = 0; i < 10; i++) {
			Entity.create(
					"boris" + i,
					new Position(World.getWorld().map.auberSpawn),
					new AABB((883f/637f), 2.25f, AABB.TAG_RENDER | AABB.TAG_COLLISION_X_ONLY),
					new Walking(),
					new NPCAI(3.0f),
					new Renderer(8),
					new WalkingRenderer(
							new Texture(Gdx.files.internal("graphics/npc1Static.png")),
							AnimSheet.create(Gdx.files.internal("graphics/npc1WalkLeft.json")),
							AnimSheet.create(Gdx.files.internal("graphics/npc1WalkRight.json"))
					)
			);
		}

		int i = 0;
		for (Vector2 pad : World.getWorld().map.teleportPads) {
			Entity.create(
					"pad" + i++,
					new Position(pad),
					new TeleportTarget(1.0f)
			);
		}

		i = 0;
		for (Vector2 kp : World.getWorld().map.keypads) {
			Entity.create(
					"kp" + i++,
					new Position(kp),
					new KeypadTarget(3.0f)
			);
		}

		RenderSystem renderSystem = new RenderSystem();
		pauseMenuSystem = new PauseMenuSystem();
		mainMenuSystem = new MainMenuSystem();
		winGameSystem = new WinScreenSystem();
		loseGameSystem = new LoseScreenSystem();
		healthSystem = new HealthSystem();
		scoreSystem = new ScoreSystem();

		this.systems = Arrays.asList(
				pauseMenuSystem,
				new KeyboardMovementSystem(),
				new ViewportTargetSystem(),
				renderSystem,
				new ArrestSystem(),
				healthSystem,
				//new NavMeshDebuggingSystem(),
				new NPCAISystem(),
				new CellNPCAISystem(),
				new TeleportPadSystem(),
				new KeypadSystem(),
				new EventSystem(),
				scoreSystem
		);

		this.pauseSystems = Arrays.asList(
				renderSystem,
				pauseMenuSystem
		);

		this.winSystems = Arrays.asList(
				renderSystem,
				winGameSystem
		);

		this.loseSystems = Arrays.asList(
				renderSystem,
				loseGameSystem
		);

		this.menuSystems = Arrays.asList(
				new KeyboardMovementSystem(),
				new ViewportTargetSystem(),
				renderSystem,
				new NavMeshDebuggingSystem(),
				mainMenuSystem,
				new NPCAISystem()
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
				if (healthSystem.isGameOver()) {
					game_state = State.LOSE;
				}
				else if (scoreSystem.gameWin()) {
					game_state = State.WIN;
				}
			}
		}
		else if (game_state == State.MENU) {
			// In Main menu
			if (mainMenuSystem.checkStartGame()) {
				this.oneTimeGame = true;
				game_state = State.RUNNING;
			}
		}
		else if (game_state == State.WIN) {
			// In win screen
			if (winGameSystem.toMainMenu()) {
				this.oneTimeMenu = true;
				game_state = State.MENU;
			}
		}
		else {
			// In lose screen
			if (loseGameSystem.toMainMenu()) {
				this.oneTimeMenu = true;
				for (System sys : this.systems) {
					if (sys instanceof Resetable) {
						((Resetable) sys).reset();
					}
				}
				game_state = State.MENU;
			}
		}

		// Clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		switch (game_state) {
			case RUNNING:
				if (this.oneTimeGame) {
					this.oneTimeGame = false;
					gameReset();
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
					createDemo();
				}
				// Tick timers
				Timer.tickAll();
				for (System syst : menuSystems) {
					syst.tick();
				}
				break;
			case WIN:
				// Draw win text on screen
				for (System syst : winSystems) {
					syst.tick();
				}
				break;
			case LOSE:
				// Draw lose text on screen
				for (System syst : loseSystems) {
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

	public void createDemo() {
		// TODO: Fix the lack of walking
		// Find Auber and delete it
		Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).destroy();

		// Find and Destroy all NPCs
		for (Entity ent : Entity.getAllEntitiesWithComponents(NPCAI.class)){
			ent.destroy();
		}

		// Find and Destroy all Prisoners
		for (Entity ent : Entity.getAllEntitiesWithComponents(CellNPCAI.class)){
			ent.destroy();
		}

		// Create new Auber and give it AI components
		Entity.create(
				"auber",
				new Position(World.getWorld().map.auberSpawn),
				new AABB((883f/637f), 2.25f, AABB.TAG_RENDER | AABB.TAG_COLLISION_X_ONLY),
				new Walking(),
				new NPCAI(3.0f),
				new Renderer(10),
				new WalkingRenderer(
						new Texture(Gdx.files.internal("graphics/auberStatic.png")),
						AnimSheet.create(Gdx.files.internal("graphics/auberWalkLeft.json")),
						AnimSheet.create(Gdx.files.internal("graphics/auberWalkRight.json"))
				),
				new ViewportTarget(),
				new ActivePlayerCharacter()
		);

		// Create new NPCs
		for (int i = 0; i < 10; i++) {
			Entity.create(
					"boris" + i,
					new Position(World.getWorld().map.auberSpawn),
					new AABB((883f/637f), 2.25f, AABB.TAG_RENDER | AABB.TAG_COLLISION_X_ONLY),
					new Walking(),
					new NPCAI(3.0f),
					new Renderer(8),
					new WalkingRenderer(
							new Texture(Gdx.files.internal("graphics/npc1Static.png")),
							AnimSheet.create(Gdx.files.internal("graphics/npc1WalkLeft.json")),
							AnimSheet.create(Gdx.files.internal("graphics/npc1WalkRight.json"))
					)
			);
		}
	}

	public void gameReset() {
		// Destroy Auber
		Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0).destroy();

		// Destroy NPCs
		for (Entity ent : Entity.getAllEntitiesWithComponents(NPCAI.class)){
			ent.destroy();
		}

		// Create new Auber
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
				new ActivePlayerCharacter(),
				new ArrestBeam(),
				new Health()
		);

		// Create new NPCs
		for (int i = 0; i < 10; i++) {
			Entity.create(
					"boris" + i,
					new Position(World.getWorld().map.auberSpawn),
					new AABB((883f/637f), 2.25f, AABB.TAG_RENDER | AABB.TAG_COLLISION_X_ONLY),
					new Walking(),
					new NPCAI(3.0f),
					new Renderer(8),
					new WalkingRenderer(
							new Texture(Gdx.files.internal("graphics/npc1Static.png")),
							AnimSheet.create(Gdx.files.internal("graphics/npc1WalkLeft.json")),
							AnimSheet.create(Gdx.files.internal("graphics/npc1WalkRight.json"))
					)
			);
		}

		// Any thing else I've missed goes here
	}
}
