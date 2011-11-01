package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.componentsengine.utils.Container;

public class Components {

	public static class DistanceComponent extends Component {

		public float distance;

		public DistanceComponent() {
			this.distance = 0f;
		}

	}

	public static class SuperSkillComponent extends Component {

		public Container energy;
		public float regenerationRate;
		public float consumeRate;
		public boolean enabled;

		public SuperSkillComponent(Container energy, float regenerationRate, float consumeRate) {
			this.energy = energy;
			this.regenerationRate = regenerationRate;
			this.consumeRate = consumeRate;
			this.enabled = false;
		}
	}
	
	public static class BoundsComponent extends Component {
		
		public Rectangle bounds;

		public BoundsComponent(Rectangle rectangle) {
			this.bounds = rectangle;
		}
		
	}

}
