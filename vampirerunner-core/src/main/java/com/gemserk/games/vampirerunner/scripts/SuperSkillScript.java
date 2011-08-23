package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.games.vampirerunner.components.Components.SuperSkillComponent;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireController;

public class SuperSkillScript extends ScriptJavaImpl {

	private static final Class<SuperSkillComponent> superSkillComponentClass = SuperSkillComponent.class;

	private final VampireController vampireController;

	public SuperSkillScript(VampireController vampireController) {
		this.vampireController = vampireController;
	}

	@Override
	public void update(World world, Entity e) {
		SuperSkillComponent superSkillComponent = e.getComponent(superSkillComponentClass);
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
