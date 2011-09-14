package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.resources.ResourceManager;

public class VampireIdleTemplate extends EntityTemplateImpl {

	static class IdleAnimationScript extends ScriptJavaImpl {
		public void update(com.artemis.World world, Entity e) {
			AnimationComponent animationComponent = e.getComponent(AnimationComponent.class);
			
			Animation currentAnimation = animationComponent.getCurrentAnimation();
			currentAnimation.update(GlobalTime.getDelta());
			
			SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);
			spriteComponent.setSprite(currentAnimation.getCurrentFrame());
		}
	}

	private final ResourceManager<String> resourceManager;

	public VampireIdleTemplate(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Animation idleAnimation = resourceManager.getResourceValue("VampireIdleAnimation");

		entity.addComponent(new TagComponent(Tags.Vampire));
		entity.addComponent(new SpriteComponent(idleAnimation.getCurrentFrame(), new Vector2(0.5f, 0.5f), Color.WHITE));
		entity.addComponent(new AnimationComponent(new Animation[] { idleAnimation }));
		entity.addComponent(new RenderableComponent(3));

		entity.addComponent(new ScriptComponent( //
				new IdleAnimationScript()
		));

		entity.addComponent(new SpatialComponent(new SpatialImpl(spatial)));
		
	}
}
