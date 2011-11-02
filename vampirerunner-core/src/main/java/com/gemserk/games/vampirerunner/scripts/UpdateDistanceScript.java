package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.vampirerunner.components.Components.DistanceComponent;
import com.gemserk.games.vampirerunner.components.GameComponents;

public class UpdateDistanceScript extends ScriptJavaImpl {

	@Override
	public void init(World world, Entity e) {
		SpatialComponent playerSpatialComponent = Components.getSpatialComponent(e);
		Spatial spatial = playerSpatialComponent.getSpatial();
		DistanceComponent distanceComponent = GameComponents.getDistanceComponent(e);
		distanceComponent.lastPosition = spatial.getX(); 
	}

	@Override
	public void update(World world, Entity e) {
		SpatialComponent playerSpatialComponent = Components.getSpatialComponent(e);
		Spatial spatial = playerSpatialComponent.getSpatial();
		DistanceComponent distanceComponent = GameComponents.getDistanceComponent(e);
		
		float difference = spatial.getX() - distanceComponent.lastPosition;
		
		distanceComponent.distance += difference;
		distanceComponent.lastPosition = spatial.getX();
	}

}
