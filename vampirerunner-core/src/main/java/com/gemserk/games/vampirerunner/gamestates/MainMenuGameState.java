package com.gemserk.games.vampirerunner.gamestates;

import java.util.Set;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
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
import com.gemserk.commons.gdx.gui.Panel;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.preferences.GamePreferences;
import com.gemserk.resources.ResourceManager;

public class MainMenuGameState extends GameStateImpl {

	private final Game game;

	private ResourceManager<String> resourceManager;
	private Profiles profiles;
	private GamePreferences gamePreferences;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void setGamePreferences(GamePreferences gamePreferences) {
		this.gamePreferences = gamePreferences;
	}

	public void setProfiles(Profiles profiles) {
		this.profiles = profiles;
	}

	public MainMenuGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		guiContainer = new Container();

		Profile profile = gamePreferences.getProfile();

		BitmapFont titleFont = resourceManager.getResourceValue("TitleFont");
		BitmapFont instructionsFont = resourceManager.getResourceValue("InstructionsFont");
		BitmapFont buttonFont = resourceManager.getResourceValue("ButtonFont");

		String[] instructions = new String[] { "Hold left click to move through walls", "<< click here to start >>", "<< click here to change it >>" };

		if (Gdx.app.getType() == ApplicationType.Android)
			instructions = new String[] { "Touch and hold screen to move through walls", "<< tap here to start >>", "<< tap here to change it >>" };

		Panel menuPanel = new Panel(0f, height * 0.5f);

		guiContainer.add(GuiControls.label("VAMPIRE RUNNER") //
				.position(width * 0.5f, height * 0.95f) //
				.center(0.5f, 0.5f) //
				.font(titleFont) //
				.color(1f, 0f, 0f, 1f) //
				.build());

		menuPanel.add(GuiControls.textButton() //
				.id("PlayButton") //
				.text("Play") //
				.position(width * 0.5f, height * 0.30f) //
				.center(0.5f, 0.5f) //
				.font(buttonFont) //
				.overColor(1f, 0f, 0f, 1f) //
				.notOverColor(1f, 1f, 0f, 1f) //
				.boundsOffset(40f, 20f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						startGame();
					}
				}) //
				.build());

		menuPanel.add(GuiControls.textButton() //
				.id("HighscoresButton") //
				.text("Highscores") //
				.position(width * 0.5f, height * 0.15f) //
				.center(0.5f, 0.5f) //
				.font(buttonFont) //
				.overColor(1f, 0f, 0f, 1f) //
				.notOverColor(1f, 1f, 0f, 1f) //
				.boundsOffset(40f, 20f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						highscoresScreen();
					}
				}) //
				.build());

		menuPanel.add(GuiControls.textButton() //
				.id("AboutButton") //
				.text("About us") //
				.position(width * 0.5f, height * 0f) //
				.center(0.5f, 0.5f) //
				.font(buttonFont) //
				.overColor(1f, 0f, 0f, 1f) //
				.notOverColor(1f, 1f, 0f, 1f) //
				.boundsOffset(40f, 20f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						aboutUsScreen();
					}
				}) //
				.build());

		Panel profilePanel = new Panel(0f, height * 0.27f);

		profilePanel.setWidth(width);
		profilePanel.setHeight(height);

		profilePanel.add(GuiControls.label("Playing as " + profile.getName()) //
				.id("ProfileLabel") //
				.position(profilePanel.getWidth() * 0.5f, profilePanel.getHeight() * 0.1f) //
				.center(0.5f, 0.5f) //
				.font(instructionsFont) //
				.color(Color.RED) //
				.build());

		profilePanel.add(GuiControls.textButton() //
				.id("ChangeProfileButton") //
				.text(instructions[2]) //
				// this stuff should be dynamic like telling relative to container position instead (50%, etc)
				.position(profilePanel.getWidth() * 0.5f, profilePanel.getHeight() * 0f) //
				.center(0.5f, 0.5f) //
				.font(buttonFont) //
				.overColor(1f, 0f, 0f, 1f) //
				.notOverColor(1f, 1f, 1f, 1f) //
				.boundsOffset(40f, 20f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						changeProfileName();
					}
				}) //
				.build());

		guiContainer.add(menuPanel);
		guiContainer.add(profilePanel);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("play", Keys.ENTER, Keys.SPACE);
			}
		};

	}

	private void changeProfileName() {
		game.getScreen().pause();

		Gdx.input.getTextInput(new TextInputListener() {

			@Override
			public void input(String username) {
				Profile profile = gamePreferences.getProfile();

				if (!"".equals(username)) {

					if (username.length() > Game.maxProfileNameLen)
						username = username.substring(0, Game.maxProfileNameLen);

					Set<Profile> profileList = gamePreferences.getSavedProfiles();

					boolean savedProfileFound = false;

					for (Profile savedProfile : profileList) {
						if (savedProfile.getName().equals(username)) {
							// use this profile as selected
							profile = savedProfile;
							gamePreferences.updateProfile(profile);
							savedProfileFound = true;
							break;
						}
					}

					if (profile.isGuest() && profile.getPublicKey() != null) {
						profile.setName(username);

						try {
							profile = profiles.update(profile);
							gamePreferences.updateProfile(profile);

						} catch (Exception e) {
							Gdx.app.log("Vampire Runner", e.getMessage(), e);
							game.getScreen().resume();
							return;
						}

					} else if (!savedProfileFound) {
						profile = new Profile(username, false);
						gamePreferences.updateProfile(profile);
					}

					Text profileLabel = guiContainer.findControl("ProfileLabel");
					profileLabel.setText("Playing as " + username);

				}
				game.getScreen().resume();
			}

			@Override
			public void canceled() {
				game.getScreen().resume();
			}

		}, "Username", gamePreferences.getProfile().getName());

	}

	private void startGame() {
		game.transition(game.getInstructionsScreen())//
				.disposeCurrent(true) //
				.start();
	}

	private void highscoresScreen() {
		game.transition(game.getHighscoresScreen())//
				.parameter("previousScreen", game.getMainMenuScreen()) //
				.start();
	}

	private void aboutUsScreen() {
		game.transition(game.getAboutScreen())//
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
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("play").isReleased())
			startGame();
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
