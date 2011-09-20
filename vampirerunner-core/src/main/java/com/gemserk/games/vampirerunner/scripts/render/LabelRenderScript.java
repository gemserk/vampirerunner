package com.gemserk.games.vampirerunner.scripts.render;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.vampirerunner.Groups;
import com.gemserk.games.vampirerunner.components.ScoreLabelComponent;

public class LabelRenderScript extends ScriptJavaImpl {

	private final Libgdx2dCamera camera;
	private SpriteBatch spriteBatch;
	private BitmapFont font = new BitmapFont();

	private final Vector2 labelPosition = new Vector2();

	public LabelRenderScript(Libgdx2dCamera camera) {
		this.camera = camera;
	}

	@Override
	public void init(World world, Entity e) {
		spriteBatch = new SpriteBatch();
	}

	@Override
	public void update(World world, Entity e) {
		ImmutableBag<Entity> positionLabels = world.getGroupManager().getEntities(Groups.PositionLabel);

		spriteBatch.begin();

		for (int i = 0; i < positionLabels.size(); i++) {

			// if position label inside screen...

			Entity positionLabel = positionLabels.get(i);
			SpatialComponent spatialComponent = positionLabel.getComponent(SpatialComponent.class);
			ScoreLabelComponent scoreLabelComponent = positionLabel.getComponent(ScoreLabelComponent.class);

			Spatial spatial = spatialComponent.getSpatial();

			labelPosition.set(spatial.getX(), spatial.getY());

			camera.project(labelPosition);

			font.setColor(1f, 1f, 1f, 1f);
			font.draw(spriteBatch, scoreLabelComponent.getLabel(), labelPosition.x, labelPosition.y);

		}

		spriteBatch.end();
	}

	@Override
	public void dispose(World world, Entity e) {
		spriteBatch.dispose();
	}

}