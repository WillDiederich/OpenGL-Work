package commands;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import a3.Starter;
import objects.Camera;

@SuppressWarnings("serial")
public class LightMoveUp extends AbstractAction{
	private Starter start;
	
	public LightMoveUp(Starter c) {

		start = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		start.lightUp();
	}
}