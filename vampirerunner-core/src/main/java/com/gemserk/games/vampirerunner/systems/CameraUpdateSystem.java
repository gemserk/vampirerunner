package com.gemserk.games.vampirerunner.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.animation4j.interpolator.FloatInterpolator;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.games.vampirerunner.components.CameraComponent;
import com.gemserk.games.vampirerunner.components.GameComponents;
import com.gemserk.games.vampirerunner.components.PreviousStateCameraComponent;

public class CameraUpdateSystem extends EntityProcessingSystem {
	
	public CameraUpdateSystem() {
		super(GameComponents.cameraComponentClass, GameComponents.previousStateCameraComponentClass);
	}
	
	@Override
	protected void process(Entity e) {
		CameraComponent cameraComponent = GameComponents.getCameraComponent(e);

		Libgdx2dCamera libgdx2dCamera = cameraComponent.getLibgdx2dCamera();
		Camera camera = cameraComponent.getCamera();

		float newX = camera.getX();
		float newY = camera.getY();

		PreviousStateCameraComponent previousStateCameraComponent = GameComponents.getPreviousStateCameraComponent(e);

		if (previousStateCameraComponent != null) {
			float interpolationAlpha = GlobalTime.getAlpha();
			Camera previousCamera = previousStateCameraComponent.getCamera();
			newX = FloatInterpolator.interpolate(previousCamera.getX(), camera.getX(), interpolationAlpha);
			newY = FloatInterpolator.interpolate(previousCamera.getY(), camera.getY(), interpolationAlpha);
		}

		libgdx2dCamera.move(newX, newY);
	}

}
