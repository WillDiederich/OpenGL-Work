package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a2.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class PanUp extends AbstractAction{
	private Camera cam;

	public PanUp(Camera c) {
		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		cam.pitch(3.0);
	}
}