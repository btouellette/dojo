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
			// Unbow all cards on the table
			for(PlayableCard card : Main.state.getDisplayedCards())
			{
				card.unbow();
				// And all cards attached to them
				for(PlayableCard attachment : card.getAllAttachments())
				{
					attachment.unbow();
				}
			}
			// And all cards attached to provinces
			for(Province province : Main.state.getProvinces())
			{
				for(PlayableCard attachment : province.getAttachments())
				{
					attachment.unbow();
				}
			}
			// And repaint so the unbowing is reflected in the display
			Main.playArea.repaint();
			// Send a message to the chatbox
			TextActionListener.send(Preferences.userName + " unbows all " + Preferences.gender + " cards.", "Action");
		}
		else if(name.equals("End Turn"))
		{
			// Send a message to the chatbox to notify other player of end of turn
			TextActionListener.send(Preferences.userName + " bows.", "Action");
			TextActionListener.send(Preferences.userName + ": The table is yours.", "You");
		}
	}
}
