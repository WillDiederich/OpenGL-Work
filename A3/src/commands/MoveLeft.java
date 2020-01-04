package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a3.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class MoveLeft extends AbstractAction{
	private Camera cam;

	public MoveLeft(Camera c) {
		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		cam.moveX(-0.25);
	}
}