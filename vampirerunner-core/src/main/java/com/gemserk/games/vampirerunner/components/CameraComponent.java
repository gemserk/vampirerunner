package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;

public class CameraComponent extends Component {
	
	private Libgdx2dCamera libgdx2dCamera;
	
	public Libgdx2dCamera getLibgdx2dCamera() {
		return libgdx2dCamera;
	}
	
	public void setLibgdx2dCamera(Libgdx2dCamera libgdx2dCamera) {
		this.libgdx2dCamera = libgdx2dCamera;
	}

	public CameraComponent(Libgdx2dCamera libgdx2dCamera) {
		this.libgdx2dCamera = libgdx2dCamera;
	}

}
