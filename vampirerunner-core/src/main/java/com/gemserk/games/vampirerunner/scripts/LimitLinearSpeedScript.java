package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.games.vampirerunner.components.Components.MaxSpeedComponent;

public class LimitLinearSpeedScript extends ScriptJavaImpl {

	private static final Class<PhysicsComponent> physicsComponentClass = PhysicsComponent.class;
	private static final Class<MaxSpeedComponent> maxSpeedComponentClass = MaxSpeedComponent.class;

	@Override
	public void update(World world, Entity e) {
		PhysicsComponent physicsComponent = e.getComponent(physicsComponentClass);
		Physics physics = physicsComponent.getPhysics();

		MaxSpeedComponent maxSpeedComponent = e.getComponent(maxSpeedComponentClass);

		Body body = physics.getBody();

		Vector2 linearVelocity = body.getLinearVelocity();

		float speed = linearVelocity.len();

		if (speed > maxSpeedComponent.maxSpeed) {
			linearVelocity.mul(maxSpeedComponent.maxSpeed / speed);
			body.setLinearVelocity(linearVelocity);
		}
	}

}
