package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import objects.Camera;


@SuppressWarnings("serial")
public class MoveForward extends AbstractAction{
	private Camera cam;

	public MoveForward(Camera c) {
		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		cam.moveZ(-0.25);
	}
}