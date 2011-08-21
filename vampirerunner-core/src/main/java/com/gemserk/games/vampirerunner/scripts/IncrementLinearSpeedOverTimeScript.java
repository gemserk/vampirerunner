package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.games.vampirerunner.components.Components.MaxSpeedComponent;

public class IncrementLinearSpeedOverTimeScript extends ScriptJavaImpl {

	private static final Class<MaxSpeedComponent> maxSpeedComponentClass = MaxSpeedComponent.class;

	private float aliveTime = 0f;
	private float startValue;

	@Override
	public void init(World world, Entity e) {
		MaxSpeedComponent maxSpeedComponent = e.getComponent(maxSpeedComponentClass);
		startValue = maxSpeedComponent.maxSpeed;
	}

	@Override
	public void update(World world, Entity e) {
		MaxSpeedComponent maxSpeedComponent = e.getComponent(maxSpeedComponentClass);
		aliveTime += GlobalTime.getDelta();
		maxSpeedComponent.maxSpeed = startValue + aliveTime * 0.5f;
	}

}
