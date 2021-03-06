package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.CameraComponent;
import com.gemserk.commons.artemis.components.PreviousStateCameraComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.scripts.CameraScript;

public class CameraTemplate extends EntityTemplateImpl {

	@Override
	public void apply(Entity entity) {
		Libgdx2dCamera libgdx2dCamera = parameters.get("libgdxCamera");
		Camera camera = parameters.get("camera");
		
		entity.addComponent(new TagComponent(Tags.MainCamera));
		
		entity.addComponent(new CameraComponent(libgdx2dCamera, camera));
		entity.addComponent(new PreviousStateCameraComponent(new CameraImpl()));
		
		entity.addComponent(new ScriptComponent(new CameraScript(Tags.Vampire)));
	}

}
