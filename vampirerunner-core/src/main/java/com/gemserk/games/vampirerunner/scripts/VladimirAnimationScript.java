package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.games.vampirerunner.components.Components.SuperSkillComponent;

public class VladimirAnimationScript extends ScriptJavaImpl {

	private static final Class<AnimationComponent> animationComponentClass = AnimationComponent.class;
	private static final Class<SpriteComponent> spriteComponentClass = SpriteComponent.class;
	private static final Class<SuperSkillComponent> superSkillComponentClass = SuperSkillComponent.class;
	
	// handle events like player death, etc, to play animations?
	private static final int RunningAnimation = 0;
	private static final int FlyingAnimation = 1;

	@Override
	public void update(World world, Entity e) {
		// should change animations based on vladimir status...
		
		SuperSkillComponent superSkillComponent = e.getComponent(superSkillComponentClass);
		
		AnimationComponent animationComponent = e.getComponent(animationComponentClass);
		
		if (superSkillComponent.enabled) {
			animationComponent.setCurrentAnimation(FlyingAnimation);
		} else {
			animationComponent.setCurrentAnimation(RunningAnimation);
		}
		
		Animation currentAnimation = animationComponent.getCurrentAnimation();
		currentAnimation.update(GlobalTime.getDelta());
		SpriteComponent spriteComponent = e.getComponent(spriteComponentClass);
		spriteComponent.setSprite(currentAnimation.getCurrentFrame());
		
		if (superSkillComponent.enabled) {
			spriteComponent.getColor().a = 0.5f;
		} else {
			spriteComponent.getColor().a = 1f;
		}
	}

}
