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
	private float distanceToRemove = 10f;
	private String group;
	
	public PreviousTilesRemoverScript(String group) {
		this.group = group;
	}

	@Override
	public void update(World world, Entity e) {
		ImmutableBag<Entity> entities = world.getGroupManager().getEntities(group);
		Entity player = world.getTagManager().getEntity(Tags.Vampire);
		
		if (player == null)
			return;
		
		SpatialComponent playerSpatialComponent = player.getComponent(spatialComponentClass);
		Spatial playerSpatial = playerSpatialComponent.getSpatial();
		
		for (int i = 0; i < entities.size(); i++) {
			
			Entity e2 = entities.get(i);
			SpatialComponent e2SpatialComponent = e2.getComponent(spatialComponentClass);
			Spatial e2Spatial = e2SpatialComponent.getSpatial();
			
			if (e2Spatial.getX() < playerSpatial.getX() - distanceToRemove) {
				e2.delete();
			}
			
		}
		
	}

}
