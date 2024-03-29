package dojo;

// ButtonListener.java
// Written by Brian Ouellette
// Listener for all GUI buttons on the main interface

import java.awt.event.*;
import javax.swing.*;

class ButtonListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		String name = ((AbstractButton) e.getSource()).getText();
		if (name.equals("Unbow All")) {
			Main.state.unbowAll();
			// And repaint so the unbowing is reflected in the display
			Main.playArea.repaint();
			// Send a message to the chatbox
			TextActionListener.send(Main.state.name + " unbows all " + Preferences.gender + " cards.", "Action");
		} else if (name.equals("End Turn")) {
			// Send a message to the chatbox to notify other player of end of turn
			TextActionListener.send(Main.state.name + " bows.", "Action");
			TextActionListener.send(Main.state.name + ": The table is yours.", "You");
		}
	}
}
