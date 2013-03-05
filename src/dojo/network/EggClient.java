package dojo.network;

//Emulator for Egg client

import java.io.IOException;
import java.net.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dojo.TextActionListener;

/**
 * Hosting:
 * Accept succeeded.
 * Got:  ["protocol", {"version": 8}]
 * 
 * Connecting:
 * Connect succeeded.
 * Sent: ["protocol",{"version":8}]
 * Got:  ["welcome", {"clid": 1}]
 * Got:  ["client-names", {"names": [[0, "Toku-san"]]}]
 * Got:  ["client-join", {"clid": 1}]
 * 
 */
public class EggClient extends Thread
{
	// Protocol version. Keep incompatible versions from trying to play together
	private int protocolVersion = 8;
	private int clientID;
	private NetworkHandler handler;
	
	public EggClient(Socket s)
	{
		handler = new NetworkHandler(s);
	}

	public void run()
	{
		while (true) {
			try {
				String inputLine = handler.readLine();
				if (inputLine != null) {
					System.out.println("Got:  " + inputLine);
					try {
						JSONArray jarray = new JSONArray(inputLine);
						String command = jarray.getString(0);
						if (command.equals("rejected")) {
							handleRejected(jarray.getJSONObject(1));
						} else if (command.equals("welcome")) {
							handleWelcome(jarray.getJSONObject(1));
						} else if (command.equals("client-names")) {
							handleClientNames(jarray.getJSONObject(1));
						} else if (command.equals("client-join")) {
							handleClientJoin(jarray.getJSONObject(1));
						}
					} catch (JSONException e) {
						e.printStackTrace();
						System.err.println("** Failed to parse JSON command from server");
					}
				}
			} catch (IOException e) {
				System.err.println("** Couldn't get new line from server");
			}
		}
	}

	public void handshake()
	{
		try {
			sendProtocol();
		} catch (JSONException e) {
			e.printStackTrace();
			System.err.println("** Failed to parse JSON command from client");
		}
	}

	// Send our protocol version to the client
	private void sendProtocol() throws JSONException
	{
		String message = handler.encode("protocol", "version", protocolVersion);
		handler.send(message);
	}

	// Handle the "rejected" message from server if our protocol handshake wasn't established correctly
	private void handleRejected(JSONObject jobj) throws JSONException
	{
		final String message = jobj.getString("msg");
		// Report the error but do so on the event dispatch queue where it is safe to interact with Swing components
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				TextActionListener.send(message, "Error");
			}
		});
	}

	// Handle the "welcome" message from server on connect
	private void handleWelcome(JSONObject jobj) throws JSONException
	{
		// Update our clientID with the one reported back to us
		clientID = jobj.getInt("clid");

		// TODO: Send name to server
	}

	private void handleClientJoin(JSONObject jobj) throws JSONException
	{
		int clientID = jobj.getInt("clid");
		// Ignore the join if it is us joining
		if (this.clientID == clientID) {
			return;
		}
	}

	private void handleClientNames(JSONObject jobj)
	{
	}
}
