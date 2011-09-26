package com.gemserk.games.vampirerunner.components;

import com.artemis.Entity;

public class GameComponents {
	
	public static final Class<CameraComponent> cameraComponentClass = CameraComponent.class;
	public static final Class<PreviousStateCameraComponent> previousStateCameraComponentClass = PreviousStateCameraComponent.class;
	
	public static CameraComponent getCameraComponent(Entity e) {
		return e.getComponent(cameraComponentClass);
	}

	public static PreviousStateCameraComponent getPreviousStateCameraComponent(Entity e) {
		return e.getComponent(previousStateCameraComponentClass);
	}

}
