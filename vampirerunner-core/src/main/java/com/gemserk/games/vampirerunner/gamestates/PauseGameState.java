package com.gemserk.games.vampirerunner.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.ButtonHandler;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.resources.ResourceManager;

public class PauseGameState extends GameStateImpl {

	Game game;
	ResourceManager<String> resourceManager;

	private Container guiContainer;
	private SpriteBatch spriteBatch;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;
	private WorldWrapper scene;
	private Sprite whiteRectangle;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		guiContainer = new Container();

		BitmapFont titleFont = resourceManager.getResourceValue("TitleFont");
		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont");

		whiteRectangle = resourceManager.getResourceValue("WhiteRectangleSprite");

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
				.position(width * 0.5f, height * 0.6f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						resumeGame();
					}
				}) //
				.build());

		guiContainer.add(GuiControls.textButton() //
				.id("HighscoresButton") //
				.text("Highscores") //
				.boundsOffset(30f, 30f) //
				.font(buttonFont) //
				.notOverColor(1f, 1f, 0f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.position(width * 0.5f, height * 0.45f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						highscoresScreen();
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
				.position(width * 0.5f, height * 0.3f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						mainMenuScreen();
					}
				}) //
				.build());

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("back", Keys.BACK, Keys.ESCAPE, Keys.SPACE, Keys.ENTER);
			}
		};

		scene = getParameters().get("scene");

	}

	private void highscoresScreen() {
		game.transition(game.getHighscoresScreen())//
				.disposeCurrent() //
				.parameter("previousScreen", game.getPauseScreen()) //
				.start();
	}

	private void resumeGame() {
		game.transition(game.getPlayGameScreen())//
				.disposeCurrent(true) //
				.start();
	}

	private void mainMenuScreen() {
		game.transition(game.getMainMenuScreen())//
				.disposeCurrent(true) //
				.start();
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		scene.render();

		whiteRectangle.setPosition(0, 0);
		whiteRectangle.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		whiteRectangle.setColor(0f, 0f, 0f, 0.5f);

		spriteBatch.begin();
		whiteRectangle.draw(spriteBatch);

		guiContainer.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());
		guiContainer.update();
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("back").isReleased())
			resumeGame();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
		game.getAdWhirlViewHandler().show();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

}
