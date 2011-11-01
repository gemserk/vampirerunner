package com.gemserk.games.vampirerunner.components;

import com.artemis.ComponentType;
import com.artemis.ComponentTypeManager;
import com.artemis.Entity;

public class GameComponents {
	
	public static final Class<RunningComponent> runningComponentClass = RunningComponent.class;
	public static final ComponentType runningComponentType = ComponentTypeManager.getTypeFor(runningComponentClass);

	public static RunningComponent getRunningComponent(Entity e) {
		return runningComponentClass.cast(e.getComponent(runningComponentType));
	}
	
}
