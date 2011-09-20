package com.gemserk.games.vampirerunner.components;

import com.artemis.Component;
import com.gemserk.commons.artemis.scripts.Script;

public class RenderScriptComponent extends Component {

	private Script[] scripts;

	public Script[] getScripts() {
		return scripts;
	}

	public RenderScriptComponent(Script... scripts) {
		if (scripts == null)
			throw new RuntimeException("Cant create a RenderScriptComponent with null scripts");
		this.scripts = scripts;
	}

}
