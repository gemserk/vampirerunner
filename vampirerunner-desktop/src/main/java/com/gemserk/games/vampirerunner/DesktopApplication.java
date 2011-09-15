package com.gemserk.games.vampirerunner;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.datastore.profiles.ProfilesFileImpl;
import com.gemserk.datastore.profiles.ProfilesHttpImpl;
import com.gemserk.scores.ScoreSerializerJSONImpl;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresFileImpl;
import com.gemserk.scores.ScoresHttpImpl;

public class DesktopApplication {

	protected static final Logger logger = LoggerFactory.getLogger(DesktopApplication.class);

	public static void main(String[] argv) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Gemserk's Vampire Runner";
		config.width = 800;
		config.height = 480;
		// config.width = 320;
		// config.height = 240;
		config.fullscreen = false;
		config.useGL20 = false;
		config.useCPUSynch = true;
		config.forceExit = true;
		config.vSyncEnabled = true;

		Game game = new Game();

		boolean runningInDebug = System.getProperty("runningInDebug") != null;

		Scores scores = new ScoresHttpImpl("f3ba5a778d0996ffffae1088dd1773341c068552", "http://gemserkscores.appspot.com", new ScoreSerializerJSONImpl());
		Profiles profiles = new ProfilesHttpImpl("http://gemserkscores.appspot.com");

		if (runningInDebug) {
			profiles = new ProfilesFileImpl(new File("/tmp/gemserk/vampirerunner/profiles.json"));
			scores = new ScoresFileImpl(new File("/tmp/gemserk/vampirerunner/scores.json"), profiles);
		}

		game.setScores(scores);
		game.setProfiles(profiles);

		new LwjglApplication(game, config);
	}

}
