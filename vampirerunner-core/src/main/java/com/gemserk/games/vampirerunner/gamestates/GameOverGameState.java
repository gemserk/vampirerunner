package com.gemserk.games.vampirerunner.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.GameInformation;
import com.gemserk.resources.ResourceManager;

public class GameOverGameState extends GameStateImpl {

	private final Game game;
	private ResourceManager<String> resourceManager;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	// private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public GameOverGameState(Game game) {
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

		GameInformation gameInformation = game.getGameData().get("gameInformation");

		BitmapFont distanceFont = resourceManager.getResourceValue("DistanceFont");

		final Text distanceLabel = GuiControls.label("Score: " + gameInformation.score).id("DistanceLabel") //
				.position(width * 0.5f, height * 0.5f) //
				.center(0.5f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build();

		guiContainer.add(distanceLabel);

		// inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		//
		// new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
		// {
		// monitorMouseLeftButton("tryAgain");
		// }
		// };

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyUp(int keycode) {
				game.setScreen(game.getPlayGameScreen(), true);
				return super.keyUp(keycode);
			}

			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				game.setScreen(game.getPlayGameScreen(), true);
				return super.touchUp(x, y, pointer, button);
			}
		});
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
		// inputDevicesMonitor.update();
		// if (inputDevicesMonitor.getButton("tryAgain").isReleased())
		// game.setScreen(game.getPlayGameScreen(), true);
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}
	
	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
		// world.dispose();
	}

}