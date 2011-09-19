package com.gemserk.games.vampirerunner.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.ButtonHandler;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.resources.ResourceManager;

public class PauseGameState extends GameStateImpl {

	private final Game game;
	private ResourceManager<String> resourceManager;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public PauseGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		guiContainer = new Container();

		BitmapFont titleFont = resourceManager.getResourceValue("TitleFont");
		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont");

		guiContainer.add(GuiControls.label("GAME PAUSED") //
				.position(width * 0.5f, height * 0.95f) //
				.center(0.5f, 0.5f) //
				.font(titleFont) //
				.color(1f, 0f, 0f, 1f) //
				.build());

		guiContainer.add(GuiControls.textButton() //
				.id("TryAgainButton") //
				.text("Resume") //
				.boundsOffset(30f, 30f) //
				.font(buttonFont) //
				.notOverColor(1f, 1f, 0f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.position(width * 0.5f, height * 0.7f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						tryAgain();
					}
				}) //
				.build());

		guiContainer.add(GuiControls.textButton() //
				.id("MainMenuButton") //
				.text("Main menu") //
				.boundsOffset(30f, 30f) //
				.font(buttonFont) //
				.notOverColor(1f, 1f, 0f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.position(width * 0.5f, height * 0.5f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						nextScreen();
					}
				}) //
				.build());

	}

	private void tryAgain() {
		game.transition(game.getPlayGameScreen())//
				.disposeCurrent(true) //
				.start();
	}

	private void nextScreen() {
		game.transition(game.getInstructionsScreen())//
				.disposeCurrent(true) //
				.start();
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		game.getBackgroundGameScene().render();
		spriteBatch.begin();
		guiContainer.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());
		game.getBackgroundGameScene().update(getDeltaInMs());
		guiContainer.update();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

}
