package dojo.network;

import java.io.IOException;
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
public class EggServer
{
	// Protocol version. Keep incompatible versions from trying to play together
	private int protocolVersion = 8;
	// Host client ID will always be 0
	private final int clientID = 0;
	private ArrayList<Client> clients = new ArrayList<Client>();

	public void clientConnect(Socket s)
	{
		// For each client connection set up input and output streams for communication
		Client client = new Client(s);
		// Assign a unique client ID for this client
		client.clientID = clients.size() + 1;
		clients.add(client);
		client.start();
	}
	
	public void broadcast(String message)
	{
		for (Client client : clients)
		{
			client.send(message);
		}
	}
	
	private class Client extends Thread
	{
		int clientID;
		String name;
		NetworkHandler handler;
		
		public Client(Socket s)
		{
			handler = new NetworkHandler(s);
		}
		
		public void send(String message) {
			handler.send(message);
		}

		public void run()
		{
			while (true) {
				try {
					String inputLine = handler.readLine();
					if (inputLine != null) {
						System.out.println("Client " + clientID + " got:  " + inputLine);
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
			}
		}

		// Handle the "protocol" message from client for exchanging protocol versions as a handshake
		private void handleProtocol(JSONObject jobj) throws JSONException
		{
			if (jobj.getInt("version") == protocolVersion) {
				// Handshake okay, continue on
				// send welcome clid clientID
				// send ["client-names", {"names": [[0, "Toku-san"]]}]
				// send deck-submitted clid clientID
				// broadcast client-join clid clientID

				// Assign a unique ID to the client and let it know
				String message = handler.encode("welcome", "clid", clientID);
				send(message);
				//message = handler.encode("client-names", key, value)
				
			} else {
				// We've encountered a different protocol version
				// Report back failure to the client
				// String message = encode("rejected", "msg", "Your client protocol version is wrong (got " + jobj.getInt("version") + ", needs " + protocolVersion + ")");
				// send(message);
			}
		}
	}
}
