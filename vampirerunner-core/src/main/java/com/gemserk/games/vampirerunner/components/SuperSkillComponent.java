package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;
import com.gemserk.componentsengine.utils.Container;

public class SuperSkillComponent extends Component {

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