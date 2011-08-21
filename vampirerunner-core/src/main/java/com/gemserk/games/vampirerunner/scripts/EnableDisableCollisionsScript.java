package com.gemserk.games.vampirerunner.scripts;

import java.util.ArrayList;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.games.vampirerunner.Collisions;
import com.gemserk.games.vampirerunner.scripts.controllers.VampireController;

public class EnableDisableCollisionsScript extends ScriptJavaImpl {

	private static final Class<PhysicsComponent> physicsComponentClass = PhysicsComponent.class;

	private final VampireController vampireController;

	private boolean vampireInvulnerable = false;

	public EnableDisableCollisionsScript(VampireController vampireController) {
		this.vampireController = vampireController;
	}

	@Override
	public void update(World world, Entity e) {

		// get controller and determine if the vampire should jump or not...

		PhysicsComponent physicsComponent = e.getComponent(physicsComponentClass);
		Physics physics = physicsComponent.getPhysics();

		Body body = physics.getBody();

		if (!vampireInvulnerable) {
			if (vampireController.moveThrough) {
				Gdx.app.log("VampireRunner", "Making vladimir invulnerable");
				vampireInvulnerable = true;
				
				ArrayList<Fixture> fixtureList = body.getFixtureList();
				for (int i = 0; i < fixtureList.size(); i++) {
					Fixture fixture = fixtureList.get(i);
					Filter filterData = fixture.getFilterData();
					filterData.maskBits = 0x00;
					fixture.setFilterData(filterData);
				}
			}
		} else {
			if (!vampireController.moveThrough) {
				Gdx.app.log("VampireRunner", "Making vladimir vulnerable :(");
				vampireInvulnerable = false;
				
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
