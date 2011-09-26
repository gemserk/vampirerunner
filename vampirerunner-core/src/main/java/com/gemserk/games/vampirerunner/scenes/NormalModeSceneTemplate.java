package com.gemserk.games.vampirerunner.scenes;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.EntityBuilder;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.systems.MovementSystem;
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
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraRestrictedImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.Events;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.components.Components.DistanceComponent;
import com.gemserk.games.vampirerunner.components.RenderScriptComponent;
import com.gemserk.games.vampirerunner.render.Layers;
import com.gemserk.games.vampirerunner.scripts.ObstacleGeneratorScript;
import com.gemserk.games.vampirerunner.scripts.PreviousTilesRemoverScript;
import com.gemserk.games.vampirerunner.scripts.TerrainGeneratorScript;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireController;
import com.gemserk.games.vampirerunner.scripts.render.LabelRenderScript;
import com.gemserk.games.vampirerunner.systems.RenderScriptSystem;
import com.gemserk.games.vampirerunner.templates.CameraTemplate;
import com.gemserk.games.vampirerunner.templates.CloudSpawnerTemplate;
import com.gemserk.games.vampirerunner.templates.CloudTemplate;
import com.gemserk.games.vampirerunner.templates.FloorTileTemplate;
import com.gemserk.games.vampirerunner.templates.StaticSpriteEntityTemplate;
import com.gemserk.games.vampirerunner.templates.TimedEventTemplate;
import com.gemserk.games.vampirerunner.templates.VampireControllerTemplate;
import com.gemserk.games.vampirerunner.templates.VampirePartExplosionTemplate;
import com.gemserk.games.vampirerunner.templates.VampireTemplate;
import com.gemserk.games.vampirerunner.templates.VladimirBloodExplosionTemplate;
import com.gemserk.games.vampirerunner.templates.WallTileTemplate;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Scores;

public class NormalModeSceneTemplate {

	private ResourceManager<String> resourceManager;
	private EntityFactory entityFactory;
	private EntityBuilder entityBuilder;
	
	/**
	 * Should be removed....
	 */
	Scores scores;
	
	public void setScores(Scores scores) {
		this.scores = scores;
	}

	public EntityBuilder getEntityBuilder() {
		return entityBuilder;
	}

	public EntityFactory getEntityFactory() {
		return entityFactory;
	}

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	/**
	 * Applies a world template for the specified worldwrapper.
	 * 
	 * @param worldWrapper
	 */
	public void apply(WorldWrapper worldWrapper) {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int centerX = width / 2;
		int centerY = height / 2;

		float gameZoom = 1f;

		if (Gdx.graphics.getWidth() < 640f) {
			gameZoom = (float) Gdx.graphics.getWidth() / 640f;
		}

		final EventManager eventManager = new EventManagerImpl();

		com.badlogic.gdx.physics.box2d.World physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, 0f), false);
		BodyBuilder bodyBuilder = new BodyBuilder(physicsWorld);

		RenderLayers renderLayers = new RenderLayers();

		final Libgdx2dCamera backgroundCamera = new Libgdx2dCameraTransformImpl(centerX, centerY);
		final Libgdx2dCamera secondBackgroundCamera = new Libgdx2dCameraTransformImpl(0, 0);
		Libgdx2dCamera worldCamera = new Libgdx2dCameraTransformImpl(width / 10, height / 4);

		worldCamera.zoom(64f * gameZoom);
		secondBackgroundCamera.zoom(64 * gameZoom);

		renderLayers.add(Layers.Background, new RenderLayerSpriteBatchImpl(-1000, -500, backgroundCamera));
		renderLayers.add(Layers.SecondBackground, new RenderLayerSpriteBatchImpl(-500, -100, secondBackgroundCamera));
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-100, 100, worldCamera));

		worldWrapper.addUpdateSystem(new ScriptSystem());
		worldWrapper.addUpdateSystem(new TagSystem());
		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		worldWrapper.addUpdateSystem(new MovementSystem());
		worldWrapper.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));

		worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		worldWrapper.addRenderSystem(new RenderableSystem(renderLayers));
		worldWrapper.addRenderSystem(new RenderScriptSystem());

		worldWrapper.init();

		entityFactory = new EntityFactoryImpl(worldWrapper.getWorld());
		entityBuilder = new EntityBuilder(worldWrapper.getWorld());

		// initialize templates
		EntityTemplate staticSpriteTemplate = new StaticSpriteEntityTemplate(resourceManager);
		EntityTemplate vampireTemplate = new VampireTemplate(resourceManager, bodyBuilder, eventManager);
		EntityTemplate floorTileTemplate = new FloorTileTemplate(resourceManager, bodyBuilder);
		EntityTemplate cameraTemplate = new CameraTemplate();

		// EntityTemplate obstacleTemplate = new ObstacleTemplate(resourceManager, bodyBuilder);
		EntityTemplate obstacleTemplate = new WallTileTemplate(resourceManager, bodyBuilder);

		EntityTemplate vampireControllerTemplate = new VampireControllerTemplate();
		EntityTemplate cloudTemplate = new CloudTemplate(resourceManager);
		EntityTemplate cloudSpawnerTemplate = new CloudSpawnerTemplate(cloudTemplate, entityFactory);
		
		final EntityTemplate vladimirBloodExplosion = new VladimirBloodExplosionTemplate(resourceManager);
		final EntityTemplate timedEventTemplate = new TimedEventTemplate(eventManager);
		final EntityTemplate vladimirPartExplosion = new VampirePartExplosionTemplate(resourceManager);

		VampireController vampireController = new VampireController();

		final Camera backgroundRestrictedCamera = new CameraRestrictedImpl(-256, 0, 2 * gameZoom, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Rectangle(-768, -256, 2048, 1024));

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

		entityFactory.instantiate(cloudSpawnerTemplate, new ParametersWrapper() //
				.put("bounds", new Rectangle(0f, 3.25f, 12.5f, 3.25f)) //
				);

		entityBuilder //
				.component(new TagComponent(Tags.Player)) //
				.component(new DistanceComponent()) //
				.component(new ScriptComponent(new ScriptJavaImpl() {
					@Override
					public void update(World world, Entity e) {
						Entity vampire = world.getTagManager().getEntity(Tags.Vampire);
						if (vampire == null)
							return;
						DistanceComponent vampireDistanceComponent = vampire.getComponent(DistanceComponent.class);
						DistanceComponent distanceComponent = e.getComponent(DistanceComponent.class);
						distanceComponent.distance = vampireDistanceComponent.distance;
					}
				})) //
				.build();

		entityFactory.instantiate(vampireTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(1f, 1.75f, 1f, 1f, 0f)) //
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

						entityFactory.instantiate(timedEventTemplate, new ParametersWrapper().put("time", 1.5f).put("eventId", Events.gameFinished));

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

		entityBuilder //
				.component(new TagComponent("PlayerDeathSoundSpawner")) //
				.component(new ScriptComponent(new ScriptJavaImpl() {

					private Resource<Sound> vampireDeathSoundResource;

					@Override
					public void init(World world, Entity e) {
						vampireDeathSoundResource = resourceManager.get("VampireDeathSound");
					}

					@Handles
					public void playerDeath(Event e) {
						Sound sound = vampireDeathSoundResource.get();
						sound.play();
						// musicResource.get().stop();
					}

				})) //
				.build();
		
		//// ...
		BitmapFont scoresFont = resourceManager.getResourceValue("ScoresFont");
		
		entityBuilder.component(new RenderScriptComponent(new LabelRenderScript(worldCamera, scoresFont))).build();

	}

}
