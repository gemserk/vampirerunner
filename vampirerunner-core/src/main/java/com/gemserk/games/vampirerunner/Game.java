package com.gemserk.games.vampirerunner;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.analytics.Analytics;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.events.reflection.EventListenerReflectionRegistrator;
import com.gemserk.commons.gdx.GameState;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.screens.transitions.TransitionBuilder;
import com.gemserk.commons.gdx.time.TimeStepProviderGlobalImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.commons.utils.BrowserUtils;
import com.gemserk.commons.utils.BrowserUtilsNullImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.games.vampirerunner.gamestates.AboutGameState;
import com.gemserk.games.vampirerunner.gamestates.GameOverGameState;
import com.gemserk.games.vampirerunner.gamestates.HighscoresGameState;
import com.gemserk.games.vampirerunner.gamestates.InstructionsGameState;
import com.gemserk.games.vampirerunner.gamestates.MainMenuGameState;
import com.gemserk.games.vampirerunner.gamestates.PauseGameState;
import com.gemserk.games.vampirerunner.gamestates.PlayGameState;
import com.gemserk.games.vampirerunner.gamestates.SplashGameState;
import com.gemserk.games.vampirerunner.preferences.GamePreferences;
import com.gemserk.games.vampirerunner.resources.GameResources;
import com.gemserk.games.vampirerunner.scenes.BackgroundSceneTemplate;
import com.gemserk.scores.Scores;
import com.gemserk.util.ScreenshotSaver;

public class Game extends com.gemserk.commons.gdx.Game {

	public static final int maxProfileNameLen = 15;

	private static boolean showFps = false;
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

	private CustomResourceManager<String> resourceManager;
	private BitmapFont fpsFont;
	private SpriteBatch spriteBatch;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private EventManager eventManager;
	private BrowserUtils browserUtils = new BrowserUtilsNullImpl();
	private AdWhirlViewHandler adWhirlViewHandler = new AdWhirlViewHandler();

	/**
	 * Used to store global information about the game and to send data between GameStates and Screens.
	 */
	private Parameters gameData;

	private Screen playGameScreen;
	private Screen gameOverScreen;
	private Screen instructionsScreen;
	private Screen highscoresScreen;
	private Screen pauseScreen;
	private Screen mainMenuScreen;
	private Screen aboutScreen;

	private WorldWrapper backgroundGameScene;

	public Scores scores;
	public Profiles profiles;

	public Screen getSplashScreen() {
		return splashScreen;
	}

