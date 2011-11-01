package com.gemserk.games.vampirerunner.gamestates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.gui.ButtonHandler;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.gui.MultipleTextButtonContainer;
import com.gemserk.games.vampirerunner.preferences.GamePreferences;
import com.gemserk.resources.ResourceManager;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.gemserk.scores.Scores.Range;
import com.gemserk.util.concurrent.FutureHandler;
import com.gemserk.util.concurrent.FutureProcessor;

public class HighscoresGameState extends GameStateImpl {

	private class RefreshScoresCallable implements Callable<Collection<Score>> {

		private Range range;

		public RefreshScoresCallable(Range range) {
			this.range = range;
		}

		@Override
		public Collection<Score> call() throws Exception {
			Set<String> tags = new HashSet<String>();
			return scores.getOrderedByPoints(tags, 20, false, range);
		}
	}

	Game game;
	ResourceManager<String> resourceManager;
	Scores scores;
	ExecutorService executorService;

	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private ArrayList<Text> texts;
	private int viewportWidth;
	private int viewportHeight;

	private FutureHandler<Collection<Score>> scoresRefreshHandler = new FutureHandler<Collection<Score>>() {
		@Override
		public void done(Collection<Score> scores) {
			refreshScores(scores);
		}

		@Override
		public void failed(Exception e) {
			texts.clear();
			texts.add(new Text("Refresh scores failed...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f).setColor(Color.RED));
			if (e != null)
				Gdx.app.log("FaceHunt", e.getMessage(), e);
		}
	};

	private FutureProcessor<Collection<Score>> scoresRefreshProcessor;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private GamePreferences gamePreferences;

	private Container guiContainer;

	private Screen previousScreen;

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setScores(Scores scores) {
		this.scores = scores;
	}

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void setGamePreferences(GamePreferences gamePreferences) {
		this.gamePreferences = gamePreferences;
	}

	@Override
	public void init() {

		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		BitmapFont titleFont = resourceManager.getResourceValue("TitleFont");
		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont");
		font = resourceManager.getResourceValue("ScoresFont");

		font.setScale(1f);

		spriteBatch = new SpriteBatch();
		texts = new ArrayList<Text>();
		scoresRefreshProcessor = new FutureProcessor<Collection<Score>>(scoresRefreshHandler);

		reloadScores(Range.Day);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("back", Keys.BACK, Keys.ESCAPE, Keys.ENTER, Keys.SPACE);
			}
		};

		guiContainer = new Container("ButtonsPanel");

		guiContainer.add(GuiControls.label("HIGHSCORES") //
				.position(viewportWidth * 0.5f, viewportHeight * 0.95f) //
				.center(0.5f, 0.5f) //
				.font(titleFont) //
				.color(1f, 0f, 0f, 1f) //
				.build());

		MultipleTextButtonContainer buttonsPanel = new MultipleTextButtonContainer();

		buttonsPanel.add(GuiControls.textButton() //
				.id("AllButton") //
				.font(buttonFont) //
				.text("All") //
				.position(viewportWidth * 0.9f, viewportHeight * 0.85f) //
				.notOverColor(0.6f, 0.6f, 0.6f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.boundsOffset(40f, 40f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						reloadScores(Range.All);
					}
				}) //
				.build());

