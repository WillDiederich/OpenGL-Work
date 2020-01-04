package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a2.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class MoveRight extends AbstractAction{
	private Camera cam;

	public MoveRight(Camera c) {
		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		cam.moveX(0.25);
	}
}