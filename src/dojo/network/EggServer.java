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
				// Let the client know the assigned ID
				String message = handler.encode("welcome", "clid", clientID);
				send(message);
				
				// Send the currently connected client names and IDs
				int[] intValues = new int[clients.size() + 1];
				String[] stringValues = new String[clients.size() + 1];
				// Populate the host client ID and user name
				intValues[0] = EggServer.this.clientID;
				stringValues[0] = dojo.Preferences.userName;
				for (int i = 1; i <= clients.size(); i++)
				{
					intValues[i] = clients.get(i).clientID;
					stringValues[i] = clients.get(i).name;
				}
				message = handler.encode("client-names", "names", intValues, stringValues);
				send(message);
				
				// Let the other clients know about the join
				message = handler.encode("client-join", "clid", clientID);
				EggServer.this.broadcast(message);
			} else {
				// We've encountered a different protocol version
				// Report back failure to the client
				String message = handler.encode("rejected", "msg", "Your client protocol version is wrong (got " + jobj.getInt("version") + ", needs " + protocolVersion + ")");
				send(message);
			}
		}
	}
}
