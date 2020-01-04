package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;
import a3.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class PanRight extends AbstractAction{
	private Camera cam;

	public PanRight(Camera c) {
		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		cam.pan(-3.0);
	}
}