package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.Tags;

public class TerrainGeneratorScript extends ScriptJavaImpl {

	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;

	private final EntityFactory entityFactory;
	private final EntityTemplate tileTemplate;

	private float lastGeneratedPositionX;
	private float distanceToGenerate = 24f;
	
	private Parameters parameters = new ParametersWrapper();
	
	public TerrainGeneratorScript(EntityFactory entityFactory, EntityTemplate tileTemplate) {
		this.entityFactory = entityFactory;
		this.tileTemplate = tileTemplate;
	}

	@Override
	public void init(World world, Entity e) {
		Entity player = world.getTagManager().getEntity(Tags.Vampire);
		SpatialComponent playerSpatialComponent = player.getComponent(spatialComponentClass);
		Spatial playerSpatial = playerSpatialComponent.getSpatial();
		lastGeneratedPositionX = playerSpatial.getX() - 10f;
	}

	@Override
	public void update(World world, Entity e) {

		Entity player = world.getTagManager().getEntity(Tags.Vampire);

		SpatialComponent playerSpatialComponent = player.getComponent(spatialComponentClass);
		Spatial playerSpatial = playerSpatialComponent.getSpatial();
		
		while (lastGeneratedPositionX - playerSpatial.getX() < distanceToGenerate) {
			
			float w = 2f;
			float h = 2f;
			lastGeneratedPositionX += w;
			
			parameters.clear();
			entityFactory.instantiate(tileTemplate, parameters //
					.put("spatial", new SpatialImpl(lastGeneratedPositionX, 1f, w, h, 0f)) //
					);
			
			// generate new tile
			
			
		}

	}

}
