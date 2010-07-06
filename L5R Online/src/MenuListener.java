// MenuListener.java
// Written by Brian Ouellette
// Part of Dojo
// Listener for all the menus at the top of the panel.

import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

class MenuListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		String name = ((AbstractButton)e.getSource()).getText();
		if(name.equals("Connect"))
		{
		}
		else if(name.equals("Start Game"))
		{
		}
		else if(name.equals("Find Game"))
		{
		}
		else if(name.equals("Deckbuilder"))
		{
			Deckbuilder.frame.pack();
			Deckbuilder.frame.setVisible(true);
		}
		else if(name.equals("Preferences"))
		{
		}
		else if(name.equals("Exit"))
		{
			//Add in a prompt if there are cards on the table
			System.exit(0);
		}
		else if(name.equals("Flip Coin"))
		{
			Random gen = new Random();
			if(gen.nextBoolean())
			{
				TextActionListener.send(Main.userName + " flips a coin and it comes up heads", "Action");
			}
			else
			{
				TextActionListener.send(Main.userName + " flips a coin and it comes up tails", "Action");
			}
		}
		else if(name.equals("Drop Random Fate Card"))
		{
			TextActionListener.send(Main.userName + " drops a random fate card.", "Action");
		}
		else if(name.equals("Reveal Hand"))
		{
			if(Main.gender.equals("Male"))
			{
				TextActionListener.send(Main.userName + " reveals his hand.", "Action");
			}
			else
			{
				TextActionListener.send(Main.userName + " reveals her hand.", "Action");
			}
		}
		else if(name.equals("About Dojo"))
		{
			//TODO: Create about panel
		}
	}
}