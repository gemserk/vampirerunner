package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;
import com.gemserk.scores.Score;

public class ScoreLabelComponent extends Component {

	private final Score score;
	private final int position;
	private final String label;

	public int getPosition() {
		return position;
	}

	public Score getScore() {
		return score;
	}
	
	public String getLabel() {
		return label;
	}

	public ScoreLabelComponent(int position, Score score) {
		this.position = position;
		this.score = score;
		this.label = "" + position;
	}

}