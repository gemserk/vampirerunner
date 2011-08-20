package com.gemserk.games.vampirerunner.gamestates;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class PlayGameState extends GameStateImpl {

	private final Game game;
	private ResourceManager<String> resourceManager;
	private WorldWrapper worldWrapper;
	private World world;

	public PlayGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int centerX = width / 2;
		int centerY = height / 2;

		resourceManager = new ResourceManagerImpl<String>();
		
		world = new World();
		worldWrapper = new WorldWrapper(world);
		
		worldWrapper.init();

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
