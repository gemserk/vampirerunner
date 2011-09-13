package com.gemserk.games.vampirerunner.gui;

import com.badlogic.gdx.graphics.Color;
import com.gemserk.commons.gdx.gui.ButtonHandler;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.TextButton;

public class MultipleTextButtonContainer extends Container {

	private final Color selectedColor = Color.RED;
	private final Color notOverColor = Color.WHITE;
	private final Color overColor = new Color(1f, 1f, 0f, 1f);
	
	public void add(Control control) {
		if (control instanceof TextButton) {

			final TextButton textButton = (TextButton) control;
			final ButtonHandler buttonHandler = textButton.getButtonHandler();

			textButton.setNotOverColor(notOverColor);
			textButton.setOverColor(overColor);

			textButton.setButtonHandler(new ButtonHandler() {
				@Override
				public void onPressed(Control control) {
					buttonHandler.onPressed(control);
				}

				@Override
				public void onReleased(Control control) {
					select(textButton);
					buttonHandler.onReleased(control);
				}

			});

			super.add(textButton);
		}
	}
	
	public void select(String id) {
		TextButton textButton = findControl(id);
		if (textButton == null)
			return;
		select(textButton);
	}

	private void select(final TextButton textButton) {
		for (int i = 0; i < getControls().size(); i++) {
			Control otherControl = getControls().get(i);
			if (otherControl == textButton)
				continue;

			if (!(otherControl instanceof TextButton))
				continue;

			TextButton otherTextButton = (TextButton) otherControl;
			otherTextButton.setNotOverColor(notOverColor);
			otherTextButton.setOverColor(overColor);
		}

		textButton.setNotOverColor(selectedColor);
		textButton.setOverColor(selectedColor);
	}

	@Override
	public void setPosition(float x, float y) {
		for (int i = 0; i < getControls().size(); i++) {
			Control control = getControls().get(i);
			control.setPosition(x + control.getX(), y + control.getY());
		}
	}

}