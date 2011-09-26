package com.gemserk.games.vampirerunner;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import com.dmurph.tracking.VisitorData;
import com.gemserk.analytics.Analytics;
import com.gemserk.analytics.googleanalytics.android.AnalyticsStoredConfig;
import com.gemserk.analytics.googleanalytics.android.BasicConfig;
import com.gemserk.commons.utils.BrowserUtilsAndroidImpl;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.datastore.profiles.ProfilesHttpImpl;
import com.gemserk.scores.ScoreSerializerJSONImpl;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresHttpImpl;

public class AndroidApplication extends com.badlogic.gdx.backends.android.AndroidApplication {

	private AnalyticsStoredConfig storedConfig;
	private VisitorData visitorData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useGL20 = false;
		config.useAccelerometer = true;
		config.useCompass = true;
		config.useWakelock = true;

		Game game = new Game() {
			@Override
			public void pause() {
				super.pause();
				saveAnalyticsData();
			}
		};

		Scores scores = new ScoresHttpImpl("f3ba5a778d0996ffffae1088dd1773341c068552", "http://gemserkscores.appspot.com", new ScoreSerializerJSONImpl());
		Profiles profiles = new ProfilesHttpImpl("http://gemserkscores.appspot.com");

		game.setScores(scores);
		game.setProfiles(profiles);
		
		game.setBrowserUtils(new BrowserUtilsAndroidImpl(this));

		View gameView = initializeForView(game, config);

		layout.addView(gameView);

		setContentView(layout);

		storedConfig = new AnalyticsStoredConfig(getApplicationContext());
		visitorData = storedConfig.loadVisitor();

		AnalyticsConfigData analyticsconfig = new AnalyticsConfigData("UA-23542248-5", visitorData);
		BasicConfig.configure(analyticsconfig, getApplicationContext());

		Analytics.traker = new JGoogleAnalyticsTracker(analyticsconfig, GoogleAnalyticsVersion.V_4_7_2);
	}

	protected void saveAnalyticsData() {
		Analytics.traker.completeBackgroundTasks(500);
		storedConfig.saveVisitor(visitorData);
	}
}