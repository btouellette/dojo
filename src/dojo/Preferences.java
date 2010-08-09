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
						//TODO: Change this to a single substring/str2num call
						int count = 6, num = 0;
						while(Character.isDigit(line.charAt(count)))
						{
							num *=10;
							num += Character.getNumericValue(line.charAt(count));
						}
						width = num;
					}
					else if(line.startsWith("height "))
					{
						int count = 7, num = 0;
						while(Character.isDigit(line.charAt(count)))
						{
							num *=10;
							num += Character.getNumericValue(line.charAt(count));
						}
						height = num;
					}
					else if(line.startsWith("sliderValue "))
					{
						int count = 12, num = 0;
						while(Character.isDigit(line.charAt(count)))
						{
							num *=10;
							num += Character.getNumericValue(line.charAt(count));
						}
						sliderValue = num;
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
				}
			}
		} catch(IOException e) {
			System.err.println("** Couldn't find config file. Using defaults.");
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
}
