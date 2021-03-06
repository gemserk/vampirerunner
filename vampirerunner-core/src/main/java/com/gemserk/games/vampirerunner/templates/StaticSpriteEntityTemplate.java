package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.resources.ResourceManager;

public class StaticSpriteEntityTemplate extends EntityTemplateImpl {
	
	private final ResourceManager<String> resourceManager;
	
	{
		parameters.put("layer", new Integer(0));
	}

	public StaticSpriteEntityTemplate(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		String spriteId = parameters.get("spriteId");
		Spatial spatial = parameters.get("spatial");
		Integer layer = parameters.get("layer");
		
		Sprite sprite = resourceManager.getResourceValue(spriteId);
		if (sprite == null)
			throw new RuntimeException("Failed to instantiate static sprite, " + spriteId + " not found.");
		
		entity.addComponent(new SpatialComponent(spatial));
		entity.addComponent(new SpriteComponent(sprite));
		entity.addComponent(new RenderableComponent(layer));
	}

}
