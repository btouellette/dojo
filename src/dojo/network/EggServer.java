package dojo.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Hosting:
 * Accept succeeded.
 * Got:  ["protocol", {"version": 8}]
 * Sent: ["welcome",{"clid":1}]
 * Sent: ["client-names",{"names":[[0,"New Player"]]}]
 * Sent: ["client-join",{"clid":1}]
 * Got:  ["name", {"value": "Toku-san"}]
 * -- Join Game -> Submit Deck
 * Got:  ["submit-deck", {"cards": [[1, "Emperor393"], [1, "TSE005"], [1, "SC003"], [1, "P460"], [1, "Emperor005"], [1, "P440"], [1, "SC078"], [3, "Emperor038"], [2, "SoD009"], [3, "FL008"], [1, "Emperor054"], [1, "EEGempukku009"], [3, "EoW008"], [1, "FL007"], [1, "FL006"], [1, "EoW014"], [3, "TSE016"], [3, "FL010"], [1, "TSE018"], [3, "HaT012"], [2, "TSE015"], [1, "Emperor059"], [1, "Emperor060"], [3, "EoW013"], [3, "SoD014"], [3, "Emperor295"], [3, "TSE113"], [3, "TA110"], [1, "FL066"], [1, "FL059"], [1, "P475"], [3, "BtD122"], [3, "TSE125"], [3, "Emperor362"], [1, "Emperor364"], [1, "EoW147"], [3, "SC153"], [3, "SoD156"], [3, "FL063"], [1, "TSE138"], [3, "TA123"], [2, "P450"], [1, "P491"], [1, "Emperor245"]]}]
 * 
 * Connecting:
 * Connect succeeded.
 * Sent: ["protocol",{"version":8}]
 * Got:  ["welcome", {"clid": 1}]
 * Got:  ["client-names", {"names": [[0, "Toku-san"]]}]
 * Got:  ["client-join", {"clid": 1}]
 * Sent: ["name",{"value":"New Player"}]
 * Got:  ["name", {"clid": 1, "value": "New Player"}]
 * Submit Deck
 * Sent: ["submit-deck",{"cards":[[3,"Celestial311"],[3,"Celestial263"],[3,"GotE092"],[3,"Celestial246"],[2,"WoH032"],[3,"Emperor042"],[1,"IG2018"],[1,"IG2017"],[3,"Celestial074"],[1,"Celestial075"],[1,"Celestial070"],[1,"IG2044"],[3,"Emperor351"],[1,"Celestial071"],[3,"GotE017"],[3,"Celestial204"],[3,"GotE019"],[2,"GotE018"],[3,"Celestial216"],[3,"GotE086"],[3,"WoH008"],[3,"Celestial237"],[3,"DaK012"],[1,"Celestial009"],[3,"Emperor229"],[3,"IG2010"],[2,"P282"],[3,"Celestial299"],[1,"Emperor054"],[1,"Emperor016"],[1,"IG2070"],[3,"IG1004"],[1,"GotE021"],[1,"IG2008"],[1,"Celestial345"],[1,"Celestial381"],[1,"GotE004"],[3,"Emperor231"]]}]
 * Got:  ["deck-submitted", {"clid": 1}] 
 */
public class EggServer
{
	// Protocol version. Keep incompatible versions from trying to play together
	private int protocolVersion = 8;
	// Host client ID will always be 0
	private final int clientID = 0;
	private ArrayList<Client> clients = new ArrayList<Client>();
	private NetworkCore network;

	public EggServer(NetworkCore network)
	{
		this.network = network;
	}
	
	public boolean isClientConnected()
	{
		for(Client client : clients) {
			if(client.handler.isConnected) {
				return true;
			}
		}
		return false;
	}
	
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
		for (Client client : clients) {
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

		public void send(String message)
		{
			handler.send(message);
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
							if (command.equals("protocol")) {
								handleProtocol(jarray.getJSONObject(1));
							} else if (command.equals("name")) {
								handleName(jarray.getJSONObject(1));
							} else if (command.equals("submit-deck")) {
								handleSubmitDeck(jarray.getJSONObject(1));
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

		private void handleSubmitDeck(JSONObject jobj) throws JSONException
		{
			// Read the deck JSON into a list of cards to forward on to the game core
			Map<String, Integer> cardList = new HashMap<String, Integer>();
			JSONArray cards = jobj.getJSONArray("cards");
			for (int i = 0; i < cards.length(); i++) {
				JSONArray card = cards.getJSONArray(i);
				int num = card.getInt(0);
				String name = card.getString(1);
				cardList.put(name, num);
			}
			network.opponentSubmitDeck(clientID, cardList);
			// Broadcast to all clients that the user has submitted a deck
			String message = handler.encode("deck-submitted", "clid", clientID);
			broadcast(message);
			network.markDeckSubmitted(clientID);
		}

		private void handleName(JSONObject jobj) throws JSONException
		{
			// Now that we have an ID and name for the client and the handshake is done they are connected
			network.opponentConnect(clientID, jobj.getString("value"));
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
				int[] intValues = new int[clients.size()];
				String[] stringValues = new String[clients.size()];
				// Populate the host client ID and user name
				intValues[0] = EggServer.this.clientID;
				stringValues[0] = dojo.Main.state.name;
				int i = 1;
				for (Client curr : clients) {
					if (curr != this) {
						intValues[i + 1] = curr.clientID;
						stringValues[i + 1] = curr.name;
						i++;
					}
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
