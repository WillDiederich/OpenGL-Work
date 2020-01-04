package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a2.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class MoveDown extends AbstractAction{
	private Camera cam;

	public MoveDown(Camera c) {
		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		cam.moveY(0.25);
	}
}