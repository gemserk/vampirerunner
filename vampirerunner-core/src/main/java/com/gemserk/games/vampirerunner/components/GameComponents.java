package com.gemserk.games.vampirerunner.components;

import com.artemis.Entity;

public class GameComponents {
	
	public static final Class<PreviousSpatialStateComponent> spatialStateComponentClass = PreviousSpatialStateComponent.class;
	public static final Class<CameraComponent> cameraComponentClass = CameraComponent.class;
	
	public static PreviousSpatialStateComponent previousSpatialStateComponent(Entity e) {
		return e.getComponent(spatialStateComponentClass);
	}
	
	public static CameraComponent getCameraComponent(Entity e) {
		return e.getComponent(cameraComponentClass);
	}

}
