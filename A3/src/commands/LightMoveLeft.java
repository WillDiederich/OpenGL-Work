package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a3.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class LightMoveLeft extends AbstractAction{
	private Starter start;

	public LightMoveLeft(Starter c) {

		start = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		start.lightLeft();
	}
}