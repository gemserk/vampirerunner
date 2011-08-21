package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.resources.ResourceManager;

public class VladimirBloodExplosionTemplate extends EntityTemplateImpl {

	private final ResourceManager<String> resourceManager;

	public VladimirBloodExplosionTemplate(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Animation bloodAnimation = resourceManager.getResourceValue("VampireBloodAnimation");

		entity.addComponent(new SpriteComponent(bloodAnimation.getCurrentFrame(), new Vector2(0.5f, 0.5f), Color.WHITE));
		entity.addComponent(new AnimationComponent(new Animation[] { bloodAnimation }));
		entity.addComponent(new RenderableComponent(3));

		entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
			@Override
			public void update(World world, Entity e) {
				AnimationComponent animationComponent = e.getComponent(AnimationComponent.class);
				Animation currentAnimation = animationComponent.getCurrentAnimation();
				currentAnimation.update(GlobalTime.getDelta());
				SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);
				spriteComponent.setSprite(currentAnimation.getCurrentFrame());
				
			}
		}));

		entity.addComponent(new SpatialComponent(spatial));
	}
}
