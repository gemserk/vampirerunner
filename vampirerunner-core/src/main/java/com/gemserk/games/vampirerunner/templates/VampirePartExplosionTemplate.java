package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.resources.ResourceManager;

public class VampirePartExplosionTemplate extends EntityTemplateImpl {

	private final ResourceManager<String> resourceManager;

	public VampirePartExplosionTemplate(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");
		String partId = parameters.get("sprite");
		final Vector2 direction = parameters.get("direction");

		Sprite sprite = resourceManager.getResourceValue(partId);

		entity.addComponent(new SpriteComponent(sprite, new Vector2(0.5f, 0.5f), Color.WHITE));
		entity.addComponent(new RenderableComponent(3));
		entity.addComponent(new SpatialComponent(spatial));

		entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
			
			Vector2 moveDirection = direction;
			
			@Override
			public void update(World world, Entity e) {
				SpatialComponent spatialComponent = e.getComponent(SpatialComponent.class);
				Spatial spatial = spatialComponent.getSpatial();
				
				spatial.setAngle(spatial.getAngle() + 360f * GlobalTime.getDelta());
				float x = spatial.getX();
				float y = spatial.getY();
				
				spatial.setPosition(x + moveDirection.x * GlobalTime.getDelta(), y + moveDirection.y * GlobalTime.getDelta());
			}
		}));

	}
}
