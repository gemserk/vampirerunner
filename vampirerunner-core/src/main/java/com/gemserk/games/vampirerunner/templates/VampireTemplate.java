package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.PreviousStateSpatialComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.PhysicsImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.componentsengine.utils.Container;
import com.gemserk.games.vampirerunner.Collisions;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.components.Components;
import com.gemserk.games.vampirerunner.components.Components.DistanceComponent;
import com.gemserk.games.vampirerunner.components.Components.SuperSkillComponent;
import com.gemserk.games.vampirerunner.scripts.ApplyLinearForceScript;
import com.gemserk.games.vampirerunner.scripts.IncrementLinearSpeedOverTimeScript;
import com.gemserk.games.vampirerunner.scripts.LimitLinearSpeedScript;
import com.gemserk.games.vampirerunner.scripts.SuperSkillScript;
import com.gemserk.games.vampirerunner.scripts.UpdateDistanceScript;
import com.gemserk.games.vampirerunner.scripts.VladimirAnimationScript;
import com.gemserk.games.vampirerunner.scripts.VladimirHealthScript;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireController;
import com.gemserk.resources.ResourceManager;

public class VampireTemplate extends EntityTemplateImpl {

	private final ResourceManager<String> resourceManager;
	private final BodyBuilder bodyBuilder;
	private final EventManager eventManager;

	public VampireTemplate(ResourceManager<String> resourceManager, BodyBuilder bodyBuilder, EventManager eventManager) {
		this.resourceManager = resourceManager;
		this.bodyBuilder = bodyBuilder;
		this.eventManager = eventManager;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");
		VampireController vampireController = parameters.get("vampireController");

		Animation runningAnimation = resourceManager.getResourceValue("VampireRunningAnimation");
		Animation flyingAnimation = resourceManager.getResourceValue("VampireFlyingAnimation");

		entity.addComponent(new TagComponent(Tags.Vampire));
		entity.addComponent(new SpriteComponent(runningAnimation.getCurrentFrame(), new Vector2(0.5f, 0.5f), Color.WHITE));
		entity.addComponent(new AnimationComponent(new Animation[] { runningAnimation, flyingAnimation }));
		entity.addComponent(new RenderableComponent(3));

		entity.addComponent(new SuperSkillComponent(new Container(50f, 50f), 75f, 25f));

		entity.addComponent(new DistanceComponent());
		entity.addComponent(new Components.MaxSpeedComponent(5f));
		entity.addComponent(new ScriptComponent(new LimitLinearSpeedScript(), //
				new VladimirAnimationScript(), //
				new SuperSkillScript(vampireController), //
				new ApplyLinearForceScript(new Vector2(500f, 0f)), //
				new IncrementLinearSpeedOverTimeScript(),//
				new UpdateDistanceScript(), //
				new VladimirHealthScript(eventManager) //
		));

		Body body = bodyBuilder //
				.fixedRotation() //
				.userData(entity) //
				.position(spatial.getX(), spatial.getY()) //
				.type(BodyType.DynamicBody) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						// .boxShape(0.1f, 0.4f) //
						.circleShape(0.1f) //
						.density(1f) //
						.sensor() //
						.categoryBits(Collisions.Vladimir) //
						.maskBits(Collisions.All).build()) //
				.build();

		entity.addComponent(new PhysicsComponent(new PhysicsImpl(body)));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, 1f, 1f)));
		entity.addComponent(new PreviousStateSpatialComponent());
	}
}
