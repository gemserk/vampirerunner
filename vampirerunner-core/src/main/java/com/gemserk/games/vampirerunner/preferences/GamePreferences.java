package com.gemserk.games.vampirerunner.preferences;

import com.badlogic.gdx.Preferences;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.ProfileJsonSerializer;

public class GamePreferences {
	
	private final Preferences preferences;

	public GamePreferences(Preferences preferences) {
		this.preferences = preferences;
	}
	
	public Profile getProfile() {
		String profileJson = preferences.getString("profile", "");
		Profile profile = new ProfileJsonSerializer().parse(profileJson);
		return profile;
	}

}
