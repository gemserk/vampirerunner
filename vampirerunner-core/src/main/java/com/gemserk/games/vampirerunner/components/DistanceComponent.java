package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;

public class DistanceComponent extends Component {

	public float distance;
	public float lastPosition;

	public DistanceComponent() {
		this.distance = 0f;
		this.lastPosition = 0f;
	}

}