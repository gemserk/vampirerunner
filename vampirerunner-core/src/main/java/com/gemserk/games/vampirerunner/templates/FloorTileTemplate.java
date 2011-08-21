package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.PhysicsImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.resources.ResourceManager;

public class FloorTileTemplate extends EntityTemplateImpl {

	private final ResourceManager<String> resourceManager;
	private final BodyBuilder bodyBuilder;

	public FloorTileTemplate(ResourceManager<String> resourceManager, BodyBuilder bodyBuilder) {
		this.resourceManager = resourceManager;
		this.bodyBuilder = bodyBuilder;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");
		
		Sprite sprite = resourceManager.getResourceValue("FloorTile01Sprite");

		entity.addComponent(new SpriteComponent(sprite, new Vector2(0.5f, 0.5f), Color.WHITE));
		entity.addComponent(new RenderableComponent(2));

		Body body = bodyBuilder //
				.userData(entity) //
				.position(spatial.getX(), spatial.getY()) //
				.type(BodyType.StaticBody) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(1f * spatial.getWidth() * 0.5f, 0.15f * spatial.getHeight() * 0.5f) //
						.density(1f) //
						.build()) //
				.build();

		entity.addComponent(new PhysicsComponent(new PhysicsImpl(body)));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
	}

}
