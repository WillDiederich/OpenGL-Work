package a1;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class Vertical extends AbstractAction{
	private Starter start;
	
	public Vertical(Starter s) {
		start = s;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		start.setVerticalMovement();
	}
}