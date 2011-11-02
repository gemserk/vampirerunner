package com.gemserk.games.vampirerunner.components;

import com.artemis.ComponentType;
import com.artemis.ComponentTypeManager;
import com.artemis.Entity;
import com.gemserk.games.vampirerunner.components.Components.DistanceComponent;
import com.gemserk.games.vampirerunner.components.Components.SuperSkillComponent;

public class GameComponents {
	
	public static final Class<RunningComponent> runningComponentClass = RunningComponent.class;
	public static final ComponentType runningComponentType = ComponentTypeManager.getTypeFor(runningComponentClass);

	public static final Class<SuperSkillComponent> superSkillComponentClass = SuperSkillComponent.class;
	public static final ComponentType superSkillComponenttType = ComponentTypeManager.getTypeFor(superSkillComponentClass);

	public static final Class<DistanceComponent> distanceComponentClass = DistanceComponent.class;
	public static final ComponentType distanceComponentType = ComponentTypeManager.getTypeFor(distanceComponentClass);

	public static RunningComponent getRunningComponent(Entity e) {
		return runningComponentClass.cast(e.getComponent(runningComponentType));
	}

	public static SuperSkillComponent getSuperSkillComponent(Entity e) {
		return superSkillComponentClass.cast(e.getComponent(superSkillComponenttType));
	}
	
	public static DistanceComponent getDistanceComponent(Entity e) {
		return distanceComponentClass.cast(e.getComponent(distanceComponentType));
	}
	
}
