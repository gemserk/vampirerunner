package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.box2d.Contact;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.games.vampirerunner.Events;

public class VladimirHealthScript extends ScriptJavaImpl {

	private static final Class<PhysicsComponent> physicsComponentClass = PhysicsComponent.class;

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
		PhysicsComponent physicsComponent = e.getComponent(physicsComponentClass);
		Physics physics = physicsComponent.getPhysics();

		Contact contact = physics.getContact();
		for (int i = 0; i < contact.getContactCount(); i++) {
			if (!contact.isInContact(i))
				continue;
			eventManager.registerEvent(Events.playerDeath, e);
			enabled = false;
		}

	}

}
