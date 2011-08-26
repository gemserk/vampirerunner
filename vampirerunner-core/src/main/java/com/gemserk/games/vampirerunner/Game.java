package com.gemserk.games.vampirerunner;

import java.io.IOException;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.events.reflection.EventListenerReflectionRegistrator;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.gamestates.GameOverGameState;
import com.gemserk.games.vampirerunner.gamestates.InstructionsGameState;
import com.gemserk.games.vampirerunner.gamestates.PlayGameState;
import com.gemserk.games.vampirerunner.gamestates.SplashGameState;
import com.gemserk.games.vampirerunner.resources.GameResources;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.util.ScreenshotSaver;

public class Game extends com.gemserk.commons.gdx.Game {

	private static boolean showFps = true;
	private static boolean showBox2dDebug = false;

	public static void setShowFps(boolean showFps) {
		Game.showFps = showFps;
	}

	public static boolean isShowFps() {
		return showFps;
	}

	public static boolean isShowBox2dDebug() {
		return showBox2dDebug;
	}

	public static void setShowBox2dDebug(boolean showBox2dDebug) {
		Game.showBox2dDebug = showBox2dDebug;
	}

	private Screen splashScreen;

	private ResourceManager<String> resourceManager;
	private BitmapFont fpsFont;
	private SpriteBatch spriteBatch;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private EventManager eventManager;

	/**
	 * Used to store global information about the game and to send data between GameStates and Screens.
	 */
	private Parameters gameData;

	private Screen playGameScreen;
	private Screen gameOverScreen;
	private Screen instructionsScreen;

	public Screen getSplashScreen() {
		return splashScreen;
	}

	public Screen getPlayGameScreen() {
		return playGameScreen;
	}

	public Screen getGameOverScreen() {
		return gameOverScreen;
	}

	public Screen getInstructionsScreen() {
		return instructionsScreen;
	}

	public Parameters getGameData() {
		return gameData;
	}

	/**
	 * Used to communicate between gamestates.
	 */
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public void create() {
		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());
		Converters.register(Float.class, Converters.floatValue());

		gameData = new ParametersWrapper();

		try {
			Properties properties = new Properties();
			properties.load(Gdx.files.classpath("version.properties").read());
			getGameData().put("version", properties.getProperty("version"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		eventManager = new EventManagerImpl();
		resourceManager = new ResourceManagerImpl<String>();

		GameResources.load(resourceManager);

		// fpsFont = resourceManager.getResourceValue("FpsFont");
		fpsFont = new BitmapFont();
		spriteBatch = new SpriteBatch();

		PlayGameState playGameState = new PlayGameState(this);
		playGameState.setResourceManager(resourceManager);

		GameOverGameState gameOverGameState = new GameOverGameState(this);
		gameOverGameState.setResourceManager(resourceManager);

		InstructionsGameState instructionsGameState = new InstructionsGameState(this);
		instructionsGameState.setResourceManager(resourceManager);

		splashScreen = new ScreenImpl(new SplashGameState(this));
		playGameScreen = new ScreenImpl(playGameState);
		gameOverScreen = new ScreenImpl(gameOverGameState);
		instructionsScreen = new ScreenImpl(instructionsGameState);

		EventListenerReflectionRegistrator registrator = new EventListenerReflectionRegistrator(eventManager);

		registrator.registerEventListeners(this);

		setScreen(splashScreen);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("grabScreenshot", Keys.NUM_9);
				monitorKey("toggleBox2dDebug", Keys.NUM_8);
			}
		};

		Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);
	}

	@Override
	public void render() {

		GlobalTime.setDelta(Gdx.graphics.getDeltaTime());

		inputDevicesMonitor.update();

		super.render();

		spriteBatch.begin();
		if (showFps)
			SpriteBatchUtils.drawMultilineText(spriteBatch, fpsFont, "FPS: " + Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth() * 0.02f, Gdx.graphics.getHeight() * 0.90f, 0f, 0.5f);
		spriteBatch.end();

		if (inputDevicesMonitor.getButton("grabScreenshot").isReleased()) {
			try {
				ScreenshotSaver.saveScreenshot("superflyingthing");
			} catch (IOException e) {
				Gdx.app.log("SuperFlyingThing", "Can't save screenshot");
			}
		}

		if (inputDevicesMonitor.getButton("toggleBox2dDebug").isReleased()) {
			setShowBox2dDebug(!isShowBox2dDebug());
		}

		eventManager.process();
	}

	public static class TransitionBuilder {

		private final Screen screen;
		private final Game game;
		private float leaveTime = 0f;
		private float enterTime = 0f;
		private boolean shouldDisposeCurrentScreen;

		public TransitionBuilder leaveTime(float leaveTime) {
			this.leaveTime = leaveTime;
			return this;
		}

		public TransitionBuilder enterTime(float enterTime) {
			this.enterTime = enterTime;
			return this;
		}

		public TransitionBuilder disposeCurrent(boolean disposeCurrent) {
			this.shouldDisposeCurrentScreen = disposeCurrent;
			return this;
		}

		public TransitionBuilder(final Game game, final Screen screen) {
			this.game = game;
			this.screen = screen;
		}

		public void start() {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					game.setScreen(screen, shouldDisposeCurrentScreen);
				}
			});
		}

	}

	public TransitionBuilder transition(Screen screen) {
		return new TransitionBuilder(this, screen);
	}

	@Override
	public void dispose() {
		super.dispose();
		resourceManager.unloadAll();
		spriteBatch.dispose();
	}

}
