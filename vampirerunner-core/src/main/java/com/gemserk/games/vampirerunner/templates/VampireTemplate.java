package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.PhysicsImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.scripts.VampireScript;
import com.gemserk.resources.ResourceManager;

public class VampireTemplate extends EntityTemplateImpl {

	private final ResourceManager<String> resourceManager;
	private final BodyBuilder bodyBuilder;

	public VampireTemplate(ResourceManager<String> resourceManager, BodyBuilder bodyBuilder) {
		this.resourceManager = resourceManager;
		this.bodyBuilder = bodyBuilder;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Sprite sprite = resourceManager.getResourceValue("VampireSprite");

		entity.addComponent(new TagComponent(Tags.Vampire));
		// entity.addComponent(new SpatialComponent(spatial));
		entity.addComponent(new SpriteComponent(sprite, new Vector2(0.5f, 0.5f), Color.WHITE));
		entity.addComponent(new RenderableComponent(1));
		entity.addComponent(new ScriptComponent(new VampireScript()));

		Body body = bodyBuilder //
				.fixedRotation() //
				.userData(entity) //
				.position(spatial.getX(), spatial.getY()) //
				.type(BodyType.DynamicBody) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(0.2f, 1f) //
						.density(1f) //
						.build()) //
				.build();

		entity.addComponent(new PhysicsComponent(new PhysicsImpl(body)));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, 1f, 1f)));
	}

}
