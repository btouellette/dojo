package dojo;
// MenuListener.java
// Written by Brian Ouellette
// Listener for all the menus at the top of the panel.

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

class MenuListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		String name = ((AbstractButton)e.getSource()).getText();
		// Don't allow interaction if we're still loading
		if(Main.loading == false)
		{
			if(name.equals("Connect"))
			{
				String str = JOptionPane.showInputDialog("IP or Hostname:");
				if(str != null)
				{
					Main.network.connectEgg(str);
				}
			}
			else if(name.equals("Load Deck"))
			{
				// Launch file chooser to let user pick deck file
				JFileChooser fc = new JFileChooser("decks");
				// Add filter so it only shows appropriate files and directories
				fc.addChoosableFileFilter(new FileFilter() {
					public boolean accept(File file) {
						String filename = file.getName();
						return file.isDirectory() || filename.endsWith(".l5d") || filename.endsWith(".dck");
					}
					public String getDescription() {
						return "Deck Files (*.dck and *.l5d)";
					}
				});
				fc.setAcceptAllFileFilterUsed(false);
				int returnVal = fc.showOpenDialog(Main.frame);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					// And import the deck if they picked one
					DeckImporter.importDeck(fc.getSelectedFile());
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
				new Deckbuilder(Main.frame.getWidth(),Main.frame.getHeight());
			}
			else if(name.equals("Preferences"))
			{
			}
			else if(name.equals("Exit"))
			{
				//TODO: Add in a prompt if there are cards on the table
				System.exit(0);
			}
			else if(name.equals("Flip Coin"))
			{
				// Generate a random boolean for use as a coin flip and send it to the chat box
				Random gen = new Random();
				if(gen.nextBoolean())
				{
					TextActionListener.send(Preferences.userName + " flips a coin and it comes up heads.", "Action");
				}
				else
				{
					TextActionListener.send(Preferences.userName + " flips a coin and it comes up tails.", "Action");
				}
			}
			else if(name.equals("Drop Random Fate Card"))
			{
				//TODO: Move random card from hand to table
				TextActionListener.send(Preferences.userName + " drops a random fate card.", "Action");
			}
			else if(name.equals("Reveal Hand"))
			{
				//TODO: Show cards in hand to opponent
				TextActionListener.send(Preferences.userName + " reveals " + Preferences.gender + " hand.", "Action");
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
		else
		{
			TextActionListener.send("Please wait until loading is done.", "Error");
		}
	}
}
