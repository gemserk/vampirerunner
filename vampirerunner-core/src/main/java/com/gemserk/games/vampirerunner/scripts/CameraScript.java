package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.vampirerunner.components.CameraComponent;
import com.gemserk.games.vampirerunner.components.GameComponents;
import com.gemserk.games.vampirerunner.components.PreviousStateCameraComponent;

public class CameraScript extends ScriptJavaImpl {

	private final String targetId;

	public CameraScript(String targetId) {
		this.targetId = targetId;
	}

	@Override
	public void update(World world, Entity e) {
		Entity target = world.getTagManager().getEntity(targetId);

		CameraComponent cameraComponent = GameComponents.getCameraComponent(e);
		Camera camera = cameraComponent.getCamera();

		// store previous camera state, to be used for interpolation
		PreviousStateCameraComponent previousStateCameraComponent = GameComponents.getPreviousStateCameraComponent(e);

		if (previousStateCameraComponent != null) {
			Camera previousCamera = previousStateCameraComponent.getCamera();
			previousCamera.setPosition(camera.getX(), camera.getY());
			previousCamera.setAngle(camera.getAngle());
			previousCamera.setZoom(camera.getZoom());
		}
		
		if (target == null)
			return;

		SpatialComponent spatialComponent = Components.spatialComponent(target);
		Spatial spatial = spatialComponent.getSpatial();

		camera.setPosition(spatial.getX(), spatial.getY());
	}

}
