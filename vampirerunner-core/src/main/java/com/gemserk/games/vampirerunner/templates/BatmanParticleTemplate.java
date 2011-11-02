package com.gemserk.games.vampirerunner.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.CameraComponent;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.ParticleEmitterComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.vampirerunner.resources.GameResources;
import com.gemserk.resources.ResourceManager;

public class BatmanParticleTemplate extends EntityTemplateImpl {
	
	public static class FollowCursorScript extends ScriptJavaImpl {
		
		private final Vector2 position = new Vector2();
		
		@Override
		public void update(World world, Entity e) {
			super.update(world, e);
			
			CameraComponent cameraComponent = Components.getCameraComponent(e);
			SpatialComponent spatialComponent = Components.getSpatialComponent(e);
			
			Libgdx2dCamera libgdx2dCamera = cameraComponent.getLibgdx2dCamera();
			
			position.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			
			libgdx2dCamera.unproject(position);
			
			spatialComponent.getSpatial().setPosition(position.x, position.y);
			
		}
		
	}
	
	ResourceManager<String> resourceManager;
	Injector injector;

	@Override
	public void apply(Entity entity) {
		Libgdx2dCamera libgdxCamera = parameters.get("libgdxCamera");
		Camera camera = parameters.get("camera");
		
		ParticleEmitter batmanEmitter = resourceManager.getResourceValue(GameResources.Emitters.BatmanEmitter);

		entity.addComponent(new SpatialComponent(new SpatialImpl(0, 0)));

		entity.addComponent(new CameraComponent(libgdxCamera, camera));
		
		entity.addComponent(new RenderableComponent(90));
		entity.addComponent(new ParticleEmitterComponent(batmanEmitter));
		
		entity.addComponent(new ScriptComponent(injector.getInstance(FollowCursorScript.class)));
	}

}