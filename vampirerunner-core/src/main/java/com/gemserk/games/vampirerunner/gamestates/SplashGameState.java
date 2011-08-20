package com.gemserk.games.vampirerunner.gamestates;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.TimeTransition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.games.vampirerunner.Game;
import com.gemserk.games.vampirerunner.resources.GameResources;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class SplashGameState extends GameStateImpl {

	private final Game game;

	private SpriteBatch spriteBatch;

	private ResourceManager<String> resourceManager;

	private Sprite gemserkLogo;
	private Sprite lwjglLogo;
	private Sprite libgdxLogo;
	private Sprite gemserkLogoBlur;

	private Color blurColor = new Color();

	private TimeTransition timeTransition;

	public SplashGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int centerX = width / 2;
		int centerY = height / 2;

		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManagerImpl<String>();

		GameResources.load(resourceManager);

		gemserkLogo = resourceManager.getResourceValue("GemserkLogo");
		gemserkLogoBlur = resourceManager.getResourceValue("GemserkLogoBlur");
		lwjglLogo = resourceManager.getResourceValue("LwjglLogo");
		libgdxLogo = resourceManager.getResourceValue("LibgdxLogo");

		SpriteUtils.resize(gemserkLogo, width * 0.8f);
		SpriteUtils.resize(gemserkLogoBlur, width * 0.8f);
		SpriteUtils.resize(lwjglLogo, width * 0.2f);
		SpriteUtils.resize(libgdxLogo, width * 0.2f);

		SpriteUtils.centerOn(gemserkLogo, centerX, centerY);
		SpriteUtils.centerOn(gemserkLogoBlur, centerX, centerY);
		SpriteUtils.centerOn(lwjglLogo, width * 0.85f, lwjglLogo.getHeight() * 0.5f);
		SpriteUtils.centerOn(libgdxLogo, width * 0.15f, libgdxLogo.getHeight() * 0.5f);

		Synchronizers.transition(blurColor, Transitions.transitionBuilder(new Color(1f, 0f, 0f, 0f)).end(new Color(1f, 0f, 0f, 1f)).time(1f));

		timeTransition = new TimeTransition();
		timeTransition.start(2f);
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();

		gemserkLogoBlur.setColor(blurColor);
		gemserkLogoBlur.draw(spriteBatch);

		gemserkLogo.draw(spriteBatch);
		if (Gdx.app.getType() != ApplicationType.Android)
			lwjglLogo.draw(spriteBatch);
		libgdxLogo.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());

		if (Gdx.input.justTouched())
			timeTransition.update(10000f);

		timeTransition.update(getDelta());

		if (!timeTransition.isFinished())
			return;

		game.setScreen(game.getPlayGameScreen(), true);
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void dispose() {
		// resourceManager.unloadAll();
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
