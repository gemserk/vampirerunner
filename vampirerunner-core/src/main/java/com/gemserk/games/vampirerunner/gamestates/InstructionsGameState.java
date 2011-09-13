package com.gemserk.games.vampirerunner.gamestates;

import java.util.Set;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
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
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.preferences.GamePreferences;
import com.gemserk.resources.ResourceManager;

public class InstructionsGameState extends GameStateImpl {

	private final Game game;

	private ResourceManager<String> resourceManager;
	private Profiles profiles;
	private GamePreferences gamePreferences;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	// private InputAdapter inputProcessor = new InputAdapter() {
	// @Override
	// public boolean keyUp(int keycode) {
	// nextScreen();
	// return super.keyUp(keycode);
	// }
	//
	// @Override
	// public boolean touchUp(int x, int y, int pointer, int button) {
	// nextScreen();
	// return super.touchUp(x, y, pointer, button);
	// }
	// };

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}
	
	public void setGamePreferences(GamePreferences gamePreferences) {
		this.gamePreferences = gamePreferences;
	}
	
	public void setProfiles(Profiles profiles) {
		this.profiles = profiles;
	}

	public InstructionsGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		guiContainer = new Container();
		
		Profile profile = gamePreferences.getProfile();

		BitmapFont distanceFont = resourceManager.getResourceValue("DistanceFont");

		String[] instructions = new String[] { "Hold left click to move through walls", "click to start", "click here to change it" };

		if (Gdx.app.getType() == ApplicationType.Android)
			instructions = new String[] { "Touch and hold screen to move through walls", "touch to start", "tap here to change it" };

		guiContainer.add(GuiControls.label("How to play") //
				.position(width * 0.5f, height * 0.9f) //
				.center(0.5f, 0.5f) //
				.font(distanceFont) //
				.color(1f, 1f, 0f, 1f) //
				.build());

		guiContainer.add(GuiControls.label(instructions[0]).id("Instructions") //
				.position(width * 0.5f, height * 0.8f) //
				.center(0.5f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build());

		// guiContainer.add(GuiControls.label(instructions[1]).id("ClickToStart") //
		// .position(width * 0.5f, height * 0.3f) //
		// .center(0.5f, 0.5f) //
		// .font(distanceFont) //
		// .color(Color.RED) //
		// .build());

		guiContainer.add(GuiControls.textButton() //
				.id("ClickToStart") //
				.text(instructions[1]) //
				.position(width * 0.5f, height * 0.7f) //
				.center(0.5f, 0.5f) //
				.font(distanceFont) //
				.overColor(1f, 0f, 0f, 1f) //
				.notOverColor(1f, 1f, 1f, 1f) //
				.boundsOffset(40f, 40f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						nextScreen();
					}
				}) //
				.build());
		
		guiContainer.add(GuiControls.label("Playing as " + profile.getName()) //
				.id("ProfileLabel") //
				.position(width * 0.5f, height * 0.4f) //
				.center(0.5f, 0.5f) //
				.font(distanceFont) //
				.color(Color.RED) //
				.build());
		
		guiContainer.add(GuiControls.textButton() //
				.id("ChangeProfileButton") //
				.text(instructions[2]) //
				.position(width * 0.5f, height * 0.3f) //
				.center(0.5f, 0.5f) //
				.font(distanceFont) //
				.overColor(1f, 0f, 0f, 1f) //
				.notOverColor(1f, 1f, 1f, 1f) //
				.boundsOffset(40f, 40f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						changeProfileName();
					}
				}) //
				.build());


	}
	
	private void changeProfileName() {
		game.getScreen().pause();

		Gdx.input.getTextInput(new TextInputListener() {

			@Override
			public void input(String username) {
				Profile profile = gamePreferences.getProfile();
				
				if (!"".equals(username)) {

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
							// profile couldn't be updated... :(
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
					// changeUsernameButton.setText("Username: " + username + "\n(tap to change it)");
					
				}
				game.getScreen().resume();
			}

			@Override
			public void canceled() {
				game.getScreen().resume();
			}

		}, "Username", gamePreferences.getProfile().getName());

	}

	private void nextScreen() {
		game.transition(game.getPlayGameScreen())//
				.leaveTime(150) //
				.enterTime(150) //
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
		Synchronizers.synchronize(getDelta());
		guiContainer.update();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
		// Gdx.input.setInputProcessor(inputProcessor);
	}

	@Override
	public void pause() {
		// if (Gdx.input.getInputProcessor() == inputProcessor)
		// Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}
}
