package dojo.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class EggServer extends Thread
{
	// Protocol version. Keep incompatible versions from trying to play together
	private int protocolVersion = 8;
	private int clientID;
	private ArrayList<Client> clients = new ArrayList<Client>();

	private class Client
	{
		int clientID;
		BufferedReader in;
		BufferedWriter out;
	}

	public void clientConnect(Socket s)
	{
		try {
			Client client = new Client();
			client.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			client.out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			clients.add(client);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("** Failed to get in/out stream to client");
		}
	}

	public void run()
	{
		/*		while (true) {
					try {
						String inputLine = in.readLine();
						if (inputLine != null) {
							System.out.println("Got:  " + inputLine);
							try {
								JSONArray jarray = new JSONArray(inputLine);
								String command = jarray.getString(0);
								if (command.equals("protocol")) {
									handleProtocol(jarray.getJSONObject(1));
								}
							} catch (JSONException e) {
								e.printStackTrace();
								System.err.println("** Failed to parse JSON command from client");
							}
						}
					} catch (IOException e) {
						System.err.println("** Couldn't get new line from client");
					}
				}*/
	}

	// Handle the "protocol" message from client for exchanging protocol versions as a handshake
	private void handleProtocol(JSONObject jobj) throws JSONException
	{
		if (jobj.getInt("version") == protocolVersion) {
			// Handshake okay, continue on
			// send welcome clid clientID
			// send client-names names clientNames
			// send deck-submitted clid clientID
			// broadcast client-join clid clientID

			// Assign a unique ID to the client and let it know
			int clientId = 1;
			// String message = encode("welcome", "clid", clientId);

		} else {
			// We've encountered a different protocol version
			// Report back failure to the client
			// String message = encode("rejected", "msg", "Your client protocol version is wrong (got " + jobj.getInt("version") + ", needs " + protocolVersion + ")");
			// send(message);
		}
	}
}
