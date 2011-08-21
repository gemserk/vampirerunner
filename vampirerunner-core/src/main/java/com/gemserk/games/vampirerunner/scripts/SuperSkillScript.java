package com.gemserk.games.vampirerunner.scripts;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.games.vampirerunner.Collisions;
import com.gemserk.games.vampirerunner.components.Components.SuperSkillComponent;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireController;

public class SuperSkillScript extends ScriptJavaImpl {

	private static final Class<PhysicsComponent> physicsComponentClass = PhysicsComponent.class;
	private static final Class<SuperSkillComponent> superSkillComponentClass = SuperSkillComponent.class;

	private final VampireController vampireController;

	public SuperSkillScript(VampireController vampireController) {
		this.vampireController = vampireController;
	}

	@Override
	public void update(World world, Entity e) {
		PhysicsComponent physicsComponent = e.getComponent(physicsComponentClass);
		Physics physics = physicsComponent.getPhysics();

		Body body = physics.getBody();

		SuperSkillComponent superSkillComponent = e.getComponent(superSkillComponentClass);

		// Gdx.app.log("VampireRunner", "Energy: " + superSkillComponent.energy);

		if (!superSkillComponent.enabled) {
			superSkillComponent.energy.add(superSkillComponent.regenerationRate * GlobalTime.getDelta());

			if (vampireController.usingSuperSkill && superSkillComponent.energy.isFull()) {
				// Gdx.app.log("VampireRunner", "Making vladimir invulnerable");
				superSkillComponent.enabled = true;

				ArrayList<Fixture> fixtureList = body.getFixtureList();
				for (int i = 0; i < fixtureList.size(); i++) {
					Fixture fixture = fixtureList.get(i);
					Filter filterData = fixture.getFilterData();
					filterData.maskBits = 0x00;
					fixture.setFilterData(filterData);
				}
			}
		} else {
			superSkillComponent.energy.remove(superSkillComponent.consumeRate * GlobalTime.getDelta());

			if (superSkillComponent.energy.isEmpty() || !vampireController.usingSuperSkill) {
				// Gdx.app.log("VampireRunner", "Making vladimir vulnerable :(");
				superSkillComponent.enabled = false;

				ArrayList<Fixture> fixtureList = body.getFixtureList();
				for (int i = 0; i < fixtureList.size(); i++) {
					Fixture fixture = fixtureList.get(i);
					Filter filterData = fixture.getFilterData();
					filterData.maskBits = Collisions.Obstacle;
					fixture.setFilterData(filterData);
				}
			}
		}

	}

}
