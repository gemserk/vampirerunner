package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.games.vampirerunner.components.ScoreLabelComponent;
import com.gemserk.scores.Score;

/**
 * Used for adding labels for players positions during the game.
 */
public class PositionLabelTemplate extends EntityTemplateImpl {

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Score score = parameters.get("score");
		Integer position = parameters.get("position");

		entity.setGroup(Groups.PositionLabel);
		entity.addComponent(new SpatialComponent(spatial));
		entity.addComponent(new ScoreLabelComponent(position, score));
	}
	
}