package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a3.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class MoveBackwards extends AbstractAction{
	private Camera cam;
	public MoveBackwards(Camera c) {

		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		cam.moveZ(0.25);
	}
}