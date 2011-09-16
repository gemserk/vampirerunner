package com.gemserk.games.vampirerunner.gamestates;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.analytics.Analytics;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.components.Components.DistanceComponent;
import com.gemserk.games.vampirerunner.components.Components.SuperSkillComponent;
import com.gemserk.games.vampirerunner.preferences.GamePreferences;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.gemserk.scores.Scores.Range;
import com.gemserk.util.concurrent.FutureHandleCallable;
import com.gemserk.util.concurrent.FutureProcessor;

public class PlayGameState extends GameStateImpl {

	static class EnergyBarControl implements Control {

		private final String id;
		private float x, y;

		private Sprite barBackgroundSprite;
		private Sprite barForegroundSprite;
		
		private float percentage;

		public EnergyBarControl(String id, Sprite whiteRectangleSprite) {
			this.id = id;
			this.barBackgroundSprite = whiteRectangleSprite;
			this.barForegroundSprite = new Sprite(whiteRectangleSprite);
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public float getX() {
			return x;
		}

		@Override
		public float getY() {
			return y;
		}

		@Override
		public void setPosition(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public void setPercentage(float percentage) {
			this.percentage = percentage;
		}

		@Override
		public void update() {
			float totalWidth = Gdx.graphics.getWidth() * 0.8f;

			float y = Gdx.graphics.getHeight() * 0.85f;

			barBackgroundSprite.setPosition(Gdx.graphics.getWidth() * 0.05f, y);
			barBackgroundSprite.setColor(1f, 0f, 0f, 1f);
			barBackgroundSprite.setSize(totalWidth, 10f);

			barForegroundSprite.setPosition(Gdx.graphics.getWidth() * 0.05f, y);
			barForegroundSprite.setColor(0f, 0f, 1f, 1f);

			barForegroundSprite.setSize(totalWidth * percentage, 10f);
		}

		@Override
		public void draw(SpriteBatch spriteBatch) {
			
			barBackgroundSprite.draw(spriteBatch);
			barForegroundSprite.draw(spriteBatch);

		}

	}

	private final Game game;
	private ResourceManager<String> resourceManager;
	private WorldWrapper worldWrapper;

	// private Box2DCustomDebugRenderer box2dCustomDebugRenderer;

	private Container guiContainer;
	private SpriteBatch spriteBatch;
	// private Sprite whiteRectangle;
	// private Sprite whiteRectangle2;
	// private Resource<Music> musicResource;
	private FutureProcessor<Score> bestDailyScoreFutureProcessor;

	private Scores scores;
	private ExecutorService executorService;
	private GamePreferences gamePreferences;

	public void setGamePreferences(GamePreferences gamePreferences) {
		this.gamePreferences = gamePreferences;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setScores(Scores scores) {
		this.scores = scores;
	}

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public PlayGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		guiContainer = new Container();

		BitmapFont distanceFont = resourceManager.getResourceValue("DistanceFont");

		final Text distanceLabel = GuiControls.label("Score: ").id("DistanceLabel") //
				.position(width * 0.05f, height * 0.95f) //
				.center(0f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build();

		guiContainer.add(distanceLabel);

		guiContainer.add(GuiControls.label("Refreshing best score...") //
				.id("BestScoreLabel") //
				.position(width * 0.95f, height * 0.95f) //
				.center(1f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build());

		worldWrapper = new WorldWrapper(new World());

		NormalModeSceneTemplate normalModeSceneTemplate = new NormalModeSceneTemplate();
		normalModeSceneTemplate.setResourceManager(resourceManager);

		normalModeSceneTemplate.apply(worldWrapper);

		normalModeSceneTemplate.getEntityBuilder().component(new ScriptComponent(new ScriptJavaImpl() {
			@Override
			public void update(World world, Entity e) {
				Entity player = world.getTagManager().getEntity(Tags.Vampire);
				if (player == null)
					return;
				DistanceComponent distanceComponent = player.getComponent(DistanceComponent.class);
				distanceLabel.setText("Score: " + (int) distanceComponent.distance);
			}
		})).build();

		normalModeSceneTemplate.getEntityBuilder().component(new ScriptComponent(new ScriptJavaImpl() {

			private World world;

			@Override
			public void init(World world, Entity e) {
				this.world = world;
			}

			@Handles
			public void gameFinished(Event e) {
				Entity player = world.getTagManager().getEntity(Tags.Player);

				DistanceComponent distanceComponent = player.getComponent(DistanceComponent.class);
				int points = (int) distanceComponent.distance;

				Profile profile = gamePreferences.getProfile();
				Score score = new Score(profile.getName(), points, new HashSet<String>(), new HashMap<String, Object>());
				game.transition(game.getGameOverScreen()) //
						.parameter("score", score) //
						.disposeCurrent(true) //
						.start();
			}
		})).build();

		Sprite whiteRectangle = resourceManager.getResourceValue("WhiteRectangleSprite");

		EnergyBarControl healthBar = new EnergyBarControl("HealthBar", whiteRectangle);

		healthBar.setPosition(Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.85f);

		guiContainer.add(healthBar);

		normalModeSceneTemplate.getEntityBuilder().component(new ScriptComponent(new ScriptJavaImpl() {
			@Override
			public void update(World world, Entity e) {
				Entity vladimir = world.getTagManager().getEntity(Tags.Vampire);
				if (vladimir == null)
					return;
				SuperSkillComponent superSkillComponent = vladimir.getComponent(SuperSkillComponent.class);
				
				EnergyBarControl healthBar = guiContainer.findControl("HealthBar");
				if (healthBar == null)
					return;

				healthBar.setPercentage(superSkillComponent.energy.getPercentage());
			}
		})).build();

		// box2dCustomDebugRenderer = new Box2DCustomDebugRenderer(worldCamera, physicsWorld);

		refreshTodayBestScore();

		// musicResource = resourceManager.get("GameMusic");

		update();

		Analytics.traker.trackPageView("/startGame", "/startGame", null);
	}

	private void refreshTodayBestScore() {
		FutureHandleCallable<Score> bestDailyScoreFutureHandler = new FutureHandleCallable<Score>() {

			@Override
			public void failed(Exception e) {
				Gdx.app.log("VampireRunner", "Failed to retrieve best daily score", e);
			}

			@Override
			public void done(Score score) {
				Text bestScoreLabel = guiContainer.findControl("BestScoreLabel");
				if (bestScoreLabel == null)
					return;
				if (score != null)
					bestScoreLabel.setText("Today Highscore: " + score.getPoints());
				else
					bestScoreLabel.setText("No Highscore today yet");
			}

			@Override
			public Score call() throws Exception {
				Collection<Score> scoreList = scores.getOrderedByPoints(new HashSet<String>(), 1, false, Range.Day);
				if (scoreList.isEmpty())
					return null;
				return scoreList.iterator().next();
			}

		};

		Future<Score> future = executorService.submit(bestDailyScoreFutureHandler);
		bestDailyScoreFutureProcessor = new FutureProcessor<Score>(bestDailyScoreFutureHandler, future);
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldWrapper.render();

		// if (Game.isShowBox2dDebug())
		// box2dCustomDebugRenderer.render();

		spriteBatch.begin();
		guiContainer.draw(spriteBatch);
		spriteBatch.end();
	}

	// FloatTransition volumeTransition = new FloatTransition();

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());
		worldWrapper.update(getDeltaInMs());
		bestDailyScoreFutureProcessor.update();
		guiContainer.update();
		// volumeTransition.update(getDeltaInMs());
		// if (!volumeTransition.isFinished()) {
		// Music music = musicResource.get();
		// float volume = volumeTransition.get();
		// music.setVolume(volume);
		// }
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
		// Music music = musicResource.get();
		// if (!music.isPlaying()) {
		// music.setLooping(true);
		// music.play();
		// music.setVolume(1f);
		// // volumeTransition.set(0f);
		// // volumeTransition.set(1f, 1000);
		// }
	}

	@Override
	public void pause() {
		// Music music = musicResource.get();
		// if (music.isPlaying())
		// music.stop();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		worldWrapper.dispose();
	}

}
