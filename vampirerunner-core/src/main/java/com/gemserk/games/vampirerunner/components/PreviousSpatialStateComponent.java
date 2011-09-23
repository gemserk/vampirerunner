package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;

public class PreviousSpatialStateComponent  extends Component {
	
	private Spatial spatial;
	
	public Spatial getSpatial() {
		return spatial;
	}
	
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	public PreviousSpatialStateComponent() {
		this.spatial = new SpatialImpl(0, 0);
	}

}
