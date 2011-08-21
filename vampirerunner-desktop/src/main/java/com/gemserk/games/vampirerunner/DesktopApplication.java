package com.gemserk.games.vampirerunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopApplication {

	protected static final Logger logger = LoggerFactory.getLogger(DesktopApplication.class);

	public static void main(String[] argv) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Ludum Dare 21 - Vampire Runner - arielsan - Gemserk";
		config.width = 640;
		config.height = 240;
		config.fullscreen = false;
		config.useGL20 = false;
		config.useCPUSynch = true;
		config.forceExit = true;
		config.vSyncEnabled = true;

		new LwjglApplication(new Game(), config);
	}

}
