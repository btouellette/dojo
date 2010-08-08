package dojo;
// Preferences.java
// Written by Brian Ouellette
// Reads in configuration prefences from a file and stores them

class Preferences
{
	//TODO: public?
	public static int width, height;
	public static Set<String> downloadedEditions = new HashSet<String>();
	public static String userName, gender;

	public static importPreferences()
	{
		// Set up defaults if preferences file not available
		// Grab the screen resolution
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 1400;
		int height = 700;
		userName = "Tatsura";
		gender = "his";
	}
}