		buttonsPanel.add(GuiControls.textButton() //
				.id("MonthlyButton") //
				.font(buttonFont) //
				.text("Monthly") //
				.position(viewportWidth * 0.9f, viewportHeight * 0.65f) //
				.notOverColor(0.6f, 0.6f, 0.6f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.boundsOffset(40f, 40f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						reloadScores(Range.Month);
					}
				}) //
				.build());

		buttonsPanel.add(GuiControls.textButton() //
				.id("WeeklyButton") //
				.font(buttonFont) //
				.text("Weekly") //
				.position(viewportWidth * 0.9f, viewportHeight * 0.45f) //
				.notOverColor(0.6f, 0.6f, 0.6f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.boundsOffset(40f, 40f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						reloadScores(Range.Week);
					}
				}) //
				.build());

		buttonsPanel.add(GuiControls.textButton() //
				.id("DailyButton") //
				.font(buttonFont) //
				.text("Daily") //
				.position(viewportWidth * 0.9f, viewportHeight * 0.25f) //
				.notOverColor(0.6f, 0.6f, 0.6f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.boundsOffset(40f, 40f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						reloadScores(Range.Day);
					}
				}) //
				.build());

		buttonsPanel.select("DailyButton");

		guiContainer.add(buttonsPanel);

		String returnText = "Click here to return";
		if (Gdx.app.getType() == ApplicationType.Android)
			returnText = "Tap here to return";

		guiContainer.add(GuiControls.textButton() //
				.font(buttonFont) //
				.text(returnText) //
				.position(viewportWidth * 0.5f, viewportHeight * 0.1f) //
				.notOverColor(1f, 1f, 1f, 1f) //
				.overColor(1f, 0f, 0f, 1f) //
				.boundsOffset(40f, 40f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						previousScreen();
					}
				}) //
				.build());
		
		previousScreen = getParameters().get("previousScreen");
		
		// tapScreenText = new Text("Tap the screen to return", viewportWidth * 0.5f, viewportHeight * 0.1f).setColor(yellowColor);

	}

	private void previousScreen() {
		game.transition(previousScreen) //
				.disposeCurrent() //
				.start();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(true);
		game.getAdWhirlViewHandler().hide();
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		game.getBackgroundGameScene().render();
		spriteBatch.begin();

		for (int i = 0; i < texts.size(); i++) {
			Text text = texts.get(i);
			text.draw(spriteBatch, font);
		}

		guiContainer.draw(spriteBatch);

		spriteBatch.end();
	}

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());
		game.getBackgroundGameScene().update(getDeltaInMs());
		inputDevicesMonitor.update();
		scoresRefreshProcessor.update();
		guiContainer.update();
		
		if (inputDevicesMonitor.getButton("back").isReleased()) 
			previousScreen();
	}

	private void reloadScores(Range range) {
		texts.clear();
		texts.add(new Text("Refreshing scores...", viewportWidth * 0.5f, viewportHeight * 0.5f, 0.5f, 0.5f));
		Future<Collection<Score>> future = executorService.submit(new RefreshScoresCallable(range));
		scoresRefreshProcessor.setFuture(future);
	}

	private void refreshScores(Collection<Score> scoreList) {
		texts.clear();

		float x = viewportWidth * 0.5f;
		float y = viewportHeight * 0.9f;
		
		float limitHeight = viewportHeight * 0.2f;

		BitmapFont font = resourceManager.getResourceValue("ScoresFont");

		y -= font.getLineHeight() * font.getScaleY();

		int index = 1;

		Profile profile = gamePreferences.getProfile();

		for (Score score : scoreList) {
			
			if (y <= limitHeight)
				break;

			Color scoreColor = new Color(1f, 1f, 0f, 1f);

			if (profile.getPublicKey() != null && profile.getPublicKey().equals(score.getProfilePublicKey()))
				scoreColor = Color.RED;

			String name = score.getName();
			
			if (name.length() > Game.maxProfileNameLen)
				name = name.substring(0, Game.maxProfileNameLen);

			Text numberText = new Text("" + index + ". ", viewportWidth * 0.15f, y, 1f, 0.5f).setColor(scoreColor);
			Text nameText = new Text(name, viewportWidth * 0.15f, y, 0f, 0.5f).setColor(scoreColor);
			Text pointsText = new Text(Long.toString(score.getPoints()), viewportWidth * 0.7f, y, 1f, 0.5f).setColor(scoreColor);

			texts.add(numberText);
			texts.add(nameText);
			texts.add(pointsText);

			y -= font.getLineHeight() * font.getScaleY();
			index++;
		}
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}
}
