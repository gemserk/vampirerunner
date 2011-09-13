package com.gemserk.games.vampirerunner.preferences;

import java.util.Set;

import com.badlogic.gdx.Preferences;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.ProfileJsonSerializer;

public class GamePreferences {
	
	private final Preferences preferences;
	private final ProfileJsonSerializer profileJsonSerializer;

	public GamePreferences(Preferences preferences) {
		this.preferences = preferences;
		this.profileJsonSerializer = new ProfileJsonSerializer();
	}
	
	public Profile getProfile() {
		String profileJson = preferences.getString("profile", "");
		Profile profile = profileJsonSerializer.parse(profileJson);
		return profile;
	}
	
	/**
	 * Updates the current profile with values of specified profile, it also updates saved profile list.
	 */
	public void updateProfile(Profile profile) {
		String profilesListJson = preferences.getString("profiles", "[]");
		Set<Profile> profileList = profileJsonSerializer.parseList(profilesListJson);
		profileList.remove(profile);
		profileList.add(profile);
		preferences.putString("profiles", profileJsonSerializer.serialize(profileList));
		preferences.putString("profile", profileJsonSerializer.serialize(profile));
		preferences.flush();
	}

}
