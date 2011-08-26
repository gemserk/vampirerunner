package com.gemserk.games.vampirerunner.gamestates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.EntityBuilder;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.ReflectionRegistratorEventSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.ScriptSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.systems.TagSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.Box2DCustomDebugRenderer;
import com.gemserk.commons.gdx.camera.CameraRestrictedImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.Events;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.GameInformation;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.components.Components.DistanceComponent;
import com.gemserk.games.vampirerunner.components.Components.SuperSkillComponent;
import com.gemserk.games.vampirerunner.render.Layers;
import com.gemserk.games.vampirerunner.scripts.ObstacleGeneratorScript;
import com.gemserk.games.vampirerunner.scripts.PreviousTilesRemoverScript;
import com.gemserk.games.vampirerunner.scripts.TerrainGeneratorScript;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireController;
import com.gemserk.games.vampirerunner.templates.CameraTemplate;
import com.gemserk.games.vampirerunner.templates.FloorTileTemplate;
import com.gemserk.games.vampirerunner.templates.ObstacleTemplate;
import com.gemserk.games.vampirerunner.templates.StaticSpriteEntityTemplate;
import com.gemserk.games.vampirerunner.templates.TimedEventTemplate;
import com.gemserk.games.vampirerunner.templates.VampireControllerTemplate;
import com.gemserk.games.vampirerunner.templates.VampirePartExplosionTemplate;
import com.gemserk.games.vampirerunner.templates.VampireTemplate;
import com.gemserk.games.vampirerunner.templates.VladimirBloodExplosionTemplate;
import com.gemserk.resources.ResourceManager;

public class PlayGameState extends GameStateImpl {

	private final Game game;
	private ResourceManager<String> resourceManager;
	private WorldWrapper worldWrapper;
	private World world;

	private EntityTemplate staticSpriteTemplate;
	private EntityTemplate vampireTemplate;
	private EntityTemplate cameraTemplate;
	private EntityTemplate floorTileTemplate;

	private EntityFactory entityFactory;
	private Box2DCustomDebugRenderer box2dCustomDebugRenderer;

	private Container guiContainer;
	private SpriteBatch spriteBatch;
	private Sprite whiteRectangle;
	private Sprite whiteRectangle2;
	private CameraRestrictedImpl backgroundRestrictedCamera;
	private Libgdx2dCamera backgroundCamera;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public PlayGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int centerX = width / 2;
		int centerY = height / 2;

		spriteBatch = new SpriteBatch();
		guiContainer = new Container();
		
		float gameZoom = 1f;
		
		if (Gdx.graphics.getWidth() < 640f) {
			gameZoom = (float) Gdx.graphics.getWidth() / 640f;
		}

		BitmapFont distanceFont = resourceManager.getResourceValue("DistanceFont");

