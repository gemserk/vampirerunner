package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.PreviousStateSpatialComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.games.vampirerunner.components.BoundsComponent;
import com.gemserk.resources.ResourceManager;

public class CloudTemplate extends EntityTemplateImpl {

	private final ResourceManager<String> resourceManager;

	public CloudTemplate(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		String spriteId = parameters.get("spriteId");

		// default world scale, considering viewport 800f
		float worldScale = 0.015625f;

		Float x = parameters.get("x");
		Float y = parameters.get("y");

		Float speed = parameters.get("speed");

		Integer layer = parameters.get("layer");

		Sprite sprite = resourceManager.getResourceValue(spriteId);
		if (sprite == null)
			throw new RuntimeException("Failed to instantiate static sprite, " + spriteId + " not found.");

		entity.setGroup(Groups.Clouds);

		// SpriteUtils.resize(sprite, worldWidth);
		
		final float cloudSize = 2f;
		
		float width = sprite.getWidth() * worldScale * cloudSize;
		float height = sprite.getHeight() * worldScale * cloudSize;

		SpriteComponent spriteComponent = new SpriteComponent(sprite);
		
		spriteComponent.getColor().set(1f, 1f, 1f, 0f);
		Synchronizers.transition(spriteComponent.getColor(), Transitions.transitionBuilder().start(new Color(1f, 1f, 1f, 0f)).end(Color.WHITE).time(1f));
		
		entity.addComponent(new SpatialComponent(new SpatialImpl(x, y, width, height, 0f)));
		entity.addComponent(new PreviousStateSpatialComponent());
		entity.addComponent(spriteComponent);
		entity.addComponent(new RenderableComponent(layer));

		entity.addComponent(new BoundsComponent(new Rectangle(0f, 0f, width, height)));
		entity.addComponent(new MovementComponent(new Vector2(speed, 0f), 0f));

	}

}
