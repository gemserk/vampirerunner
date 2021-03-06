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
import com.gemserk.games.vampirerunner.components.DistanceComponent;
import com.gemserk.games.vampirerunner.components.RunningComponent;
import com.gemserk.games.vampirerunner.components.SuperSkillComponent;
import com.gemserk.games.vampirerunner.scripts.RunningScript;
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

		Body body = bodyBuilder //
				.fixedRotation() //
				.userData(entity) //
				.position(spatial.getX(), spatial.getY()) //
				.type(BodyType.DynamicBody) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						// .boxShape(0.1f, 0.4f) //
						.circleShape(0.05f) //
						.density(1f) //
						.sensor() //
						.categoryBits(Collisions.Vladimir) //
						.maskBits(Collisions.All).build()) //
				.build();

		entity.addComponent(new TagComponent(Tags.Vampire));
		entity.addComponent(new SpriteComponent(runningAnimation.getCurrentFrame(), new Vector2(0.5f, 0.5f), Color.WHITE));
		entity.addComponent(new AnimationComponent(new Animation[] { runningAnimation, flyingAnimation }));
		entity.addComponent(new RenderableComponent(3));

		entity.addComponent(new SuperSkillComponent(new Container(50f, 50f), 75f, 25f));

		entity.addComponent(new DistanceComponent());
		
		float maxSpeed = 25f;
		float minSpeed = 7f;
		float timeToMaxSpeed = 60f;
		
		float forceToMaxSpeed = ((maxSpeed - minSpeed) / timeToMaxSpeed) * body.getMass();
		
		entity.addComponent(new RunningComponent(new Vector2(forceToMaxSpeed, 0f), maxSpeed, minSpeed));

		entity.addComponent(new ScriptComponent(
				new VladimirAnimationScript(), //
				new SuperSkillScript(vampireController), //
				new RunningScript(), //
				new UpdateDistanceScript(), //
				new VladimirHealthScript(eventManager) //
		));

		entity.addComponent(new PhysicsComponent(new PhysicsImpl(body)));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, 1f, 1f)));
		entity.addComponent(new PreviousStateSpatialComponent());
	}
}
