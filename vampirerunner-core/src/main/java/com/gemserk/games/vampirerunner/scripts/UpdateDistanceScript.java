package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.vampirerunner.components.Components.DistanceComponent;

public class UpdateDistanceScript extends ScriptJavaImpl {

	float lastPlayerPosition = 0f;

	@Override
	public void init(World world, Entity e) {
		SpatialComponent playerSpatialComponent = e.getComponent(SpatialComponent.class);
		Spatial spatial = playerSpatialComponent.getSpatial();
		lastPlayerPosition = spatial.getX();
	}

	@Override
	public void update(World world, Entity e) {
		SpatialComponent playerSpatialComponent = e.getComponent(SpatialComponent.class);
		Spatial spatial = playerSpatialComponent.getSpatial();
		DistanceComponent distanceComponent = e.getComponent(DistanceComponent.class);
		distanceComponent.distance += spatial.getX() - lastPlayerPosition;
		lastPlayerPosition = spatial.getX();
	}

}
