package com.gemserk.games.vampirerunner.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.vampirerunner.Tags;

public class PreviousTilesRemoverScript extends ScriptJavaImpl {

	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;
	private float distanceToRemove = 4f;
	private String group;
	
	public PreviousTilesRemoverScript(String group) {
		this.group = group;
	}

	@Override
	public void update(World world, Entity e) {
		
		ImmutableBag<Entity> tiles = world.getGroupManager().getEntities(group);
		Entity player = world.getTagManager().getEntity(Tags.Vampire);
		
		SpatialComponent playerSpatialComponent = player.getComponent(spatialComponentClass);
		Spatial playerSpatial = playerSpatialComponent.getSpatial();
		
		for (int i = 0; i < tiles.size(); i++) {
			
			Entity tile = tiles.get(i);
			SpatialComponent tileSpatialComponent = tile.getComponent(spatialComponentClass);
			Spatial tileSpatial = tileSpatialComponent.getSpatial();
			
			if (tileSpatial.getX() < playerSpatial.getX() - distanceToRemove) {
				tile.delete();
			}
			
		}
		
	}

}
