package a1;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class Circle extends AbstractAction{
	private Starter start;
	
	public Circle(Starter s) {
		start = s;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		start.setCircularMovement();
	}
}