package com.gemserk.games.vampirerunner.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.gui.ControlImpl;

public class EnergyBarControl extends ControlImpl {

	private float widthPercentage;
	private float height;

	private Sprite barBackgroundSprite;
	private Sprite barForegroundSprite;

	private float percentage;

	public EnergyBarControl(String id, Sprite whiteRectangleSprite) {
		setId(id);
		this.barBackgroundSprite = whiteRectangleSprite;
		this.barForegroundSprite = new Sprite(whiteRectangleSprite);
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

		barBackgroundSprite.setPosition(getX(), getY());
		barBackgroundSprite.setColor(1f, 0f, 0f, 1f);
		barBackgroundSprite.setSize(width, height);

		barForegroundSprite.setPosition(getX(), getY());
		barForegroundSprite.setColor(0f, 0f, 1f, 1f);

		barForegroundSprite.setSize(width * percentage, height);
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {

		barBackgroundSprite.draw(spriteBatch);
		barForegroundSprite.draw(spriteBatch);

	}
}