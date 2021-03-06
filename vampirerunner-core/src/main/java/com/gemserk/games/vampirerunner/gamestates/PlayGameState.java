package com.gemserk.games.vampirerunner.gamestates;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.commons.gdx.time.TimeStepProviderGameStateImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.components.DistanceComponent;
import com.gemserk.games.vampirerunner.components.SuperSkillComponent;
import com.gemserk.games.vampirerunner.gui.EnergyBarControl;
import com.gemserk.games.vampirerunner.preferences.GamePreferences;
import com.gemserk.games.vampirerunner.scenes.NormalModeSceneTemplate;
import com.gemserk.games.vampirerunner.templates.PositionLabelTemplate;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.gemserk.scores.Scores.Range;
import com.gemserk.util.concurrent.FutureHandleCallable;
import com.gemserk.util.concurrent.FutureProcessor;

public class PlayGameState extends GameStateImpl {

	Game game;
	ResourceManager<String> resourceManager;
	Scores scores;
	ExecutorService executorService;
	GamePreferences gamePreferences;
	
	private WorldWrapper worldWrapper;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	private FutureProcessor<Collection<Score>> bestDailyScoreFutureProcessor;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

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

		guiContainer.add(GuiControls.label("") //
				.id("BestScoreLabel") //
				.position(width * 0.95f, height * 0.95f) //
				.center(1f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build());

		worldWrapper = new WorldWrapper(new World());

		NormalModeSceneTemplate normalModeSceneTemplate = new NormalModeSceneTemplate();
		normalModeSceneTemplate.setResourceManager(resourceManager);
		
		normalModeSceneTemplate.setTimeStepProvider(new TimeStepProviderGameStateImpl(this));

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
						.start();
			}
		})).build();

		Sprite whiteRectangle = resourceManager.getResourceValue("WhiteRectangleSprite");

		EnergyBarControl healthBar = new EnergyBarControl("HealthBar", whiteRectangle);

		healthBar.setPosition(Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.85f);
		healthBar.setWidthPercentage(0.9f);
		healthBar.setHeight(10f);

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

				healthBar.setPercentage(superSkillComponent.energy);
			}
		})).build();

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("back", Keys.BACK, Keys.ESCAPE, Keys.MENU);
			}
		};

		refreshTodayBestScore();

		update();

		Analytics.traker.trackPageView("/startGame", "/startGame", null);
	}

	private void refreshTodayBestScore() {
		FutureHandleCallable<Collection<Score>> bestDailyScoreFutureHandler = new FutureHandleCallable<Collection<Score>>() {

			@Override
			public void failed(Exception e) {
				Gdx.app.log("VampireRunner", "Failed to retrieve best daily score", e);
			}

			@Override
			public void done(Collection<Score> scores) {
				Text bestScoreLabel = guiContainer.findControl("BestScoreLabel");
				if (bestScoreLabel == null)
					return;

				if (!scores.isEmpty()) {
					Score score = scores.iterator().next();
					bestScoreLabel.setText("Today Highscore: " + score.getPoints());
				} else
					bestScoreLabel.setText("No scores today, yet");
				
				EntityFactory entityFactory = new EntityFactoryImpl(worldWrapper.getWorld());
				EntityTemplate positionLabelTemplate = new PositionLabelTemplate();
				
				int position = 1;
				for (Score score : scores) {
					
					Sprite bloodSprite = resourceManager.getResourceValue("BloodSprite");
					
					SpriteUtils.resize(bloodSprite, bloodSprite.getWidth() * Gdx.graphics.getWidth() / 800f);
					
					entityFactory.instantiate(positionLabelTemplate, new ParametersWrapper() //
							.put("score", score) //
							.put("position", position++) //
							.put("spatial", new SpatialImpl(score.getPoints(), 0.5f, bloodSprite.getWidth(), bloodSprite.getHeight(), 0f)) //
							.put("sprite", bloodSprite) //
							);
				}
			}

			@Override
			public Collection<Score> call() throws Exception {
				Collection<Score> scoreList = scores.getOrderedByPoints(new HashSet<String>(), 25, false, Range.Day);
				return scoreList;
			}

		};

		Future<Collection<Score>> future = executorService.submit(bestDailyScoreFutureHandler);
		bestDailyScoreFutureProcessor = new FutureProcessor<Collection<Score>>(bestDailyScoreFutureHandler, future);
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

		inputDevicesMonitor.update();
		if (inputDevicesMonitor.getButton("back").isReleased())
			pauseScreen();
	}

	private void pauseScreen() {
		game.transition(game.getPauseScreen())//
				.disposeCurrent(false) //
				.parameter("scene", worldWrapper)//
				.start();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(true);
		game.getAdWhirlViewHandler().hide();
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