		final Text distanceLabel = GuiControls.label("Score: ").id("DistanceLabel") //
				.position(width * 0.05f, height * 0.95f) //
				.center(0f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build();

		guiContainer.add(distanceLabel);

		final EventManager eventManager = new EventManagerImpl();

		com.badlogic.gdx.physics.box2d.World physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, 0f), false);
		BodyBuilder bodyBuilder = new BodyBuilder(physicsWorld);

		world = new World();
		worldWrapper = new WorldWrapper(world);

		RenderLayers renderLayers = new RenderLayers();

		backgroundCamera = new Libgdx2dCameraTransformImpl(centerX, centerY);
		Libgdx2dCamera worldCamera = new Libgdx2dCameraTransformImpl(width / 5, height / 4);

		worldCamera.zoom(64f * gameZoom);

		renderLayers.add(Layers.Background, new RenderLayerSpriteBatchImpl(-1000, -100, backgroundCamera));
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-100, 100, worldCamera));

		worldWrapper.addUpdateSystem(new ScriptSystem());
		worldWrapper.addUpdateSystem(new TagSystem());
		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		worldWrapper.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));

		worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		worldWrapper.addRenderSystem(new RenderableSystem(renderLayers));

		worldWrapper.init();

		entityFactory = new EntityFactoryImpl(world);
		EntityBuilder entityBuilder = new EntityBuilder(world);

		// initialize templates
		staticSpriteTemplate = new StaticSpriteEntityTemplate(resourceManager);
		vampireTemplate = new VampireTemplate(resourceManager, bodyBuilder, eventManager);
		floorTileTemplate = new FloorTileTemplate(resourceManager, bodyBuilder);
		cameraTemplate = new CameraTemplate();
		EntityTemplate obstacleTemplate = new ObstacleTemplate(resourceManager, bodyBuilder);
		EntityTemplate vampireControllerTemplate = new VampireControllerTemplate();
		final EntityTemplate vladimirBloodExplosion = new VladimirBloodExplosionTemplate(resourceManager);
		final EntityTemplate timedEventTemplate = new TimedEventTemplate(eventManager);
		final EntityTemplate vladimirPartExplosion = new VampirePartExplosionTemplate(resourceManager);

		VampireController vampireController = new VampireController();

		backgroundRestrictedCamera = new CameraRestrictedImpl(-256, 0, 2 * gameZoom, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Rectangle(-768, -256, 2048, 1024));

		entityBuilder //
				.component(new ScriptComponent(new ScriptJavaImpl() {

					float startX;
					float cameraDistance = 0f;
					float playerDistance;
					float daySpeed = 0f;

					@Override
					public void init(World world, Entity e) {
						super.init(world, e);
						startX = backgroundRestrictedCamera.getX();
					}

					@Override
					public void update(World world, Entity e) {
						// day speed
						cameraDistance += daySpeed * GlobalTime.getDelta();

						Entity player = world.getTagManager().getEntity(Tags.Vampire);
						if (player != null) {
							DistanceComponent distanceComponent = player.getComponent(DistanceComponent.class);
							playerDistance = distanceComponent.distance;
						} 

						float newPosition = startX + playerDistance - cameraDistance;

						backgroundRestrictedCamera.setPosition(newPosition, 0f);

						backgroundCamera.move(backgroundRestrictedCamera.getX(), backgroundRestrictedCamera.getY());
						backgroundCamera.zoom(backgroundRestrictedCamera.getZoom());

					}
				})) //
				.build();

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "BackgroundTile01Sprite") //
				.put("layer", -999) //
				.put("spatial", new SpatialImpl(-512, 0, 512, 512, 0f)) //
				);
		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "BackgroundTile02Sprite") //
				.put("layer", -999) //
				.put("spatial", new SpatialImpl(0, 0, 512, 512, 0f)) //
				);
		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "BackgroundTile03Sprite") //
				.put("layer", -999) //
				.put("spatial", new SpatialImpl(512, 0, 512, 512, 0f)) //
				);
		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "BackgroundTile04Sprite") //
				.put("layer", -999) //
				.put("spatial", new SpatialImpl(1024, 0, 512, 512, 0f)) //
				);

		entityFactory.instantiate(vampireTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(1f, 1.8f, 1f, 1f, 0f)) //
				.put("vampireController", vampireController) //
				);

		entityFactory.instantiate(cameraTemplate, new ParametersWrapper() //
				.put("libgdxCamera", worldCamera) //
				);

		entityFactory.instantiate(vampireControllerTemplate, new ParametersWrapper() //
				.put("vampireController", vampireController) //
				);

		// an entity which removes old tiles

		entityBuilder //
				.component(new ScriptComponent(new PreviousTilesRemoverScript(Groups.Tiles), //
						new TerrainGeneratorScript(entityFactory, floorTileTemplate, -10f))) //
				.build();

		entityBuilder //
				.component(new ScriptComponent(new PreviousTilesRemoverScript(Groups.Obstacles), //
						new ObstacleGeneratorScript(entityFactory, obstacleTemplate, 5f))) //
				.build();

		final GameInformation gameInformation = new GameInformation();

		entityBuilder //
				.component(new ScriptComponent(new ScriptJavaImpl() {

					@Override
					public void update(World world, Entity e) {
						Entity player = world.getTagManager().getEntity(Tags.Vampire);
						if (player == null)
							return;
						DistanceComponent distanceComponent = player.getComponent(DistanceComponent.class);
						gameInformation.score = (int) distanceComponent.distance;
						distanceLabel.setText("Score: " + gameInformation.score);
					}
				})) //
				.build();

		entityBuilder //
				.component(new ScriptComponent(new ScriptJavaImpl() {

					private World world;
					private String[] parts = new String[] { "VampireHead", "VampireLeftArm", "VampireRightArm", "VampireLeftLeg", "VampireRightLeg", "VampireTorso" };

					@Override
					public void init(World world, Entity e) {
						this.world = world;
						eventManager.registerEvent(Events.gameStarted, e);
					}

					@Handles
					public void gameStarted(Event e) {
						Gdx.app.log("VampireRunner", "Game started");
					}

					@Handles
					public void gameFinished(Event e) {
						Gdx.app.log("VampireRunner", "Game finished");

						game.getGameData().put("gameInformation", gameInformation);
						// game.setScreen(game.getGameOverScreen(), true);
						game.transition(game.getGameOverScreen()) //
								.disposeCurrent(true) //
								.start();
					}

					@Handles
					public void playerDeath(Event e) {
						Gdx.app.log("VampireRunner", "Player death");
						Entity entity = (Entity) e.getSource();
						entity.delete();

						SpatialComponent spatialComponent = entity.getComponent(SpatialComponent.class);

						// play death animation by creating a new entity
						// game.getGameData().put("gameInformation", gameInformation);
						// game.setScreen(game.getGameOverScreen(), true);

						entityFactory.instantiate(vladimirBloodExplosion, new ParametersWrapper().put("spatial", spatialComponent.getSpatial()));

						entityFactory.instantiate(timedEventTemplate, new ParametersWrapper().put("time", 2f).put("eventId", Events.gameFinished));

						for (int i = 0; i < parts.length; i++) {
							Vector2 direction = new Vector2(1f, 0f);
							direction.rotate(MathUtils.random(0, 360f));
							entityFactory.instantiate(vladimirPartExplosion, new ParametersWrapper() //
									.put("sprite", parts[i]) //
									.put("spatial", new SpatialImpl(spatialComponent.getSpatial())) //
									.put("direction", direction) //
									);
						}

					}

					@Override
					public void update(World world, Entity e) {
						eventManager.process();
					}
				})) //
				.build();

		box2dCustomDebugRenderer = new Box2DCustomDebugRenderer(worldCamera, physicsWorld);

		whiteRectangle = resourceManager.getResourceValue("WhiteRectangleSprite");
		whiteRectangle2 = resourceManager.getResourceValue("WhiteRectangleSprite");
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldWrapper.render();

		if (Game.isShowBox2dDebug())
			box2dCustomDebugRenderer.render();

		float totalWidth = Gdx.graphics.getWidth() * 0.8f;

		whiteRectangle.setPosition(Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.8f);
		whiteRectangle.setColor(1f, 0f, 0f, 1f);
		whiteRectangle.setSize(totalWidth, 10f);

		whiteRectangle2.setPosition(Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.8f);
		whiteRectangle2.setColor(0f, 0f, 1f, 1f);

		Entity vladimir = world.getTagManager().getEntity(Tags.Vampire);
		if (vladimir != null) {
			SuperSkillComponent superSkillComponent = vladimir.getComponent(SuperSkillComponent.class);
			whiteRectangle2.setSize(totalWidth * superSkillComponent.energy.getPercentage(), 10f);
		}

		spriteBatch.begin();

		whiteRectangle.draw(spriteBatch);
		whiteRectangle2.draw(spriteBatch);

		guiContainer.draw(spriteBatch);
		spriteBatch.end();

	}

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());
		worldWrapper.update(getDeltaInMs());

	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		worldWrapper.dispose();
	}

}
