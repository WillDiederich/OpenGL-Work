package commands;

import javax.swing.*;
import java.awt.event.ActionEvent;

import a3.Starter;
import objects.Camera;


@SuppressWarnings("serial")
public class LightMoveForward extends AbstractAction{
	private Starter start;

	public LightMoveForward(Starter c) {

		start = c;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		start.lightForward();
	}
}