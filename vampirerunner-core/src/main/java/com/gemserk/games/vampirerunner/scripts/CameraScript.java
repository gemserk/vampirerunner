package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.CameraComponent;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PreviousStateCameraComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.games.Spatial;

public class CameraScript extends ScriptJavaImpl {

	private final String targetId;

	public CameraScript(String targetId) {
		this.targetId = targetId;
	}

	@Override
	public void update(World world, Entity e) {
		Entity target = world.getTagManager().getEntity(targetId);

		CameraComponent cameraComponent = Components.getCameraComponent(e);
		Camera camera = cameraComponent.getCamera();

		// store previous camera state, to be used for interpolation
		PreviousStateCameraComponent previousStateCameraComponent = Components.getPreviousStateCameraComponent(e);

		if (previousStateCameraComponent != null) {
			Camera previousCamera = previousStateCameraComponent.getCamera();
			previousCamera.setPosition(camera.getX(), camera.getY());
			previousCamera.setAngle(camera.getAngle());
			previousCamera.setZoom(camera.getZoom());
		}

		if (target == null)
			return;

		SpatialComponent spatialComponent = Components.getSpatialComponent(target);
		Spatial spatial = spatialComponent.getSpatial();

		camera.setPosition(spatial.getX(), spatial.getY());
	}

}
