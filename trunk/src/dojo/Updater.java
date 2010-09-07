package dojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class Updater extends Thread
{
	public void run()
	{
		// Wait for the loading flag to be cleared
		while(Main.loading)
		{
			// Do nothing
		}
		Main.loading = true;
		try {
			URL url = new URL("http://dojo.googlecode.com/files/version");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = br.readLine();
			// Check if the version recorded online is greater than the one we have
			if(Double.parseDouble(line.substring(8)) > Main.version)
			{
				// If so we need to pull down the new versions of all the files and restart
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// No net connection, do nothing
		}
		Main.loading = false;
	}
}
