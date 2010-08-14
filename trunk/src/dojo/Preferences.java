package dojo;
// Preferences.java
// Written by Brian Ouellette
// Reads in configuration prefences from a file and stores them

import java.io.*;
import java.util.HashSet;
import java.util.Set;

//TODO: Write out pref file (either on change or on destructor)
class Preferences
{
	// Width and height of the play area
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
	}
}
