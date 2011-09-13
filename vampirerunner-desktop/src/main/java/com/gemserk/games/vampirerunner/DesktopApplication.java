package com.gemserk.games.vampirerunner;

import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gemserk.datastore.profiles.ProfilesMemoryImpl;
import com.gemserk.scores.Score;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresMemoryImpl;

public class DesktopApplication {

	protected static final Logger logger = LoggerFactory.getLogger(DesktopApplication.class);

	public static void main(String[] argv) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Gemserk's Vampire Runner";
		config.width = 800;
		config.height = 480;
		// config.height = 240;
		config.fullscreen = false;
		config.useGL20 = false;
		config.useCPUSynch = true;
		config.forceExit = true;
		config.vSyncEnabled = true;

		Game game = new Game();

		// Scores scores = new ScoresHttpImpl("f3ba5a778d0996ffffae1088dd1773341c068552", "http://gemserkscores.appspot.com", new ScoreSerializerJSONImpl());
		// Profiles profiles = new ProfilesHttpImpl("http://gemserkscores.appspot.com");
		
		// if DEBUG -> 
		ProfilesMemoryImpl profiles = new ProfilesMemoryImpl();
		Scores scores = new ScoresMemoryImpl(profiles) {
			{
				submit(new Score("arielsan", 360, new HashSet<String>(), new HashMap<String, Object>()));
				submit(new Score("ruben01", 1, new HashSet<String>(), new HashMap<String, Object>()));
				submit(new Score("t4ils", 1525, new HashSet<String>(), new HashMap<String, Object>()));
			}
		};

		game.setScores(scores);
		game.setProfiles(profiles);

		new LwjglApplication(game, config);
	}

}
