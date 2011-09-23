package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.components.CameraComponent;
import com.gemserk.games.vampirerunner.components.PreviousSpatialStateComponent;
import com.gemserk.games.vampirerunner.scripts.CameraScript;

public class CameraTemplate extends EntityTemplateImpl {

	@Override
	public void apply(Entity entity) {
		Libgdx2dCamera libgdx2dCamera = parameters.get("libgdxCamera");
		entity.addComponent(new TagComponent(Tags.MainCamera));
		
		entity.addComponent(new CameraComponent(libgdx2dCamera));
		entity.addComponent(new SpatialComponent(new SpatialImpl(0, 0)));
		entity.addComponent(new PreviousSpatialStateComponent());
		
		entity.addComponent(new ScriptComponent(new CameraScript(Tags.Vampire, libgdx2dCamera)));
	}

}
