package dojo;
// Preferences.java
// Written by Brian Ouellette
// Reads in configuration prefences from a file and stores them

import java.io.*;
import java.util.HashSet;
import java.util.Set;

class Preferences
{
	// Width and height stored in the config file
	static int width, height;
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
						width = Integer.parseInt(line.substring(6));
					}
					else if(line.startsWith("height "))
					{
						height = Integer.parseInt(line.substring(7));
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
				fw.write("width " + Main.playArea.getWidth() + "\n");
			}
			if(Main.playArea.getWidth() > 0)
			{
				fw.write("height " + Main.playArea.getHeight() + "\n");
			}
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
