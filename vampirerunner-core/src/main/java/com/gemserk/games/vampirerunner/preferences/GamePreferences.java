package com.gemserk.games.vampirerunner.preferences;

import java.util.Set;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
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

		if (profileJson != null && !"".equals(profileJson))
			return profileJsonSerializer.parse(profileJson);

		Profile profile = new Profile("guest-" + MathUtils.random(10000, 99999), true);

		preferences.putString("profile", profileJsonSerializer.serialize(profile));
		preferences.flush();

		return profile;
	}
	
	/**
	 * Returns locally saved profiles.
	 */
	public Set<Profile> getSavedProfiles() {
		String profilesListJson = preferences.getString("profiles", "[]");
		return profileJsonSerializer.parseList(profilesListJson);
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
