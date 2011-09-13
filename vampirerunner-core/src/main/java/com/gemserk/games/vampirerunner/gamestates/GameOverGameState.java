package com.gemserk.games.vampirerunner.gamestates;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

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
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.preferences.GamePreferences;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
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

	private InputAdapter inputProcessor = new InputAdapter() {
		@Override
		public boolean keyUp(int keycode) {
			nextScreen();
			return super.keyUp(keycode);
		}

		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			nextScreen();
			return super.touchUp(x, y, pointer, button);
		}
	};

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

		score = getParameters().get("score");

		final Text distanceLabel = GuiControls.label("Score: " + score.getPoints() + " pts submitting...") //
				.id("ScoresLabel") //
				.position(width * 0.5f, height * 0.5f) //
				.center(0.5f, 0.5f) //
				.font(scoresFont) //
				.color(Color.RED) //
				.build();

		guiContainer.add(distanceLabel);
		
		profile = gamePreferences.getProfile();

		submitScoreProcessor = new FutureProcessor<String>(new SubmitScoreHandler());
		registerProfileProcessor = new FutureProcessor<Profile>(new FutureHandler<Profile>() {

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

		});
		registerProfileProcessor.setFuture(executorService.submit(new Callable<Profile>() {
			@Override
			public Profile call() throws Exception {
				if (profile.getPublicKey() != null)
					return profile;
				return profiles.register(profile.getName(), profile.isGuest());
			}
		}));
	}
	
	private void nextScreen() {
		game.transition(game.getHighscoresScreen())//
				.disposeCurrent(true) //
				.start();
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
		Gdx.input.setInputProcessor(inputProcessor);
		Synchronizers.synchronize(getDelta());
		
		registerProfileProcessor.update();
		submitScoreProcessor.update();
		
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
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
