package dojo.network;

//Emulator for Egg client

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import dojo.Preferences;
import dojo.TextActionListener;

/**
 * Hosting:
 * Accept succeeded.
 * Got:  ["protocol", {"version": 8}]
 * Sent: ["welcome",{"clid":1}]
 * Sent: ["client-names",{"names":[[0,"New Player"]]}]
 * Sent: ["client-join",{"clid":1}]
 * Got:  ["name", {"value": "Toku-san"}]
 * Got:  ["submit-deck", {"cards": [[1, "Emperor393"], [1, "TSE005"], [1, "SC003"], [1, "P460"], [1, "Emperor005"], [1, "P440"], [1, "SC078"], [3, "Emperor038"], [2, "SoD009"], [3, "FL008"], [1, "Emperor054"], [1, "EEGempukku009"], [3, "EoW008"], [1, "FL007"], [1, "FL006"], [1, "EoW014"], [3, "TSE016"], [3, "FL010"], [1, "TSE018"], [3, "HaT012"], [2, "TSE015"], [1, "Emperor059"], [1, "Emperor060"], [3, "EoW013"], [3, "SoD014"], [3, "Emperor295"], [3, "TSE113"], [3, "TA110"], [1, "FL066"], [1, "FL059"], [1, "P475"], [3, "BtD122"], [3, "TSE125"], [3, "Emperor362"], [1, "Emperor364"], [1, "EoW147"], [3, "SC153"], [3, "SoD156"], [3, "FL063"], [1, "TSE138"], [3, "TA123"], [2, "P450"], [1, "P491"], [1, "Emperor245"]]}]
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
	private Network network;

	public EggClient(Socket s, Network network)
	{
		handler = new NetworkHandler(s);
		this.network = network;
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
		String message = handler.encode("name", "value", Preferences.userName);
		handler.send(message);
	}

	private void handleClientJoin(JSONObject jobj) throws JSONException
	{
		int clientID = jobj.getInt("clid");
		// Ignore the join if it is us joining
		if (this.clientID == clientID) {
			return;
		}
	}

	private void handleClientNames(JSONObject jobj) throws JSONException
	{
		JSONArray names = jobj.getJSONArray("names");
		for (int i = 0; i < names.length(); i++)
		{
			JSONArray nameID = names.getJSONArray(i);
			int id = nameID.getInt(0);
			String name = nameID.getString(1);
			network.opponentConnect(id, name);
		}
	}
}
