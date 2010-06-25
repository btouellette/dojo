// ButtonListener.java
// Written by Brian Ouellette
// Part of Dojo

import java.awt.event.*;
import javax.swing.*;

class ButtonListener implements ActionListener
{
	public ButtonListener()
	{
	}

	public void actionPerformed(ActionEvent e)
	{
		String name = ((AbstractButton)e.getSource()).getText();
		if(name.equals("Unbow All"))
		{
			if(Main.gender.equals("Male"))
			{
				TextActionListener.send(Main.userName + " unbows all his cards.", "Action");
			}
			else
			{
				TextActionListener.send(Main.userName + " unbows all her cards.", "Action");
			}
		}
		else if(name.equals("End Turn"))
		{
			TextActionListener.send(Main.userName + " bows.", "Action");
			TextActionListener.send(Main.userName + ": The table is yours.", "You");
		}
	}
}
