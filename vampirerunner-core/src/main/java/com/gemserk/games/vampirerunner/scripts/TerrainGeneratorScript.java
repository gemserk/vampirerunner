package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.gemserk.animation4j.gdx.converters.ColorConverter;
import com.gemserk.animation4j.interpolator.GenericInterpolator;
import com.gemserk.animation4j.interpolator.Interpolator;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.GlobalTime;
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

	private final Color startColor = new Color(1f, 1f, 1f, 1f);
	private final Color endColor = new Color(0.4f, 0.4f, 0.4f, 1f);
	private float alpha;

	private Parameters parameters = new ParametersWrapper();

	private Interpolator<Color> interpolator;

	public TerrainGeneratorScript(EntityFactory entityFactory, EntityTemplate tileTemplate, float lastGeneratedPositionX) {
		this.entityFactory = entityFactory;
		this.tileTemplate = tileTemplate;
		this.lastGeneratedPositionX = lastGeneratedPositionX;
	}

	@Override
	public void init(World world, Entity e) {
		alpha = 0f;
		interpolator = new GenericInterpolator<Color>(new ColorConverter());
		generateTerrain(world);
	}

	@Override
	public void update(World world, Entity e) {
		generateTerrain(world);
	}

	public void generateTerrain(World world) {
		alpha += GlobalTime.getDelta() / 120f;
		
		Entity character = world.getTagManager().getEntity(Tags.Vampire);
		if (character == null)
			return;

		SpatialComponent characterSpatialComponent = character.getComponent(spatialComponentClass);
		Spatial characterSpatial = characterSpatialComponent.getSpatial();

		Color color = interpolator.interpolate(startColor, endColor, alpha);

		while (lastGeneratedPositionX - characterSpatial.getX() < distanceToGenerate) {

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
