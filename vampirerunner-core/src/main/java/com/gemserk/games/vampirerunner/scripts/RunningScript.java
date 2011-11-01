package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.games.vampirerunner.components.GameComponents;
import com.gemserk.games.vampirerunner.components.RunningComponent;

public class RunningScript extends ScriptJavaImpl {

	@Override
	public void init(World world, Entity e) {
		RunningComponent runningComponent = GameComponents.getRunningComponent(e);
		PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);

		Body body = physicsComponent.getBody();

		body.setLinearVelocity(runningComponent.initialSpeed, 0f);
	}

	@Override
	public void update(World world, Entity e) {
		RunningComponent runningComponent = GameComponents.getRunningComponent(e);
		PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);

		Body body = physicsComponent.getBody();

		Vector2 linearVelocity = body.getLinearVelocity();

		float speed = linearVelocity.len();
		if (speed > runningComponent.maxSpeed)
			return;

		body.applyForceToCenter(runningComponent.force);
	}

}
