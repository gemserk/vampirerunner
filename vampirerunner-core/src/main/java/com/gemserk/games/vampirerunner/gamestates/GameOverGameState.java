package com.gemserk.games.vampirerunner.gamestates;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.ButtonHandler;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.preferences.GamePreferences;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.gemserk.util.concurrent.FutureHandleCallable;
import com.gemserk.util.concurrent.FutureHandler;
import com.gemserk.util.concurrent.FutureProcessor;

public class GameOverGameState extends GameStateImpl {

	class SubmitScoreCallable implements Callable<String> {

		private final Score score;
		private final Profile profile;

		private SubmitScoreCallable(Score score, Profile profile) {
			this.score = score;
			this.profile = profile;
		}

		@Override
		public String call() throws Exception {
			return scores.submit(profile.getPrivateKey(), score);
		}

	}

	class SubmitScoreHandler implements FutureHandler<String> {

		public void done(String scoreId) {
			Text scoreSubmitText = guiContainer.findControl("ScoresLabel");
			scoreSubmitText.setText("Score: " + score.getPoints() + " pts submitted!").setColor(Color.GREEN);
		}

		public void failed(Exception e) {
			Text scoreSubmitText = guiContainer.findControl("ScoresLabel");
			scoreSubmitText.setText("Score: " + score.getPoints() + " pts submit failed").setColor(Color.RED);
			if (e != null)
				Gdx.app.log("FaceHunt", e.getMessage(), e);
		}

	}

	private final Game game;
	private ResourceManager<String> resourceManager;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	private Scores scores;
	private Profiles profiles;
	private GamePreferences gamePreferences;
	private ExecutorService executorService;

	private Score score;
	private Profile profile;

	private FutureProcessor<String> submitScoreProcessor;
	private FutureProcessor<Profile> registerProfileProcessor;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void setScores(Scores scores) {
		this.scores = scores;
	}

	public void setProfiles(Profiles profiles) {
		this.profiles = profiles;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setGamePreferences(GamePreferences gamePreferences) {
		this.gamePreferences = gamePreferences;
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

		BitmapFont scoresFont = resourceManager.getResourceValue("ScoresFont");
		BitmapFont titleFont = resourceManager.getResourceValue("TitleFont");
		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont");

		score = getParameters().get("score");

		guiContainer.add(GuiControls.label("GAME OVER") //
				.position(width * 0.5f, height * 0.95f) //
				.center(0.5f, 0.5f) //
				.font(titleFont) //
				.color(1f, 0f, 0f, 1f) //
				.build());

		guiContainer.add(GuiControls.label("Score: " + score.getPoints() + " pts submitting...") //
				.id("ScoresLabel") //
				.position(width * 0.5f, height * 0.75f) //
				.center(0.5f, 0.5f) //
				.font(scoresFont) //
				.color(Color.RED) //
				.build());

		guiContainer.add(GuiControls.textButton() //
				.id("TryAgainButton") //
				.text("Try Again") //
				.boundsOffset(30f, 30f) //
				.font(buttonFont) //
				.notOverColor(1f, 1f, 0f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.position(width * 0.5f, height * 0.6f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						tryAgain();
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
						nextScreen();
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

		profile = gamePreferences.getProfile();

		submitScoreProcessor = new FutureProcessor<String>(new SubmitScoreHandler());

		FutureHandleCallable<Profile> registerProfileFutureHandler = new FutureHandleCallable<Profile>() {

			@Override
			public void done(Profile profile) {
				gamePreferences.updateProfile(profile);
				submitScoreProcessor.setFuture(executorService.submit(new SubmitScoreCallable(score, profile)));
			}

			@Override
			public void failed(Exception e) {
				Text scoreSubmitText = guiContainer.findControl("ScoresLabel");
				scoreSubmitText.setText("Score: " + score.getPoints() + " pts submit failed").setColor(Color.RED);
				if (e != null)
					Gdx.app.log("VampireRunner", e.getMessage(), e);
			}

			@Override
			public Profile call() throws Exception {
				if (profile.getPublicKey() != null)
					return profile;
				return profiles.register(profile.getName(), profile.isGuest());
			}

		};

		registerProfileProcessor = new FutureProcessor<Profile>(registerProfileFutureHandler);
		registerProfileProcessor.setFuture(executorService.submit(registerProfileFutureHandler));
	}

	private void tryAgain() {
		game.transition(game.getPlayGameScreen())//
				.disposeCurrent(true) //
				.restartScreen() //
				.start();
	}
	
	private void mainMenuScreen() {
		game.transition(game.getInstructionsScreen())//
				.disposeCurrent(true) //
				.start();
	}

	private void nextScreen() {
		game.transition(game.getHighscoresScreen())//
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
		registerProfileProcessor.update();
		submitScoreProcessor.update();
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
