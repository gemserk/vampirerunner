package com.gemserk.games.vampirerunner;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import com.gemserk.analytics.Analytics;
import com.gemserk.analytics.googleanalytics.DesktopAnalyticsAutoConfigurator;
import com.gemserk.commons.utils.BrowserUtilsDesktopImpl;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.datastore.profiles.ProfilesFileImpl;
import com.gemserk.datastore.profiles.ProfilesHttpImpl;
import com.gemserk.scores.ScoreSerializerJSONImpl;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresFileImpl;
import com.gemserk.scores.ScoresHttpImpl;

public class DesktopApplication {

	protected static final Logger logger = LoggerFactory.getLogger(DesktopApplication.class);

	private static class Arguments {

		int width = 800;
		int height = 480;

		public void parse(String[] argv) {
			if (argv.length == 0)
				return;

			String displayString = argv[0];
			String[] displayValues = displayString.split("x");

			if (displayValues.length < 2)
				return;

			try {
				width = Integer.parseInt(displayValues[0]);
				height = Integer.parseInt(displayValues[1]);
			} catch (NumberFormatException e) {
				System.out.println("error when parsing resolution from arguments: " + displayString);
			}

		}

	}

	public static void main(String[] argv) {

		AnalyticsConfigData analyticsConfig = new AnalyticsConfigData("UA-23542248-5");
		DesktopAnalyticsAutoConfigurator.populateFromSystem(analyticsConfig);
		Analytics.traker = new JGoogleAnalyticsTracker(analyticsConfig, GoogleAnalyticsVersion.V_4_7_2);

		Arguments arguments = new Arguments();
		arguments.parse(argv);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Gemserk's Vampire Runner";
		config.width = arguments.width;
		config.height = arguments.height;
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
			Analytics.traker.setEnabled(false);
			String tmpFolder = System.getProperty("java.io.tmpdir");
			profiles = new ProfilesFileImpl(new File(tmpFolder, "gemserk/vampirerunner/profiles.json"));
			scores = new ScoresFileImpl(new File(tmpFolder, "gemserk/vampirerunner/scores.json"), profiles);
		}

		game.setScores(scores);
		game.setProfiles(profiles);
		
		game.setBrowserUtils(new BrowserUtilsDesktopImpl());

		new LwjglApplication(game, config);
	}

}
