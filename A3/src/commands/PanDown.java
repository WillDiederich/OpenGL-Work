package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a3.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class PanDown extends AbstractAction{
	private Camera cam;

	public PanDown(Camera c) {
		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		cam.pitch(-3.0);
	}
}