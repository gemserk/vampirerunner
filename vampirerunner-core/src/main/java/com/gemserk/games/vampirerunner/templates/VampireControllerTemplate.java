package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireController;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireControllerScript;

public class VampireControllerTemplate extends EntityTemplateImpl {

	@Override
	public void apply(Entity entity) {
		
		VampireController vampireController = parameters.get("vampireController");
		
		entity.addComponent(new ScriptComponent(new VampireControllerScript(vampireController)));
		
	}

}
