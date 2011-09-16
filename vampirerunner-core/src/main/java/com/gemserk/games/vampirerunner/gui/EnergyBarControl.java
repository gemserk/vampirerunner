package com.gemserk.games.vampirerunner.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.gui.Control;

public class EnergyBarControl implements Control {

	private final String id;
	private float x, y;

	private float widthPercentage;
	private float height;

	private Sprite barBackgroundSprite;
	private Sprite barForegroundSprite;

	private float percentage;

	public EnergyBarControl(String id, Sprite whiteRectangleSprite) {
		this.id = id;
		this.barBackgroundSprite = whiteRectangleSprite;
		this.barForegroundSprite = new Sprite(whiteRectangleSprite);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setWidthPercentage(float widthPercentage) {
		this.widthPercentage = widthPercentage;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}

	public void setPercentage(com.gemserk.componentsengine.utils.Container energy) {
		this.percentage = energy.getPercentage();
	}

	@Override
	public void update() {
		int containerWidth = Gdx.graphics.getWidth();

		float width = containerWidth * this.widthPercentage;

		barBackgroundSprite.setPosition(x, y);
		barBackgroundSprite.setColor(1f, 0f, 0f, 1f);
		barBackgroundSprite.setSize(width, height);

		barForegroundSprite.setPosition(x, y);
		barForegroundSprite.setColor(0f, 0f, 1f, 1f);

		barForegroundSprite.setSize(width * percentage, height);
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {

		barBackgroundSprite.draw(spriteBatch);
		barForegroundSprite.draw(spriteBatch);

	}

}