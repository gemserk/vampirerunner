package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
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
		Sprite bloodSprite = parameters.get("sprite");

		Score score = parameters.get("score");
		Integer position = parameters.get("position");

		entity.setGroup(Groups.PositionLabel);
		entity.addComponent(new SpatialComponent(spatial));
		entity.addComponent(new ScoreLabelComponent(position, score));
		
		entity.addComponent(new SpriteComponent(bloodSprite));
		
//		entity.addComponent(new SpriteComponent(bloodSprite, 0.5f, 0.2f, Color.WHITE));
	}
	
}