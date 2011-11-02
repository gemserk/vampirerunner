package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.games.vampirerunner.components.GameComponents;
import com.gemserk.games.vampirerunner.components.SuperSkillComponent;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireController;

public class SuperSkillScript extends ScriptJavaImpl {

	private final VampireController vampireController;

	public SuperSkillScript(VampireController vampireController) {
		this.vampireController = vampireController;
	}

	@Override
	public void update(World world, Entity e) {
		SuperSkillComponent superSkillComponent = GameComponents.getSuperSkillComponent(e);
		if (!superSkillComponent.enabled) {
			superSkillComponent.energy.add(superSkillComponent.regenerationRate * GlobalTime.getDelta());
			if (vampireController.usingSuperSkill) 
				superSkillComponent.enabled = true;
		} else {
			superSkillComponent.energy.remove(superSkillComponent.consumeRate * GlobalTime.getDelta());
			if (!vampireController.usingSuperSkill)
				superSkillComponent.enabled = false;
		}

	}

}
