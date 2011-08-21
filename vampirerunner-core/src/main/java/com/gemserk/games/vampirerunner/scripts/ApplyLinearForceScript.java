package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Physics;

public class ApplyLinearForceScript extends ScriptJavaImpl {

	private static final Class<PhysicsComponent> physicsComponentClass = PhysicsComponent.class;
	private final Vector2 force;

	public ApplyLinearForceScript(Vector2 force) {
		this.force = force;
	}

	@Override
	public void update(World world, Entity e) {
		PhysicsComponent physicsComponent = e.getComponent(physicsComponentClass);
		Physics physics = physicsComponent.getPhysics();
		Body body = physics.getBody();
		body.applyForce(force.tmp().mul(GlobalTime.getDelta()), body.getPosition());
	}

}
