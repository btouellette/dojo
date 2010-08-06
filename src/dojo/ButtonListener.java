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
		String name = ((AbstractButton)e.getSource()).getText();
		if(name.equals("Unbow All"))
		{
			TextActionListener.send(Main.userName + " unbows all " + Main.gender + " cards.", "Action");
		}
		else if(name.equals("End Turn"))
		{
			TextActionListener.send(Main.userName + " bows.", "Action");
			TextActionListener.send(Main.userName + ": The table is yours.", "You");
		}
	}
}
