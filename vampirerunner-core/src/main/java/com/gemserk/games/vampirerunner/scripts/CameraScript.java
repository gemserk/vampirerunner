package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.Spatial;

public class CameraScript extends ScriptJavaImpl {

	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;

	private final Libgdx2dCamera libgdx2dCamera;
	private final String targetId;

	public CameraScript(String targetId, Libgdx2dCamera libgdx2dCamera) {
		this.targetId = targetId;
		this.libgdx2dCamera = libgdx2dCamera;
	}

	@Override
	public void update(World world, Entity e) {
		Entity target = world.getTagManager().getEntity(targetId);

		if (target == null)
			return;

		SpatialComponent spatialComponent = target.getComponent(spatialComponentClass);
		Spatial spatial = spatialComponent.getSpatial();

		libgdx2dCamera.move(spatial.getX(), spatial.getY());
	}

}
