package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;

public class TimedEventTemplate extends EntityTemplateImpl {

	private final EventManager eventManager;

	public TimedEventTemplate(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public void apply(Entity entity) {
		
		final Float time = parameters.get("time");
		final String eventId = parameters.get("eventId");

		entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
			
			float timerTime = time;
			String timerEventId = eventId;
			
			@Override
			public void update(World world, Entity e) {
				timerTime -= GlobalTime.getDelta();
				if (timerTime <= 0) {
					eventManager.registerEvent(timerEventId, e);
					e.delete();
				}
			}
			
		}));

	}
}
