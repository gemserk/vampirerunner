package com.gemserk.games.vampirerunner.gamestates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.EntityBuilder;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.ScriptSystem;
import com.gemserk.commons.artemis.systems.TagSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.Box2DCustomDebugRenderer;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.render.Layers;
import com.gemserk.games.vampirerunner.scripts.ObstacleGeneratorScript;
import com.gemserk.games.vampirerunner.scripts.PreviousTilesRemoverScript;
import com.gemserk.games.vampirerunner.scripts.TerrainGeneratorScript;
import com.gemserk.games.vampirerunner.templates.CameraTemplate;
import com.gemserk.games.vampirerunner.templates.FloorTileTemplate;
import com.gemserk.games.vampirerunner.templates.ObstacleTemplate;
import com.gemserk.games.vampirerunner.templates.StaticSpriteEntityTemplate;
import com.gemserk.games.vampirerunner.templates.VampireTemplate;
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

		BitmapFont distanceFont = resourceManager.getResourceValue("DistanceFont");

		final Text distanceLabel = GuiControls.label("Score: ").id("DistanceLabel") //
				.position(width * 0.05f, height * 0.95f) //
				.center(0f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build();

		guiContainer.add(distanceLabel);

		com.badlogic.gdx.physics.box2d.World physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, -1f), false);
		BodyBuilder bodyBuilder = new BodyBuilder(physicsWorld);

		world = new World();
		worldWrapper = new WorldWrapper(world);

		RenderLayers renderLayers = new RenderLayers();

		Libgdx2dCamera backgroundCamera = new Libgdx2dCameraTransformImpl(centerX, centerY);
		Libgdx2dCamera worldCamera = new Libgdx2dCameraTransformImpl(width / 10, height / 10);
		worldCamera.zoom(32f);

		renderLayers.add(Layers.Background, new RenderLayerSpriteBatchImpl(-1000, -100, backgroundCamera));
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-100, 100, worldCamera));

		worldWrapper.addUpdateSystem(new ScriptSystem());
		worldWrapper.addUpdateSystem(new TagSystem());
		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));

		// worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		// worldWrapper.addRenderSystem(new RenderableSystem(renderLayers));

		worldWrapper.init();

		entityFactory = new EntityFactoryImpl(world);
		EntityBuilder entityBuilder = new EntityBuilder(world);

		// initialize templates
		staticSpriteTemplate = new StaticSpriteEntityTemplate(resourceManager);
		vampireTemplate = new VampireTemplate(resourceManager, bodyBuilder);
		floorTileTemplate = new FloorTileTemplate(resourceManager, bodyBuilder);
		cameraTemplate = new CameraTemplate();
		EntityTemplate obstacleTemplate = new ObstacleTemplate(bodyBuilder);

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "BackgroundSprite") //
				.put("layer", -999) //
				.put("spatial", new SpatialImpl(0, 0, width, height, 0f)) //
				);

		entityFactory.instantiate(vampireTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(1f, 2f, 1f, 1f, 0f)) //
				);

		entityFactory.instantiate(cameraTemplate, new ParametersWrapper() //
				.put("libgdxCamera", worldCamera) //
				);

		// an entity which removes old tiles

		entityBuilder //
				.component(new ScriptComponent(new PreviousTilesRemoverScript(Groups.Tiles), new TerrainGeneratorScript(entityFactory, floorTileTemplate, -5f))) //
				.build();

		entityBuilder //
				.component(new ScriptComponent(new PreviousTilesRemoverScript(Groups.Obstacles), new ObstacleGeneratorScript(entityFactory, obstacleTemplate, 5f))) //
				.build();

		entityBuilder //
				.component(new ScriptComponent(new ScriptJavaImpl() {

					float distance = 0f;
					float lastPlayerPosition = 0f;
					int score = 0;

					@Override
					public void init(World world, Entity e) {
						Entity player = world.getTagManager().getEntity(Tags.Vampire);
						SpatialComponent playerSpatialComponent = player.getComponent(SpatialComponent.class);
						Spatial playerSpatial = playerSpatialComponent.getSpatial();
						lastPlayerPosition = playerSpatial.getX();
					}

					@Override
					public void update(World world, Entity e) {
						Entity player = world.getTagManager().getEntity(Tags.Vampire);
						SpatialComponent playerSpatialComponent = player.getComponent(SpatialComponent.class);
						Spatial playerSpatial = playerSpatialComponent.getSpatial();

						distance += playerSpatial.getX() - lastPlayerPosition;

						lastPlayerPosition = playerSpatial.getX();

						score = (int) distance;

						distanceLabel.setText("Score: " + score);
					}
				})) //
				.build();

		box2dCustomDebugRenderer = new Box2DCustomDebugRenderer(worldCamera, physicsWorld);
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldWrapper.render();

		if (Game.isShowBox2dDebug())
			box2dCustomDebugRenderer.render();

		spriteBatch.begin();
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
		// world.dispose();
	}

}
