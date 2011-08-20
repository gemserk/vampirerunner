package com.gemserk.games.vampirerunner.gamestates;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.render.Layers;
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

	private EntityFactory entityFactory;

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

		world = new World();
		worldWrapper = new WorldWrapper(world);

		RenderLayers renderLayers = new RenderLayers();

		Libgdx2dCamera backgroundCamera = new Libgdx2dCameraTransformImpl(centerX, centerY);
		Libgdx2dCamera worldCamera = new Libgdx2dCameraTransformImpl(centerX, centerY);
		worldCamera.zoom(48f);

		renderLayers.add(Layers.Background, new RenderLayerSpriteBatchImpl(-1000, -100, backgroundCamera));
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-100, 100, worldCamera));

		worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		worldWrapper.addRenderSystem(new RenderableSystem(renderLayers));

		worldWrapper.init();

		entityFactory = new EntityFactoryImpl(world);

		{
			// initialize templates
			staticSpriteTemplate = new StaticSpriteEntityTemplate(resourceManager);
			vampireTemplate = new VampireTemplate(resourceManager);
		}

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "BackgroundSprite") //
				.put("layer", -999) //
				.put("spatial", new SpatialImpl(0, 0, width, height, 0f)) //
				);

		entityFactory.instantiate(vampireTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(1f, 1f, 1f, 1f, 0f)) //
				);
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldWrapper.render();
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
