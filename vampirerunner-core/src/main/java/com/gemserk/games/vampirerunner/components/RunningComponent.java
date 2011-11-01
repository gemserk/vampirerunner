package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class RunningComponent extends Component {
	
	public Vector2 force = new Vector2();
	public float maxSpeed;
	public float initialSpeed;
	
	public RunningComponent(Vector2 force, float maxSpeed, float initialSpeed) {
		this.initialSpeed = initialSpeed;
		this.force.set(force);
		this.maxSpeed = maxSpeed;
	}

}
