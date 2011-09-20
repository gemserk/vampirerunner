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
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.games.vampirerunner.Collisions;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.resources.ResourceManager;

@Deprecated
public class ObstacleTemplate extends EntityTemplateImpl {

	private final BodyBuilder bodyBuilder;
	private final ResourceManager<String> resourceManager;
	
	{
		parameters.put("x", new Float(0f));
		parameters.put("y", new Float(0f));
		parameters.put("width", new Float(2f));
		parameters.put("height", new Float(20f));
	}

	public ObstacleTemplate(ResourceManager<String> resourceManager, BodyBuilder bodyBuilder) {
		this.resourceManager = resourceManager;
		this.bodyBuilder = bodyBuilder;
	}

	@Override
	public void apply(Entity entity) {
		Float x = parameters.get("x");
		Float y = parameters.get("y");

		Float width = parameters.get("width");
		Float height= parameters.get("height");
		
		Sprite sprite = resourceManager.getResourceValue("WhiteRectangleSprite");
		
		entity.addComponent(new SpriteComponent(sprite, new Vector2(0.5f, 0.5f), Color.BLACK));
		entity.addComponent(new RenderableComponent(2));

		entity.setGroup(Groups.Obstacles);
		
		Body body = bodyBuilder //
				.userData(entity) //
				.position(x, y) //
				.type(BodyType.StaticBody) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(width * 0.5f, height * 0.5f) //
						.density(1f) //
						.categoryBits(Collisions.Obstacle )//
						.build()) //
				.build();

		entity.addComponent(new PhysicsComponent(new PhysicsImpl(body)));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, width, height)));
	}

}
