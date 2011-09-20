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
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.games.vampirerunner.Collisions;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.resources.ResourceManager;

public class WallTileTemplate extends EntityTemplateImpl {

	private final ResourceManager<String> resourceManager;
	private final BodyBuilder bodyBuilder;

	public WallTileTemplate(ResourceManager<String> resourceManager, BodyBuilder bodyBuilder) {
		this.resourceManager = resourceManager;
		this.bodyBuilder = bodyBuilder;
	}

	@Override
	public void apply(Entity entity) {
		String spriteId = parameters.get("spriteId");

		Float x = parameters.get("x");
		Float y = parameters.get("y");

		Sprite sprite = resourceManager.getResourceValue(spriteId);

		SpriteUtils.resize(sprite, sprite.getWidth() / 32f);

		float width = sprite.getWidth();
		float height = sprite.getHeight();

		entity.setGroup(Groups.Obstacles);

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(width * 0.5f, height * 0.5f, new Vector2(width * 0.5f, height * 0.5f), 0f) //
						.categoryBits(Collisions.Obstacle) //
				) //
				.type(BodyType.StaticBody) //
				.position(x, y) //
				.userData(entity) //
				.build();

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, sprite.getWidth(), sprite.getHeight())));
		entity.addComponent(new SpriteComponent(sprite, 0f, 0f, Color.WHITE));
		entity.addComponent(new RenderableComponent(2));
	}

}