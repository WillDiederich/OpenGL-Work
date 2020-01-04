package commands;

import a2.Starter;
import objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
public class ToggleAxis extends AbstractAction{
	private Starter start;

	public ToggleAxis(Starter s) {
		start = s;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(start.isAxis())
			start.setAxis(false);
		else
			start.setAxis(true);
	}
}