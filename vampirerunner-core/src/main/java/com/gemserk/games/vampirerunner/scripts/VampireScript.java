package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Physics;

public class VampireScript extends ScriptJavaImpl {

	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;
	private static final Class<PhysicsComponent> physicsComponentClass = PhysicsComponent.class;

	private float maxLinearSpeed = 5f;
	private float aliveTime = 0f;

	@Override
	public void update(World world, Entity e) {

		// get controller and determine if the vampire should jump or not...

		PhysicsComponent physicsComponent = e.getComponent(physicsComponentClass);
		Physics physics = physicsComponent.getPhysics();

		Body body = physics.getBody();

		body.applyForce(new Vector2(50f, 0f).mul(GlobalTime.getDelta()), body.getPosition());

		Vector2 linearVelocity = body.getLinearVelocity();

		float speed = linearVelocity.len();

		if (speed > maxLinearSpeed) {
			linearVelocity.mul(maxLinearSpeed / speed);
			body.setLinearVelocity(linearVelocity);
		}

		AnimationComponent animationComponent = e.getComponent(AnimationComponent.class);
		Animation currentAnimation = animationComponent.getCurrentAnimation();
		currentAnimation.update(GlobalTime.getDelta());

		SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);
		spriteComponent.setSprite(currentAnimation.getCurrentFrame());

		aliveTime += GlobalTime.getDelta();
		maxLinearSpeed = 5f + aliveTime * 0.028f;

		// System.out.println(maxLinearSpeed);

	}

}
