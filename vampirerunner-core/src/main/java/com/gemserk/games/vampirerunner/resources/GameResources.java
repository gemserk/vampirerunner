package com.gemserk.games.vampirerunner.resources;

import com.badlogic.gdx.Gdx;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;

/**
 * Declares all resources needed for the game.
 */
public class GameResources extends LibgdxResourceBuilder {
	
	public static class Emitters {
		
		public static final String BatmanEmitter = "BatmanEmitter";
		
	}

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
		spriteAtlas("WhiteRectangleSprite", "TextureAtlas", "white-rectangle");
		spriteAtlas("FloorTile01Sprite", "TextureAtlas", "floor-bigtile-01");

		spriteAtlas("Cloud01Sprite", "TextureAtlas", "cloud01");
		spriteAtlas("Cloud02Sprite", "TextureAtlas", "cloud02");
		spriteAtlas("Cloud03Sprite", "TextureAtlas", "cloud03");
		
		spriteAtlas("BloodSprite", "TextureAtlas", "blood-01");

		texture("VampireSpriteSheet", internal("data/images/spritesheets/vampire-spritesheet.png"), false);
		// animation speed should depend on the vampire speed.
		animation("VampireRunningAnimation", "VampireSpriteSheet", 0, 32, 32, 32, 6, true, 1000);
		animation("VampireFlyingAnimation", "VampireSpriteSheet", 32 * 2, 0, 32, 32, 2, true, 2000);
		animation("VampireBloodAnimation", "VampireSpriteSheet", 0, 32 * 3, 32, 32, 7, false, 100);
		animation("VampireIdleAnimation", "VampireSpriteSheet", 0, 0, 32, 32, 2, true, 1500, 250);

		if (Gdx.graphics.getHeight() >= 480f) {
			font("DistanceFont", "data/fonts/purisa-20-bold-outlined.png", "data/fonts/purisa-20-bold-outlined.fnt", false);
			font("ScoresFont", "data/fonts/purisa-20-bold-outlined.png", "data/fonts/purisa-20-bold-outlined.fnt", false);
			font("ScoresFont", "data/fonts/purisa-20-bold-outlined.png", "data/fonts/purisa-20-bold-outlined.fnt", false);
			font("TitleFont", "data/fonts/ogilvie-40-bold-outlined.png", "data/fonts/ogilvie-40-bold-outlined.fnt", false);
			font("InstructionsFont", "data/fonts/purisa-20-gradient.png", "data/fonts/purisa-20-gradient.fnt", false);
			font("ButtonFont", "data/fonts/purisa-20-bold-outlined.png", "data/fonts/purisa-20-bold-outlined.fnt", false);
		} else {
			font("DistanceFont", "data/fonts/purisa-12-bold.png", "data/fonts/purisa-12-bold.fnt", false);
			font("ScoresFont", "data/fonts/purisa-12-bold.png", "data/fonts/purisa-12-bold.fnt", false);
			font("TitleFont", "data/fonts/ogilvie-20-bold-outlined.png", "data/fonts/ogilvie-20-bold-outlined.fnt", false);
			font("InstructionsFont", "data/fonts/purisa-12-bold.png", "data/fonts/purisa-12-bold.fnt", false);
			font("ButtonFont", "data/fonts/purisa-12-bold.png", "data/fonts/purisa-12-bold.fnt", false);
		}

		sprite("VampireRightArm", "VampireSpriteSheet", 0, 32 * 2, 32, 32);
		sprite("VampireLeftArm", "VampireSpriteSheet", 32 * 1, 32 * 2, 32, 32);
		sprite("VampireHead", "VampireSpriteSheet", 32 * 2, 32 * 2, 32, 32);
		sprite("VampireLeftLeg", "VampireSpriteSheet", 32 * 3, 32 * 2, 32, 32);
		sprite("VampireRightLeg", "VampireSpriteSheet", 32 * 4, 32 * 2, 32, 32);
		sprite("VampireTorso", "VampireSpriteSheet", 32 * 5, 32 * 2, 32, 32);

		texture("BackgroundTile01", "data/images/background-tile-01.png", false);
		texture("BackgroundTile02", "data/images/background-tile-02.png", false);
		texture("BackgroundTile03", "data/images/background-tile-03.png", false);
		texture("BackgroundTile04", "data/images/background-tile-04.png", false);

		sprite("BackgroundTile01Sprite", "BackgroundTile01");
		sprite("BackgroundTile02Sprite", "BackgroundTile02");
		sprite("BackgroundTile03Sprite", "BackgroundTile03");
		sprite("BackgroundTile04Sprite", "BackgroundTile04");

		// music("GameMusic", "data/audio/music.ogg");
		sound("VampireDeathSound", "data/audio/vampiredeath.ogg");
		
		textureAtlas("WallTextureAtlas", "data/images/wall/pack");
		
		spriteAtlas("WallTileASprite", "WallTextureAtlas", "wall-tile-01");
		spriteAtlas("WallTileBSprite", "WallTextureAtlas", "wall-tile-02");
		spriteAtlas("WallTileCSprite", "WallTextureAtlas", "wall-tile-03");
		spriteAtlas("WallTileDSprite", "WallTextureAtlas", "wall-tile-04");
		
		particleEffect("BatmanEffect", "data/particles/BatmanEffect", "data/particles");
		particleEmitter(Emitters.BatmanEmitter, "BatmanEffect", "BatmanEmitter", 1f / 64f);

	}
}
