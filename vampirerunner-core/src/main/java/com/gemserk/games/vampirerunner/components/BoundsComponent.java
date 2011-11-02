package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;

public class BoundsComponent extends Component {
	
	public Rectangle bounds;

	public BoundsComponent(Rectangle rectangle) {
		this.bounds = rectangle;
	}
	
}