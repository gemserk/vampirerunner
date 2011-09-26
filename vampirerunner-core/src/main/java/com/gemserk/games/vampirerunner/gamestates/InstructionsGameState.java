package com.gemserk.games.vampirerunner.gamestates;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.resources.ResourceManager;

public class InstructionsGameState extends GameStateImpl {

	private final Game game;

	private ResourceManager<String> resourceManager;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public InstructionsGameState(Game game) {
		this.game = game;
	}

	private InputProcessor inputProcessor = new InputAdapter() {

		public boolean keyUp(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.ESCAPE)
				mainMenu();
			else
				startGame();
			return super.keyUp(keycode);
		}

		public boolean touchUp(int x, int y, int pointer, int button) {
			startGame();
			return super.touchUp(x, y, pointer, button);
		}

	};

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		guiContainer = new Container();

		BitmapFont titleFont = resourceManager.getResourceValue("TitleFont");
		BitmapFont instructionsFont = resourceManager.getResourceValue("InstructionsFont");
		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont");

		String[] instructions = new String[] { "Hold left click to move through walls,\nrelease it to recover energy.\n\n\nObjective: Run as far as you can.", "<< click to start >>" };

		if (Gdx.app.getType() == ApplicationType.Android)
			instructions = new String[] { "Touch and hold screen to move through walls,\nrelease it to recover energy.\n\n\nObjective: Run as far as you can.", "<< tap to start >>" };

		guiContainer.add(GuiControls.label("HOW TO PLAY") //
				.position(width * 0.5f, height * 0.95f) //
				.center(0.5f, 0.5f) //
				.font(titleFont) //
				.color(1f, 0f, 0f, 1f) //
				.build());

		guiContainer.add(GuiControls.label(instructions[0]).id("Instructions") //
				.position(width * 0.5f, height * 0.65f) //
				.center(0.5f, 0.5f) //
				.font(instructionsFont) //
				.color(1f, 0f, 0f, 1f) //
				.build());

		guiContainer.add(GuiControls.label(instructions[1]) //
				.id("ClickToStart") //
				.position(width * 0.5f, height * 0.1f) //
				.center(0.5f, 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.font(buttonFont) //
				.build());
	}

	private void startGame() {
		game.transition(game.getPlayGameScreen())//
				.leaveTime(0.1f) //
				.enterTime(0.1f) //
				.restartScreen() //
				.disposeCurrent(true) //
				.start();
	}

	private void mainMenu() {
		game.transition(game.getMainMenuScreen())//
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
		guiContainer.update();
		game.getBackgroundGameScene().update(getDeltaInMs());
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputProcessor);
		game.getAdWhirlViewHandler().hide();
	}

	@Override
	public void pause() {
		if (Gdx.input.getInputProcessor() == inputProcessor)
			Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}
}
