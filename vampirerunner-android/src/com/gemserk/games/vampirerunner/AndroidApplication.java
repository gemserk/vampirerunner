package com.gemserk.games.vampirerunner;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import com.dmurph.tracking.VisitorData;
import com.gemserk.analytics.Analytics;
import com.gemserk.analytics.googleanalytics.android.AnalyticsStoredConfig;
import com.gemserk.analytics.googleanalytics.android.BasicConfig;
import com.gemserk.commons.adwhirl.AdWhirlAndroidHandler;
import com.gemserk.commons.adwhirl.CustomAdViewHandler;
import com.gemserk.commons.adwhirl.PausableAdWhirlLayout;
import com.gemserk.commons.utils.BrowserUtilsAndroidImpl;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.datastore.profiles.ProfilesHttpImpl;
import com.gemserk.scores.ScoreSerializerJSONImpl;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresHttpImpl;

public class AndroidApplication extends com.badlogic.gdx.backends.android.AndroidApplication implements AdWhirlInterface {

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
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;
		
		AdWhirlManager.setConfigExpireTimeout(1000 * 15);
		AdWhirlTargeting.setAge(23);
		AdWhirlTargeting.setGender(AdWhirlTargeting.Gender.MALE);
		AdWhirlTargeting.setKeywords("online games gaming");
		AdWhirlTargeting.setPostalCode("94123");
		AdWhirlTargeting.setTestMode(false);

		String adWhirlSDKKey = "183a2ece28814417ac2c44b56e1281fa";
		PausableAdWhirlLayout adView = new PausableAdWhirlLayout(this, adWhirlSDKKey);
		
		Handler handler = new AdWhirlAndroidHandler(adView);
		CustomAdViewHandler adWhirlViewHandler = new CustomAdViewHandler(handler);

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
		game.setAdWhirlViewHandler(adWhirlViewHandler);

		View gameView = initializeForView(game, config);
		
		int diWidth = 320;
		int diHeight = 52;
		
		float density = getResources().getDisplayMetrics().density;

		adView.setAdWhirlInterface(this);
		adView.setMaxWidth((int) (diWidth * density));
		adView.setMaxHeight((int) (diHeight * density));
		
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		layout.addView(gameView);
		layout.addView(adView, adParams);

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

	@Override
	public void adWhirlGeneric() {
		
	}
}