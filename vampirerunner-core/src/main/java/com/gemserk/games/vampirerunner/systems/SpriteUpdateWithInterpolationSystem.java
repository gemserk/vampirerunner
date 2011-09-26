package com.gemserk.games.vampirerunner.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.interpolator.FloatInterpolator;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.vampirerunner.components.GameComponents;
import com.gemserk.games.vampirerunner.components.PreviousStateSpatialComponent;

/**
 * Updates Sprites from SpriteComponent to the location of the Spatial from the SpatialComponent, 
 * if the entity has a PreviousSpatialStateComponent, then it performs an interpolation between both 
 * spatial information using the GlobalTime.getAlpha().
 */
public class SpriteUpdateWithInterpolationSystem extends EntityProcessingSystem {

	@SuppressWarnings("unchecked")
	public SpriteUpdateWithInterpolationSystem() {
		super(SpatialComponent.class, SpriteComponent.class);
	}

	@Override
	protected void process(Entity e) {
		SpatialComponent spatialComponent = Components.spatialComponent(e);
		SpriteComponent spriteComponent = Components.spriteComponent(e);

		PreviousStateSpatialComponent previousStateSpatialComponent = GameComponents.getPreviousStateSpatialComponent(e);

		Spatial spatial = spatialComponent.getSpatial();

		float newX = spatial.getX();
		float newY = spatial.getY();

		float angle = spatial.getAngle();

		if (previousStateSpatialComponent != null) {
			float interpolationAlpha = GlobalTime.getAlpha();
			Spatial previousSpatial = previousStateSpatialComponent.getSpatial();
			newX = FloatInterpolator.interpolate(previousSpatial.getX(), spatial.getX(), interpolationAlpha);
			newY = FloatInterpolator.interpolate(previousSpatial.getY(), spatial.getY(), interpolationAlpha);
			angle = FloatInterpolator.interpolate(previousSpatial.getAngle(), spatial.getAngle(), interpolationAlpha);
		}

		Sprite sprite = spriteComponent.getSprite();
		Vector2 center = spriteComponent.getCenter();

		if (spriteComponent.isUpdateRotation())
			sprite.setRotation(angle);

		sprite.setOrigin(spatial.getWidth() * center.x, spatial.getHeight() * center.y);

		sprite.setSize(spatial.getWidth(), spatial.getHeight());
		sprite.setPosition(newX - sprite.getOriginX(), newY - sprite.getOriginY());
	}

}
