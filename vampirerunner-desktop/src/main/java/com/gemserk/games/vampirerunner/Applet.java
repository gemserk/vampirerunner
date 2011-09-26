package com.gemserk.games.vampirerunner;

import java.awt.BorderLayout;
import java.awt.Canvas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import com.gemserk.analytics.Analytics;
import com.gemserk.analytics.googleanalytics.DesktopAnalyticsAutoConfigurator;
import com.gemserk.commons.utils.BrowserUtilsDesktopImpl;
import com.gemserk.commons.utils.gdx.LwjglLibgdxLibraryUtils;
import com.gemserk.datastore.profiles.Profiles;
import com.gemserk.datastore.profiles.ProfilesHttpImpl;
import com.gemserk.scores.ScoreSerializerJSONImpl;
import com.gemserk.scores.Scores;
import com.gemserk.scores.ScoresHttpImpl;

public class Applet extends java.applet.Applet {

	private static final long serialVersionUID = 6396112708370503447L;

	private Canvas canvas;

	private LwjglApplication application;

	public void start() {

	}

	public void stop() {

	}

	public void destroy() {
		remove(canvas);
		super.destroy();
	}

	public void init() {

		AnalyticsConfigData analyticsConfig = new AnalyticsConfigData("UA-23542248-5");
		DesktopAnalyticsAutoConfigurator.populateFromSystem(analyticsConfig);
		Analytics.traker = new JGoogleAnalyticsTracker(analyticsConfig, GoogleAnalyticsVersion.V_4_7_2);

		GdxNativesLoader.disableNativesLoading = true;
		// System.loadLibrary("gdx");
		LwjglLibgdxLibraryUtils.loadLibgdxLibrary();

		try {
			setLayout(new BorderLayout());
			// ApplicationListener game = (ApplicationListener) Class.forName(getParameter("game")).newInstance();

			canvas = new Canvas() {
				public final void addNotify() {
					super.addNotify();

					Game game = new Game() {
						@Override
						public void create() {
							Gdx.graphics.setVSync(true);
							super.create();
						};
					};

					Scores scores = new ScoresHttpImpl("f3ba5a778d0996ffffae1088dd1773341c068552", "http://gemserkscores.appspot.com", new ScoreSerializerJSONImpl());
					Profiles profiles = new ProfilesHttpImpl("http://gemserkscores.appspot.com");

					game.setScores(scores);
					game.setProfiles(profiles);
					game.setBrowserUtils(new BrowserUtilsDesktopImpl());

					application = new LwjglApplication(game, false, this) {
						public com.badlogic.gdx.Application.ApplicationType getType() {
							return ApplicationType.Applet;
						};
					};
				}

				public final void removeNotify() {
					application.stop();
					super.removeNotify();
				}
			};
			canvas.setSize(getWidth(), getHeight());
			add(canvas);
			canvas.setFocusable(true);
			canvas.requestFocus();
			canvas.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display", e);
		}
	}
}