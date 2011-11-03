package com.gemserk.games.vampirerunner.scripts;

import java.text.MessageFormat;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.animation4j.gdx.converters.ColorConverter;
import com.gemserk.animation4j.interpolator.GenericInterpolator;
import com.gemserk.animation4j.interpolator.Interpolator;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.vampirerunner.GameInformation;
import com.gemserk.games.vampirerunner.Tags;
import com.gemserk.games.vampirerunner.components.GameComponents;
import com.gemserk.games.vampirerunner.components.RunningComponent;
import com.gemserk.games.vampirerunner.components.SuperSkillComponent;

public class ObstacleGeneratorScript extends ScriptJavaImpl {

	private final String[] wallSpriteIds = { "WallTileASprite", "WallTileBSprite", "WallTileCSprite", "WallTileDSprite" };

	private final int[][] wallPatterns = { //
	// { 0, 1, 3 }, // 1.25m (approx)
			{ 0, 1, 2, 1, 3 }, // 2
			{ 0, 1, 2, 1, 2, 1, 3 }, // 3
			{ 0, 1, 2, 1, 2, 1, 2, 1, 3 }, // 4
			{ 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 3 }, // 5
			{ 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 3 }, // 6.25m (approx)
			{ 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 3 }, // 7.25m (approx)
			{ 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 3 }, // 8.25m (approx)

	};

	private final EntityFactory entityFactory;
	private final EntityTemplate tileTemplate;

	private float distanceTrigger;

	private Parameters parameters = new ParametersWrapper();

	private final Color startColor = new Color(1f, 1f, 1f, 1f);
	private final Color endColor = new Color(0.4f, 0.4f, 0.4f, 1f);
	private float alpha;

	private Interpolator<Color> interpolator;

	public ObstacleGeneratorScript(EntityFactory entityFactory, EntityTemplate tileTemplate, float distanceTrigger) {
		this.entityFactory = entityFactory;
		this.tileTemplate = tileTemplate;
		this.distanceTrigger = distanceTrigger;
	}

	@Override
	public void init(World world, Entity e) {
		alpha = 0f;
		interpolator = new GenericInterpolator<Color>(new ColorConverter());
	}

	@Override
	public void update(World world, Entity e) {
		alpha += GlobalTime.getDelta() / 120f;

		Entity character = world.getTagManager().getEntity(Tags.Vampire);

		if (character == null)
			return;

		SpatialComponent characterSpatialComponent = Components.getSpatialComponent(character);
		Spatial characterSpatial = characterSpatialComponent.getSpatial();

		Color color = interpolator.interpolate(startColor, endColor, alpha);

		if (characterSpatial.getX() > distanceTrigger) {

			SuperSkillComponent superSkillComponent = GameComponents.getSuperSkillComponent(character);
			PhysicsComponent physicsComponent = Components.getPhysicsComponent(character);

			float speed = physicsComponent.getBody().getLinearVelocity().len();
			
			RunningComponent runningComponent = GameComponents.getRunningComponent(character);
			float maxSpeed = runningComponent.maxSpeed;
			
			float distanceMultiplier = maxSpeed / speed;

			float minDistance = 0.75f * (superSkillComponent.energy.getTotal() / (superSkillComponent.regenerationRate / speed));
			float maxDistance = 1f * (superSkillComponent.energy.getTotal() / (superSkillComponent.regenerationRate / speed));

			Gdx.app.log(GameInformation.applicationId, MessageFormat.format("minDistance = {0}, maxDistance = {1}", minDistance, maxDistance));
			Gdx.app.log(GameInformation.applicationId, MessageFormat.format("distanceMultiplier = {0}", distanceMultiplier));

			// float x = distanceTrigger + distanceBetweenObstacles;
			float y = 1.1f;

			int wallQuantity = 4;

			float offset = 15f;

			float random = MathUtils.random();

			float relaxChance = 0.3f;

			if (random < relaxChance) {

				// 3 seconds of relax...
				float relaxTime = 3f;
				float relaxDistance = relaxTime * speed;

				Gdx.app.log(GameInformation.applicationId, "Relax time for " + relaxDistance + "m");

				distanceTrigger += relaxDistance;

			} else {

				for (int j = 0; j < wallQuantity; j++) {

					float width = 0f;
					float x = distanceTrigger + MathUtils.random(minDistance, maxDistance) * distanceMultiplier;

					// generate a random pattern
					int[] wallPattern = wallPatterns[MathUtils.random(0, wallPatterns.length - 1)];
					// int[] wallPattern = wallPatterns[0];

					for (int i = 0; i < wallPattern.length; i++) {

						int pattern = wallPattern[i];
						String spriteId = wallSpriteIds[pattern];

						boolean generateBounding = (i > 0 && i < wallPattern.length - 1);

						Entity wallTile = entityFactory.instantiate(tileTemplate, parameters //
								.put("spriteId", spriteId) //
								.put("x", x + offset) //
								.put("y", y) //
								.put("color", color) //
								.put("generateBounding", generateBounding) //
								);

						SpatialComponent spatialComponent = Components.getSpatialComponent(wallTile);
						x += spatialComponent.getSpatial().getWidth();

						width += spatialComponent.getSpatial().getWidth();

					}

					Gdx.app.log(GameInformation.applicationId, "New obstacle generated with width of " + width + "m");

					distanceTrigger = x;
				}

			}
		}

	}

}
