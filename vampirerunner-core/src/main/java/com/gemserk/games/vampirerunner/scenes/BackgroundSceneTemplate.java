package com.gemserk.games.vampirerunner.scenes;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.commons.artemis.EntityBuilder;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.systems.CameraUpdateSystem;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.PreviousStateSpatialSystem;
import com.gemserk.commons.artemis.systems.ReflectionRegistratorEventSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.ScriptSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.systems.TagSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.time.TimeStepProvider;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.components.RenderScriptComponent;
import com.gemserk.games.vampirerunner.render.Layers;
import com.gemserk.games.vampirerunner.scripts.TerrainGeneratorScript;
import com.gemserk.games.vampirerunner.systems.RenderScriptSystem;
import com.gemserk.games.vampirerunner.templates.CameraTemplate;
import com.gemserk.games.vampirerunner.templates.CloudSpawnerTemplate;
import com.gemserk.games.vampirerunner.templates.CloudTemplate;
import com.gemserk.games.vampirerunner.templates.FloorTileTemplate;
import com.gemserk.games.vampirerunner.templates.StaticSpriteEntityTemplate;
import com.gemserk.games.vampirerunner.templates.VampireIdleTemplate;
import com.gemserk.resources.ResourceManager;

public class BackgroundSceneTemplate {

	static class Box2dRenderDebugScript extends ScriptJavaImpl {

		private final Box2DDebugRenderer box2dDebugRenderer = new Box2DDebugRenderer();
		private final Libgdx2dCamera camera;
		private final com.badlogic.gdx.physics.box2d.World physicsWorld;

		public Box2dRenderDebugScript(Libgdx2dCamera camera, com.badlogic.gdx.physics.box2d.World physicsWorld) {
			this.camera = camera;
			this.physicsWorld = physicsWorld;
		}

		@Override
		public void update(World world, Entity e) {
			box2dDebugRenderer.render(physicsWorld, camera.getCombinedMatrix());
		}

	}

	class Box2dDebugRendererTemplate extends EntityTemplateImpl {

		@Override
		public void apply(Entity entity) {
			Libgdx2dCamera camera = parameters.get("camera");
			com.badlogic.gdx.physics.box2d.World physicsWorld = parameters.get("physicsWorld");
			entity.addComponent(new RenderScriptComponent(new Box2dRenderDebugScript(camera, physicsWorld)));
		}
	}

	private ResourceManager<String> resourceManager;
	private EntityFactory entityFactory;
	private EntityBuilder entityBuilder;
	private TimeStepProvider timeStepProvider;

	// private EntityTemplate box2dRendererTemplate = new Box2dDebugRendererTemplate();
	private BodyBuilder bodyBuilder;

	public EntityBuilder getEntityBuilder() {
		return entityBuilder;
	}

	public EntityFactory getEntityFactory() {
		return entityFactory;
	}

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}
	
	public void setTimeStepProvider(TimeStepProvider timeStepProvider) {
		this.timeStepProvider = timeStepProvider;
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
		bodyBuilder = new BodyBuilder(physicsWorld);

		RenderLayers renderLayers = new RenderLayers();

		final Libgdx2dCamera backgroundCamera = new Libgdx2dCameraTransformImpl(centerX, centerY);
		final Libgdx2dCamera secondBackgroundCamera = new Libgdx2dCameraTransformImpl(0, 0);
		Libgdx2dCamera worldCamera = new Libgdx2dCameraTransformImpl(width / 10, height / 4);

		worldCamera.zoom(64f * gameZoom);
		backgroundCamera.zoom(2 * gameZoom);
		secondBackgroundCamera.zoom(64 * gameZoom);

		renderLayers.add(Layers.Background, new RenderLayerSpriteBatchImpl(-1000, -500, backgroundCamera));
		renderLayers.add(Layers.SecondBackground, new RenderLayerSpriteBatchImpl(-500, -100, secondBackgroundCamera));
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-100, 100, worldCamera));

		final Map<Long, Vector2> previousPositions = new HashMap<Long, Vector2>();

		worldWrapper.addUpdateSystem(new EntityProcessingSystem(SpatialComponent.class) {

			@Override
			protected void begin() {
				super.begin();
				previousPositions.clear();
			}

			@Override
			protected void process(Entity e) {
				SpatialComponent spatialComponent = Components.spatialComponent(e);
				Vector2 position = spatialComponent.getPosition();
				previousPositions.put(e.getUniqueId(), new Vector2(position));
			}
		});

		worldWrapper.addUpdateSystem(new PreviousStateSpatialSystem());
		worldWrapper.addUpdateSystem(new ScriptSystem());
		worldWrapper.addUpdateSystem(new TagSystem());
		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		worldWrapper.addUpdateSystem(new MovementSystem());
		worldWrapper.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));

		worldWrapper.addRenderSystem(new CameraUpdateSystem(timeStepProvider));

		worldWrapper.addRenderSystem(new SpriteUpdateSystem(timeStepProvider));
		worldWrapper.addRenderSystem(new RenderableSystem(renderLayers));
		worldWrapper.addRenderSystem(new RenderScriptSystem());

		worldWrapper.init();

		entityFactory = new EntityFactoryImpl(worldWrapper.getWorld());
		entityBuilder = new EntityBuilder(worldWrapper.getWorld());

		// initialize templates
		EntityTemplate staticSpriteTemplate = new StaticSpriteEntityTemplate(resourceManager);
		EntityTemplate vampireTemplate = new VampireIdleTemplate(resourceManager);
		EntityTemplate floorTileTemplate = new FloorTileTemplate(resourceManager, bodyBuilder);
		EntityTemplate cameraTemplate = new CameraTemplate();

		EntityTemplate cloudTemplate = new CloudTemplate(resourceManager);
		EntityTemplate cloudSpawnerTemplate = new CloudSpawnerTemplate(cloudTemplate, entityFactory);

		Camera worldCameraData = new CameraImpl(0f, 0f, 64f * gameZoom, 0f);

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "BackgroundTile03Sprite") //
				.put("layer", -999) //
				.put("spatial", new SpatialImpl(-512, 0, 512, 512, 0f)) //
				);
		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "BackgroundTile04Sprite") //
				.put("layer", -999) //
				.put("spatial", new SpatialImpl(0, 0, 512, 512, 0f)) //
				);

		entityFactory.instantiate(cloudSpawnerTemplate, new ParametersWrapper() //
				.put("bounds", new Rectangle(0f, 3.25f, 12.5f, 3.25f)) //
				);

		entityFactory.instantiate(vampireTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(1f, 1.75f, 1f, 1f, 0f)) //
				);

		entityFactory.instantiate(cameraTemplate, new ParametersWrapper() //
				.put("libgdxCamera", worldCamera) //
				.put("camera", worldCameraData) //
				);

		// an entity which removes old tiles

		entityBuilder //
				.component(new ScriptComponent(new TerrainGeneratorScript(entityFactory, floorTileTemplate, -10f))) //
				.build();

	}

}
