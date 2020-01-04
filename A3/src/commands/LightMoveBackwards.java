package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a3.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class LightMoveBackwards extends AbstractAction{
	private Starter start;
	public LightMoveBackwards(Starter c) {

		start = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		start.lightBackward();
	}
}