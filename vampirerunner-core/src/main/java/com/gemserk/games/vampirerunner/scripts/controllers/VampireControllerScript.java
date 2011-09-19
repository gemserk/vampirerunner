package com.gemserk.games.vampirerunner.scripts.controllers;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;

public class VampireControllerScript extends ScriptJavaImpl {

	private final VampireController vampireController;

	public VampireControllerScript(VampireController vampireController) {
		this.vampireController = vampireController;
	}

	@Override
	public void update(World world, Entity e) {
		vampireController.usingSuperSkill = Gdx.input.isTouched();
	}

}
