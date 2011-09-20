package com.gemserk.games.vampirerunner.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.commons.artemis.scripts.Script;
import com.gemserk.games.vampirerunner.components.RenderScriptComponent;

public class RenderScriptSystem extends EntityProcessingSystem {

	private static final Class<RenderScriptComponent> renderScriptComponentClass = RenderScriptComponent.class;

	public RenderScriptSystem() {
		super(RenderScriptComponent.class);
	}

	@Override
	protected void added(Entity e) {
		super.added(e);
		Script[] scripts = e.getComponent(renderScriptComponentClass).getScripts();
		for (int i = 0; i < scripts.length; i++)
			scripts[i].init(world, e);
	}

	@Override
	protected void removed(Entity e) {
		Script[] scripts = e.getComponent(renderScriptComponentClass).getScripts();
		for (int i = 0; i < scripts.length; i++)
			scripts[i].dispose(world, e);
		super.removed(e);
	}

	@Override
	protected void process(Entity e) {
		Script[] scripts = e.getComponent(renderScriptComponentClass).getScripts();
		for (int i = 0; i < scripts.length; i++)
			scripts[i].update(world, e);
	}

}
