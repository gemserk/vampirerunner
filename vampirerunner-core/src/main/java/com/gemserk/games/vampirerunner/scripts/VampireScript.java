package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;

public class VampireScript extends ScriptJavaImpl {

	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;

	@Override
	public void update(World world, Entity e) {

		// SpatialComponent spatialComponent = e.getComponent(spatialComponentClass);
		// Spatial spatial = spatialComponent.getSpatial();
		// spatial.setPosition(spatial.getX(), 1f);

		// get controller and determine if the vampire should jump or not...

	}

}
