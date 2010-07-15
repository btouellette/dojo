package dojo;
// MenuListener.java
// Written by Brian Ouellette
// Part of Dojo
// Listener for all the menus at the top of the panel.

import java.awt.event.*;
import java.io.*;

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
		//TODO: Set so it filters out .l5d and .dck files
		else if(name.equals("Load Deck"))
		{
			final JFileChooser fc = new JFileChooser("decks");
			int returnVal = fc.showOpenDialog(Main.frame);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            try {
					BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
					
					Deck dynasty = new Deck();
					Deck fate = new Deck();
					
					String line;
					while ((line = br.readLine()) != null)
					{
						if(!line.isEmpty() && line.charAt(0) != '#')
						{
							int count = 0;
							int num = 0;
							while(Character.isDigit(line.charAt(count)))
							{
								num *= 10;
								num += Character.getNumericValue(line.charAt(count));;
								count++;
							}
							String cardName = line.substring(count+1);
						}
					}
				} catch (FileNotFoundException err) {
					TextActionListener.send("Deck not found.\n", "Error");
					err.printStackTrace();
				} catch (IOException err) {
					TextActionListener.send("Failed to read in deck.\n", "Error");
					err.printStackTrace();
				}
	        }
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
			//TODO: If Dojo gets an icon add it here
			//TODO: Make this a legit JFrame that looks decent
			JOptionPane.showMessageDialog(Main.frame,
			                              "Dojo is an unofficial Java-based client for playing \n" +
			                              "the card game Legend of the Five Rings (L5R) online.\n\n" +
			                              "Written by Brian Ouellette (aka btouellette aka Hida Tatsura) \n" +
			                              "with help from James Spencer (aka blitz blitz blitz).\n" +
			                              "To contribute, file bug reports, request features, or discuss visit:\n" +
			                              "http://code.google.com/p/dojo\n\n" +
			                              "Legend of the Five Rings, L5R, and all images used herein are\n" +
			                              "Copyright © 2010 Alderac Entertainment Group",
			                              "About Dojo",
			                              JOptionPane.INFORMATION_MESSAGE);
		}
	}
}