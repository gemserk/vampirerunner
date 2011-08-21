package com.gemserk.games.vampirerunner.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.resources.ResourceManager;

public class InstructionsGameState extends GameStateImpl {

	private final Game game;
	private ResourceManager<String> resourceManager;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public InstructionsGameState(Game game) {
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

		guiContainer.add(GuiControls.label("Press left click to move through walls").id("Instructions") //
				.position(width * 0.5f, height * 0.5f) //
				.center(0.5f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build());
		
		guiContainer.add(GuiControls.label("click to start").id("ClickToStart") //
				.position(width * 0.5f, height * 0.3f) //
				.center(0.5f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build());

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorMouseLeftButton("play");
			}
		};

	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		guiContainer.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());
		inputDevicesMonitor.update();
		if (inputDevicesMonitor.getButton("play").isReleased())
			game.setScreen(game.getPlayGameScreen(), true);
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}

}
