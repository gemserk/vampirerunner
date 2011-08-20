package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.resources.ResourceManager;

public class VampireTemplate extends EntityTemplateImpl {
	
	private final ResourceManager<String> resourceManager;

	public VampireTemplate(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");
		
		Sprite sprite = resourceManager.getResourceValue("VampireSprite");
		
		entity.addComponent(new SpatialComponent(spatial));
		entity.addComponent(new SpriteComponent(sprite));
		entity.addComponent(new RenderableComponent(1));
	}

}
