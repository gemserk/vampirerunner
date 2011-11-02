package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.games.vampirerunner.components.GameComponents;
import com.gemserk.games.vampirerunner.components.SuperSkillComponent;

public class VladimirAnimationScript extends ScriptJavaImpl {

	// handle events like player death, etc, to play animations?
	private static final int RunningAnimation = 0;
	private static final int FlyingAnimation = 1;

	@Override
	public void update(World world, Entity e) {
		// should change animations based on vladimir status...

		PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);
		Physics physics = physicsComponent.getPhysics();
		Body body = physics.getBody();

		SuperSkillComponent superSkillComponent = GameComponents.getSuperSkillComponent(e);

		AnimationComponent animationComponent = Components.getAnimationComponent(e);

		if (superSkillComponent.enabled)
			animationComponent.setCurrentAnimation(FlyingAnimation);
		else
			animationComponent.setCurrentAnimation(RunningAnimation);

		Animation currentAnimation = animationComponent.getCurrentAnimation();
		currentAnimation.update(GlobalTime.getDelta() * body.getLinearVelocity().len());
		SpriteComponent spriteComponent = Components.getSpriteComponent(e);
		spriteComponent.setSprite(currentAnimation.getCurrentFrame());

		if (superSkillComponent.enabled)
			spriteComponent.getColor().a = 0.5f;
		else
			spriteComponent.getColor().a = 1f;
	}

}
