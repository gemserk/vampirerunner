package com.gemserk.games.vampirerunner.resources;

import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;

/**
 * Declares all resources needed for the game.
 */
public class GameResources extends LibgdxResourceBuilder {

	public static void load(ResourceManager<String> resourceManager) {
		new GameResources(resourceManager);
	}

	private GameResources(ResourceManager<String> resourceManager) {
		super(resourceManager);

		texture("GemserkLogoTexture", "data/images/logos/logo-gemserk-512x128.png");
		texture("GemserkLogoTextureBlur", "data/images/logos/logo-gemserk-512x128-blur.png");
		texture("LwjglLogoTexture", "data/images/logos/logo-lwjgl-512x256-inverted.png");
		texture("LibgdxLogoTexture", "data/images/logos/logo-libgdx-clockwork-512x256.png");

		sprite("GemserkLogo", "GemserkLogoTexture");
		sprite("GemserkLogoBlur", "GemserkLogoTextureBlur");
		sprite("LwjglLogo", "LwjglLogoTexture", 0, 0, 512, 185);
		sprite("LibgdxLogo", "LibgdxLogoTexture", 0, 25, 512, 256 - 50);

		textureAtlas("TextureAtlas", "data/images/packs/pack");
		spriteAtlas("BackgroundSprite", "TextureAtlas", "background");
		spriteAtlas("VampireSprite", "TextureAtlas", "vampire");
		spriteAtlas("FloorTile01Sprite", "TextureAtlas", "floor-tile-01");
		spriteAtlas("WhiteRectangleSprite", "TextureAtlas", "white-rectangle");

		texture("VampireSpriteSheet", internal("data/images/spritesheets/vampire-spritesheet.png"), false);
		// animation speed should depend on the vampire speed.
		animation("VampireRunningAnimation", "VampireSpriteSheet", 0, 32, 32, 32, 6, true, 1000);
		animation("VampireFlyingAnimation", "VampireSpriteSheet", 32 * 2, 0, 32, 32, 2, true, 2000);
		animation("VampireBloodAnimation", "VampireSpriteSheet", 0, 32 * 3, 32, 32, 7, false, 100);

		font("DistanceFont", "data/fonts/purisa-18.png", "data/fonts/purisa-18.fnt", false);

		sprite("VampireRightArm", "VampireSpriteSheet", 0, 32 * 2, 32, 32);
		sprite("VampireLeftArm", "VampireSpriteSheet", 32 * 1, 32 * 2, 32, 32);
		sprite("VampireHead", "VampireSpriteSheet", 32 * 2, 32 * 2, 32, 32);
		sprite("VampireLeftLeg", "VampireSpriteSheet", 32 * 3, 32 * 2, 32, 32);
		sprite("VampireRightLeg", "VampireSpriteSheet", 32 * 4, 32 * 2, 32, 32);
		sprite("VampireTorso", "VampireSpriteSheet", 32 * 5, 32 * 2, 32, 32);
		
		texture("BackgroundTile01", "data/images/background-tile-01.png", false);
		texture("BackgroundTile02", "data/images/background-tile-02.png", false);
		sprite("BackgroundTile01Sprite", "BackgroundTile01");
		sprite("BackgroundTile02Sprite", "BackgroundTile02");

	}
}
