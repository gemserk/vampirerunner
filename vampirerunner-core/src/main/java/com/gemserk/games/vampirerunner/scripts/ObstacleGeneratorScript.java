package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.Tags;

public class ObstacleGeneratorScript extends ScriptJavaImpl {

	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;

	private final EntityFactory entityFactory;
	private final EntityTemplate tileTemplate;

	private float distanceTrigger;

	private Parameters parameters = new ParametersWrapper();

	public ObstacleGeneratorScript(EntityFactory entityFactory, EntityTemplate tileTemplate, float distanceTrigger) {
		this.entityFactory = entityFactory;
		this.tileTemplate = tileTemplate;
		this.distanceTrigger = distanceTrigger;
	}

	@Override
	public void update(World world, Entity e) {

		Entity player = world.getTagManager().getEntity(Tags.Vampire);

		SpatialComponent playerSpatialComponent = player.getComponent(spatialComponentClass);
		Spatial playerSpatial = playerSpatialComponent.getSpatial();

		if (playerSpatial.getX() > distanceTrigger) {
			// trigger, generate multiple obstacles ahead, move distance trigger
			Gdx.app.log("VampireRunner", "Generate obstacles triggered");
			
			float width = MathUtils.random(1f, 10f);
			
			parameters.clear();
			Entity obstacle = entityFactory.instantiate(tileTemplate, parameters //
					.put("x", distanceTrigger + 20f) //
					.put("y", 1f) //
					.put("width", width)
					);

			// SpatialComponent obstacleSpatialComponent = obstacle.getComponent(spatialComponentClass);
			// float width = obstacleSpatialComponent.getSpatial().getWidth();
			
			// width = sumatoria de todos los obstaculos mas espacios en blanco
			
			distanceTrigger += 10f + width;
		}

	}

}
