package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.resources.ResourceManager;

public class FloorTileTemplate extends EntityTemplateImpl {

	private final ResourceManager<String> resourceManager;
	
	{
		parameters.put("x", new Float(0f));
		parameters.put("y", new Float(0f));
		parameters.put("width", new Float(1f));
		parameters.put("height", new Float(1f));
	}

	public FloorTileTemplate(ResourceManager<String> resourceManager, BodyBuilder bodyBuilder) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		Float x = parameters.get("x");
		Float y = parameters.get("y");

		Float width = parameters.get("width");
		Float height= parameters.get("height");

		Sprite sprite = resourceManager.getResourceValue("FloorTile01Sprite");

		entity.setGroup(Groups.Tiles);
		
		entity.addComponent(new SpriteComponent(sprite, new Vector2(0.5f, 0.5f), Color.WHITE));
		entity.addComponent(new RenderableComponent(0));
		entity.addComponent(new SpatialComponent(new SpatialImpl(x, y, width, height, 0f)));
	}

}
