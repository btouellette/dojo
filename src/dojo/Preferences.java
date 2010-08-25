package dojo;
// Preferences.java
// Written by Brian Ouellette
// Reads in configuration prefences from a file and stores them

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

class Preferences
{
	// Width and height stored in the config file
	static int width, height;
	// Used to save width and height of components within the UI
	static int infoAreaHeight, cardBoxSplitWidth, gameInfoSplitWidth;
	// Height in pixels of the cards
	static int cardHeight;
	// Represents card size
	static int sliderValue;
	// The editions we've attempted to download
	static Set<String> downloadedEditions = new HashSet<String>();
	// Used for personalization
	static String userName, gender;

	public static void importPreferences()
	{
		// Go ahead and set the preferences to the default and we'll overwrite if we find saved prefs
		defaultPreferences();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			BufferedReader br = new BufferedReader(new FileReader("prefs.cfg"));
			String line;
			// Read in every line of the config file
			while((line = br.readLine()) != null)
			{
				if(!line.isEmpty() && line.charAt(0) != '#')
				{
					if(line.startsWith("width "))
					{
						int newWidth = Integer.parseInt(line.substring(6));
						if(newWidth <= screenSize.getWidth())
						{
							width = newWidth;
						}
					}
					else if(line.startsWith("height "))
					{
						int newHeight = Integer.parseInt(line.substring(7));
						if(newHeight <= screenSize.getHeight())
						{
							height = newHeight;
						}
					}
					else if(line.startsWith("infoAreaHeight "))
					{
						infoAreaHeight = Integer.parseInt(line.substring(15));
					}
					else if(line.startsWith("cardBoxSplitWidth "))
					{
						cardBoxSplitWidth = Integer.parseInt(line.substring(18));
					}
					else if(line.startsWith("gameInfoSplitWidth "))
					{
						gameInfoSplitWidth = Integer.parseInt(line.substring(19));
					}
					else if(line.startsWith("cardHeight "))
					{
						cardHeight = Integer.parseInt(line.substring(11));
					}
					else if(line.startsWith("sliderValue "))
					{
						sliderValue = Integer.parseInt(line.substring(12));
						// Reset sliderValue if it is outside allowed range
						if(sliderValue < 1 || sliderValue > 100) 
						{
							sliderValue = 50;
						}
					}
					else if(line.startsWith("downloaded "))
					{
						downloadedEditions.add(line.substring(11));
					}
					else if(line.startsWith("userName "))
					{
						userName = line.substring(9);
					}
					else if(line.startsWith("gender "))
					{
						gender = line.substring(7);
					}
					else
					{
						// Invalid line detected in config file
						throw new IOException();
					}
				}
			}
			br.close();
		} catch(IOException e) {
			System.err.println("** Couldn't find config file. Using defaults.");
			defaultPreferences();
		}
	}

	private static void defaultPreferences()
	{
		// Set up defaults if preferences file not available
		// Grab the screen resolution
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = 1024;
		height = 600;
		infoAreaHeight = 0;
		cardBoxSplitWidth = 0;
		gameInfoSplitWidth = 0;
		cardHeight = 0;
		sliderValue = 50;
		downloadedEditions.clear();
		userName = "New Player";
		gender = "his";
	}
	
	public static void writePreferences()
	{
		// Called on shutdown, save current values for use next startup
		try {
			FileWriter fw = new FileWriter("prefs.cfg");
			fw.write("# Dojo config file\n");
			fw.write("# Size to start program window (in pixels)\n");
			//TODO: Find out why these occasionally return 0 on a fresh checkout
			if(Main.playArea.getWidth() > 0)
			{
				// 16 extra pixels of border (at least on Linux)
				//TODO: Test whether this causes the saved value to shrink/grow over time in Win/Mac
				fw.write("width " + (Main.playArea.getWidth()-16) + "\n");
			}
			if(Main.playArea.getHeight() > 0)
			{
				fw.write("height " + Main.playArea.getHeight() + "\n");
			}
			fw.write("infoAreaHeight " + infoAreaHeight + "\n");
			fw.write("cardBoxSplitWidth " + cardBoxSplitWidth + "\n");
			fw.write("gameInfoSplitWidth " + gameInfoSplitWidth + "\n");
			fw.write("cardHeight " + Main.playArea.getCardHeight() + "\n");
			fw.write("# Size of cards at start (1-100, 50 is default)\n");
			fw.write("sliderValue " + sliderValue + "\n");
			fw.write("# Image editions gotten from (or not present on) kamisasori.net\n");
			for(String s : downloadedEditions)
			{
				fw.write("downloaded " + s + "\n");
			}
			fw.write("# User name to be used in chat box\n");
			fw.write("userName " + userName + "\n");
			fw.write("# Used for gender specific personalization (his or her)\n");
			fw.write("gender " + gender + "\n");
			fw.close();
		} catch(IOException e) {
			System.err.println("** Failed writing out config file");
			System.err.println(e);
		}
	}
}
