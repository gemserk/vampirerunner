package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.games.vampirerunner.Events;
import com.gemserk.games.vampirerunner.components.Components.SuperSkillComponent;

public class VladimirHealthScript extends ScriptJavaImpl {

	private static final Class<PhysicsComponent> physicsComponentClass = PhysicsComponent.class;
	private static final Class<SuperSkillComponent> superSkillComponentClass = SuperSkillComponent.class;

	private final EventManager eventManager;
	private boolean enabled;

	public VladimirHealthScript(EventManager eventManager) {
		this.eventManager = eventManager;
		this.enabled = true;
	}

	@Override
	public void update(World world, Entity e) {
		if (!enabled)
			return;

		SuperSkillComponent superSkillComponent = e.getComponent(superSkillComponentClass);
		if (superSkillComponent.enabled && !superSkillComponent.energy.isEmpty())
			return;

		PhysicsComponent physicsComponent = e.getComponent(physicsComponentClass);
		Physics physics = physicsComponent.getPhysics();

		Contacts contacts = physics.getContact();
		if (!contacts.isInContact())
			return;
		eventManager.registerEvent(Events.playerDeath, e);
		enabled = false;
		return;
	}

}
