package com.gemserk.games.vampirerunner.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.games.vampirerunner.components.GameComponents;
import com.gemserk.games.vampirerunner.components.PreviousStateSpatialComponent;

/**
 * Stores spatial state on the PreviousSpatialStateComponent (name could be changed) to be used when interpolating render stuff.
 */
public class PreviousStateSpatialSystem extends EntityProcessingSystem {

	@SuppressWarnings("unchecked")
	public PreviousStateSpatialSystem() {
		super(Components.spatialComponentClass, GameComponents.previousStateSpatialComponentClass);
	}

	@Override
	protected void process(Entity e) {
		SpatialComponent spatialComponent = Components.spatialComponent(e);
		PreviousStateSpatialComponent previousStateSpatialComponent = GameComponents.getPreviousStateSpatialComponent(e);
		previousStateSpatialComponent.getSpatial().set(spatialComponent.getSpatial());
	}

}
