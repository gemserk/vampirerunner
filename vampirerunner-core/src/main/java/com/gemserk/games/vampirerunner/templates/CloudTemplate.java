package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.games.vampirerunner.components.Components.BoundsComponent;
import com.gemserk.resources.ResourceManager;

public class CloudTemplate extends EntityTemplateImpl {
	
	private final ResourceManager<String> resourceManager;
	
	public CloudTemplate(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		String spriteId = parameters.get("spriteId");
		
		Float x = parameters.get("x");
		Float y = parameters.get("y");
		
		Float speed = parameters.get("speed");
		
		Integer layer = parameters.get("layer");
		
		Sprite sprite = resourceManager.getResourceValue(spriteId);
		if (sprite == null)
			throw new RuntimeException("Failed to instantiate static sprite, " + spriteId + " not found.");
		
		entity.setGroup(Groups.Clouds);
		
		float width = sprite.getWidth() * 2;
		float height = sprite.getHeight() * 2;
		
		entity.addComponent(new SpatialComponent(new SpatialImpl(x, y, width, height, 0f)));
		entity.addComponent(new SpriteComponent(sprite));
		entity.addComponent(new RenderableComponent(layer));
		
		entity.addComponent(new BoundsComponent(new Rectangle(0f, 0f, width, height)));
		entity.addComponent(new MovementComponent(new Vector2(speed, 0f), 0f));
		
	}

}
