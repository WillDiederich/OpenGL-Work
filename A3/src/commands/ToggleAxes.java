package commands;

import a3.Starter;

import javax.swing.*;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
public class ToggleAxes extends AbstractAction{
    private Starter start;
    public ToggleAxes(Starter c) {
        start = c;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        start.setAxesOn();
    }
}