	public Screen getMainMenuScreen() {
		return mainMenuScreen;
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

	public Screen getHighscoresScreen() {
		return highscoresScreen;
	}

	public Screen getPauseScreen() {
		return pauseScreen;
	}

	public Screen getAboutScreen() {
		return aboutScreen;
	}

	public Parameters getGameData() {
		return gameData;
	}

	public CustomResourceManager<String> getResourceManager() {
		return resourceManager;
	}

	/**
	 * Used to communicate between gamestates.
	 */
	public EventManager getEventManager() {
		return eventManager;
	}

	public WorldWrapper getBackgroundGameScene() {
		return backgroundGameScene;
	}

	public void setScores(Scores scores) {
		this.scores = scores;
	}

	public void setProfiles(Profiles profiles) {
		this.profiles = profiles;
	}

	public void setBrowserUtils(BrowserUtils browserUtils) {
		this.browserUtils = browserUtils;
	}

	public AdWhirlViewHandler getAdWhirlViewHandler() {
		return adWhirlViewHandler;
	}

	public void setAdWhirlViewHandler(AdWhirlViewHandler adWhirlViewHandler) {
		this.adWhirlViewHandler = adWhirlViewHandler;
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

		ExecutorService executorService = Executors.newCachedThreadPool();
		Preferences preferences = Gdx.app.getPreferences("gemserk-vampirerunner");

		GamePreferences gamePreferences = new GamePreferences(preferences);

		eventManager = new EventManagerImpl();
		resourceManager = new CustomResourceManager<String>();

		GameResources.load(resourceManager);

		// fpsFont = resourceManager.getResourceValue("FpsFont");
		fpsFont = new BitmapFont();
		spriteBatch = new SpriteBatch();

		Injector injector = new InjectorImpl();

		injector.bind("game", this);
		injector.bind("resourceManager", resourceManager);
		injector.bind("gamePreferences", gamePreferences);
		injector.bind("executorService", executorService);
		injector.bind("scores", scores);
		injector.bind("profiles", profiles);
		injector.bind("browserUtils", browserUtils);

		GameState playGameState = injector.getInstance(PlayGameState.class);
		GameState gameOverGameState = injector.getInstance(GameOverGameState.class);
		GameState instructionsGameState = injector.getInstance(InstructionsGameState.class);
		GameState highscoresGameState = injector.getInstance(HighscoresGameState.class);
		GameState splashGameState = injector.getInstance(SplashGameState.class);
		GameState pauseGameState = injector.getInstance(PauseGameState.class);
		GameState mainMenuGameState = injector.getInstance(MainMenuGameState.class);
		GameState aboutGameState = injector.getInstance(AboutGameState.class);

		// PlayGameState playGameState = new PlayGameState(this);
		// playGameState.setResourceManager(resourceManager);
		// playGameState.setGamePreferences(gamePreferences);
		// playGameState.setExecutorService(executorService);
		// playGameState.setScores(scores);

		// GameOverGameState gameOverGameState = new GameOverGameState(this);
		// gameOverGameState.setResourceManager(resourceManager);
		// gameOverGameState.setGamePreferences(gamePreferences);
		// gameOverGameState.setExecutorService(executorService);
		// gameOverGameState.setProfiles(profiles);
		// gameOverGameState.setScores(scores);

		// InstructionsGameState instructionsGameState = new InstructionsGameState(this);
		// instructionsGameState.setResourceManager(resourceManager);
		//
		// HighscoresGameState highscoresGameState = new HighscoresGameState(this);
		// highscoresGameState.setResourceManager(resourceManager);
		// highscoresGameState.setExecutorService(executorService);
		// highscoresGameState.setGamePreferences(gamePreferences);
		// highscoresGameState.setScores(scores);

		// SplashGameState splashGameState = new SplashGameState(this);
		// splashGameState.setResourceManager(resourceManager);
		//
		// PauseGameState pauseGameState = new PauseGameState(this);
		// pauseGameState.setResourceManager(resourceManager);
		//
		// MainMenuGameState mainMenuGameState = new MainMenuGameState(this);
		// mainMenuGameState.setGamePreferences(gamePreferences);
		// mainMenuGameState.setResourceManager(resourceManager);
		// mainMenuGameState.setProfiles(profiles);
		//
		// AboutGameState aboutGameState = new AboutGameState(this);
		// aboutGameState.setResourceManager(resourceManager);
		// aboutGameState.setBrowserUtils(browserUtils);

		splashScreen = new ScreenImpl(splashGameState);
		playGameScreen = new ScreenImpl(playGameState);
		gameOverScreen = new ScreenImpl(gameOverGameState);
		instructionsScreen = new ScreenImpl(instructionsGameState);
		highscoresScreen = new ScreenImpl(highscoresGameState);
		pauseScreen = new ScreenImpl(pauseGameState);
		mainMenuScreen = new ScreenImpl(mainMenuGameState);
		aboutScreen = new ScreenImpl(aboutGameState);

		EventListenerReflectionRegistrator registrator = new EventListenerReflectionRegistrator(eventManager);

		registrator.registerEventListeners(this);

		setScreen(splashScreen);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("grabScreenshot", Keys.NUM_9);
				monitorKey("toggleBox2dDebug", Keys.NUM_8);
				monitorKey("toggleFps", Keys.NUM_7);
				monitorKey("restartScreen", Keys.NUM_1);
			}
		};

		Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);

		BackgroundSceneTemplate backgroundSceneTemplate = new BackgroundSceneTemplate();
		backgroundSceneTemplate.setResourceManager(resourceManager);

		backgroundGameScene = new WorldWrapper(new World());

		backgroundSceneTemplate.setTimeStepProvider(new TimeStepProviderGlobalImpl());
		backgroundSceneTemplate.apply(backgroundGameScene);

		String version = getGameData().get("version");

		Analytics.traker.trackPageView("/start", "/start", null);
		Analytics.traker.trackPageView("/info/version/" + version, "/info/version/" + version, null);
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
				ScreenshotSaver.saveScreenshot("vampirerunner");
			} catch (IOException e) {
				Gdx.app.log("SuperFlyingThing", "Can't save screenshot");
			}
		}

		if (inputDevicesMonitor.getButton("restartScreen").isReleased()) {
			getScreen().restart();

			backgroundGameScene.dispose();

			BackgroundSceneTemplate backgroundSceneTemplate = new BackgroundSceneTemplate();
			backgroundSceneTemplate.setResourceManager(resourceManager);

			backgroundGameScene = new WorldWrapper(new World());

			backgroundSceneTemplate.setTimeStepProvider(new TimeStepProviderGlobalImpl());
			backgroundSceneTemplate.apply(backgroundGameScene);
		}

		if (inputDevicesMonitor.getButton("toggleFps").isReleased())
			setShowFps(!isShowFps());

		if (inputDevicesMonitor.getButton("toggleBox2dDebug").isReleased())
			setShowBox2dDebug(!isShowBox2dDebug());

		eventManager.process();
	}

	public TransitionBuilder transition(Screen screen) {
		return new TransitionBuilder(this, screen);
	}

	@Override
	public void pause() {
		super.pause();
		adWhirlViewHandler.hide();
	}

	@Override
	public void dispose() {
		super.dispose();
		resourceManager.unloadAll();
		spriteBatch.dispose();
		getBackgroundGameScene().dispose();
	}

}
