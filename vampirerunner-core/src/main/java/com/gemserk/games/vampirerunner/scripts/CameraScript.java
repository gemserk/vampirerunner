package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.vampirerunner.Tags;

public class CameraScript extends ScriptJavaImpl {

	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;
	// private static final Class<PhysicsComponent> physicsComponentClass = PhysicsComponent.class;

	private final Libgdx2dCamera libgdx2dCamera;

	public CameraScript(Libgdx2dCamera libgdx2dCamera) {
		this.libgdx2dCamera = libgdx2dCamera;
	}

	@Override
	public void update(World world, Entity e) {

		Entity vampire = world.getTagManager().getEntity(Tags.Vampire);
		if (vampire == null)
			return;

		SpatialComponent spatialComponent = vampire.getComponent(spatialComponentClass);
		Spatial spatial = spatialComponent.getSpatial();

		libgdx2dCamera.move(spatial.getX(), spatial.getY());
		// libgdx2dCamera.zoom(camera.getZoom());
		// libgdx2dCamera.rotate(camera.getAngle());
	}

}
