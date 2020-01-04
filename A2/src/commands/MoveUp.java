package commands;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import a2.Starter;
import objects.Camera;

@SuppressWarnings("serial")
public class MoveUp extends AbstractAction{
	private Camera cam;
	
	public MoveUp(Camera c) {
		cam = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		cam.moveY(-0.25);
	}
}