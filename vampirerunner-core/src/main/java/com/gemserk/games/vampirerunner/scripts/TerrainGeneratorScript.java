package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.Tags;

public class TerrainGeneratorScript extends ScriptJavaImpl {

	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;

	private final EntityFactory entityFactory;
	private final EntityTemplate tileTemplate;

	private float lastGeneratedPositionX;
	private float distanceToGenerate = 24f;

	private Transition<Color> floorColorTransition;

	private Parameters parameters = new ParametersWrapper();

	public TerrainGeneratorScript(EntityFactory entityFactory, EntityTemplate tileTemplate, float lastGeneratedPositionX) {
		this.entityFactory = entityFactory;
		this.tileTemplate = tileTemplate;
		this.lastGeneratedPositionX = lastGeneratedPositionX;
	}

	@Override
	public void init(World world, Entity e) {
		floorColorTransition = Transitions.transitionBuilder(Color.WHITE).time(50f).end(new Color(0.4f, 0.4f, 0.4f, 1f)).build();
	}

	@Override
	public void update(World world, Entity e) {
		Entity player = world.getTagManager().getEntity(Tags.Vampire);
		if (player == null)
			return;

		SpatialComponent playerSpatialComponent = player.getComponent(spatialComponentClass);
		Spatial playerSpatial = playerSpatialComponent.getSpatial();

		Color color = floorColorTransition.get();

		while (lastGeneratedPositionX - playerSpatial.getX() < distanceToGenerate) {

			parameters.clear();
			Entity obstacle = entityFactory.instantiate(tileTemplate, parameters //
					.put("x", lastGeneratedPositionX) //
					.put("y", 1.4f) //
					.put("color", color) //
					);

			SpatialComponent obstacleSpatialComponent = obstacle.getComponent(spatialComponentClass);
			float width = obstacleSpatialComponent.getSpatial().getWidth();

			lastGeneratedPositionX += width;
			// generate new tile

			// Gdx.app.log("VampireRunner", "Generating new tile with color: " + color);

		}

	}

}
