package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.components.BoundsComponent;

public class CloudSpawnerTemplate extends EntityTemplateImpl {

	class CloudSpawnerScript extends ScriptJavaImpl {

		private final String[] cloudSpriteIds = { "Cloud01Sprite", "Cloud02Sprite", "Cloud03Sprite" };
		private int cloudsCount = 4;
		private Rectangle bounds;
		
		public CloudSpawnerScript(Rectangle bounds) {
			this.bounds = bounds;
		}

		@Override
		public void init(World world, Entity e) {
			while (cloudsCount > 0) {
				float randomY = (float) Math.floor(MathUtils.random(bounds.y, bounds.y + bounds.height));
				entityFactory.instantiate(cloudTemplate, new ParametersWrapper() //
						.put("spriteId", cloudSpriteIds[MathUtils.random(0, cloudSpriteIds.length - 1)]) //
						.put("layer", -250) //
						.put("x", MathUtils.random(bounds.x, bounds.x + bounds.width * 3f)) //
						.put("y", randomY) //
						.put("speed", -1f) //
						);
				cloudsCount--;
			}
		}

		@Override
		public void update(World world, Entity e) {

			ImmutableBag<Entity> clouds = world.getGroupManager().getEntities(Groups.Clouds);

			for (int i = 0; i < clouds.size(); i++) {
				Entity cloud = clouds.get(i);

				SpatialComponent spatialComponent = cloud.getComponent(SpatialComponent.class);
				Spatial spatial = spatialComponent.getSpatial();

				BoundsComponent boundsComponent = cloud.getComponent(BoundsComponent.class);
				Rectangle cloudBounds = boundsComponent.bounds;

				float x = spatial.getX() + cloudBounds.x + cloudBounds.width * 0.5f;

				if (bounds.x > x) {
					cloud.delete();
					cloudsCount++;
				}

			}

			while (cloudsCount > 0) {
				float randomY = (float) Math.floor(MathUtils.random(bounds.y, bounds.y + bounds.height));
				Entity cloud = entityFactory.instantiate(cloudTemplate, new ParametersWrapper() //
						.put("spriteId", cloudSpriteIds[MathUtils.random(0, cloudSpriteIds.length - 1)]) //
						.put("layer", -250) //
						.put("x", 0f) //
						.put("y", randomY) //
						.put("speed", -1f) //
						);

				SpatialComponent spatialComponent = cloud.getComponent(SpatialComponent.class);
				Spatial spatial = spatialComponent.getSpatial();

				BoundsComponent boundsComponent = cloud.getComponent(BoundsComponent.class);
				Rectangle cloudBounds = boundsComponent.bounds;

				float x = bounds.width + MathUtils.random(bounds.x, bounds.x + bounds.width) + cloudBounds.width * 0.5f;

				spatial.setPosition(x, spatial.getY());

				cloudsCount--;
			}

		}
	}

	private final EntityTemplate cloudTemplate;
	private final EntityFactory entityFactory;

	public CloudSpawnerTemplate(EntityTemplate cloudTemplate, EntityFactory entityFactory) {
		this.cloudTemplate = cloudTemplate;
		this.entityFactory = entityFactory;
	}

	@Override
	public void apply(Entity entity) {
		Rectangle bounds = parameters.get("bounds");
		entity.addComponent(new TagComponent(Tags.CloudSpawner));
		entity.addComponent(new ScriptComponent(new CloudSpawnerScript(bounds)));
	}
}
