package com.gemserk.games.vampirerunner;

import org.lwjgl.opengl.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.input.RemoteInput;

public class DesktopApplication {

	protected static final Logger logger = LoggerFactory.getLogger(DesktopApplication.class);

	public static void main(String[] argv) {

		System.out.println(System.getProperty("java.version"));

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
		// config.width = 320;
		// config.height = 240;
		// config.width = 1024;
		// config.height = 768;
		config.fullscreen = false;
		config.title = "Ludum Dare 21 - Vampire Runner - arielsan - Gemserk";
		config.useGL20 = false;
		config.useCPUSynch = true;
		config.forceExit = true;

		new LwjglApplication(new Game() {
			@Override
			public void create() {
				// Gdx.graphics.setVSync(true);
				Display.setVSyncEnabled(false);
				String remoteInput = System.getProperty("remoteInput");
				if (remoteInput != null)
					Gdx.input = new RemoteInput(8190);
				super.create();
			}
		}, config);
	}

}
