package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;

public class VladimirAnimationScript extends ScriptJavaImpl {

	private static final Class<AnimationComponent> animationComponentClass = AnimationComponent.class;
	private static final Class<SpriteComponent> spriteComponentClass = SpriteComponent.class;
	
	// handle events like player death, etc, to play animations?

	@Override
	public void update(World world, Entity e) {
		// should change animations based on vladimir status...
		AnimationComponent animationComponent = e.getComponent(animationComponentClass);
		Animation currentAnimation = animationComponent.getCurrentAnimation();
		currentAnimation.update(GlobalTime.getDelta());
		SpriteComponent spriteComponent = e.getComponent(spriteComponentClass);
		spriteComponent.setSprite(currentAnimation.getCurrentFrame());
	}

}
