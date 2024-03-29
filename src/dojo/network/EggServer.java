package dojo.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dojo.Card;

/**
 * Hosting:
 * Got:  ["protocol", {"version": 8}]
 * Sent: ["welcome",{"clid":1}]
 * Sent: ["client-names",{"names":[[0,"New Player"]]}]
 * Sent: ["client-join",{"clid":1}]
 * Got:  ["name", {"value": "Toku-san"}]
 * Got:  ["submit-deck", {"cards": [[1, "Emperor393"], [1, "TSE005"], [1, "SC003"], [1, "P460"], [1, "Emperor005"], [1, "P440"], [1, "SC078"], [3, "Emperor038"], [2, "SoD009"], [3, "FL008"], [1, "Emperor054"], [1, "EEGempukku009"], [3, "EoW008"], [1, "FL007"], [1, "FL006"], [1, "EoW014"], [3, "TSE016"], [3, "FL010"], [1, "TSE018"], [3, "HaT012"], [2, "TSE015"], [1, "Emperor059"], [1, "Emperor060"], [3, "EoW013"], [3, "SoD014"], [3, "Emperor295"], [3, "TSE113"], [3, "TA110"], [1, "FL066"], [1, "FL059"], [1, "P475"], [3, "BtD122"], [3, "TSE125"], [3, "Emperor362"], [1, "Emperor364"], [1, "EoW147"], [3, "SC153"], [3, "SoD156"], [3, "FL063"], [1, "TSE138"], [3, "TA123"], [2, "P450"], [1, "P491"], [1, "Emperor245"]]}]
 * 
 * Connecting:
 * Sent: ["protocol",{"version":8}]
 * Got:  ["welcome", {"clid": 1}]
 * Got:  ["client-names", {"names": [[0, "Toku-san"]]}]
 * Got:  ["client-join", {"clid": 1}]
 * Sent: ["name",{"value":"New Player"}]
 * Got:  ["name", {"clid": 1, "value": "New Player"}]
 * Sent: ["submit-deck",{"cards":[[3,"Celestial311"],[3,"Celestial263"],[3,"GotE092"],[3,"Celestial246"],[2,"WoH032"],[3,"Emperor042"],[1,"IG2018"],[1,"IG2017"],[3,"Celestial074"],[1,"Celestial075"],[1,"Celestial070"],[1,"IG2044"],[3,"Emperor351"],[1,"Celestial071"],[3,"GotE017"],[3,"Celestial204"],[3,"GotE019"],[2,"GotE018"],[3,"Celestial216"],[3,"GotE086"],[3,"WoH008"],[3,"Celestial237"],[3,"DaK012"],[1,"Celestial009"],[3,"Emperor229"],[3,"IG2010"],[2,"P282"],[3,"Celestial299"],[1,"Emperor054"],[1,"Emperor016"],[1,"IG2070"],[3,"IG1004"],[1,"GotE021"],[1,"IG2008"],[1,"Celestial345"],[1,"Celestial381"],[1,"GotE004"],[3,"Emperor231"]]}]
 * Got:  ["deck-submitted", {"clid": 1}]
 * Got:  ["deck-submitted", {"clid": 0}]
 * Got:  ["game-setup", {}]
 * Got:  ["player-join", {"clid": 0, "pid": 1}]
 * Got:  ["set-zone", {"cgids": [1, 2], "zid": 0, "pid": 1}]
 * Got:  ["set-zone", {"cgids": [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42], "zid": 1, "pid": 1}]
 * Got:  ["set-zone", {"cgids": [43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83], "zid": 2, "pid": 1}]
 * Got:  ["player-join", {"clid": 1, "pid": 2}]
 * Got:  ["set-zone", {"cgids": [84, 85], "zid": 0, "pid": 2}]
 * Got:  ["set-zone", {"cgids": [86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125], "zid": 1, "pid": 2}]
 * Got:  ["set-zone", {"cgids": [126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166], "zid": 2, "pid": 2}]
 * Got:  ["game-start", {}]
 * Got:  ["move-card", {"cgid": 42, "faceup": false, "random": false, "pid": 1, "zid": 7, "y": 0, "x": 0.0, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 41, "faceup": false, "random": false, "pid": 1, "zid": 7, "y": 0, "x": 26.0, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 40, "faceup": false, "random": false, "pid": 1, "zid": 7, "y": 0, "x": 52.0, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 39, "faceup": false, "random": false, "pid": 1, "zid": 7, "y": 0, "x": 78.0, "mover": 1, "top": null}]
 * Got:  ["reveal-card", {"cgid": 43, "cdid": "Celestial381"}]
 * Got:  ["move-card", {"cgid": 43, "faceup": true, "random": false, "pid": 1, "zid": 7, "y": 0, "x": -76.0, "mover": 1, "top": null}]
 * Got:  ["set-family-honor", {"pid": 1, "honor": 6}]
 * Got:  ["reveal-card", {"cgid": 1, "cdid": "Emperor028"}]
 * Got:  ["move-card", {"cgid": 1, "faceup": true, "random": false, "pid": 1, "zid": 7, "y": 0, "x": -102.0, "mover": 1, "top": null}]
 * Got:  ["reveal-card", {"cgid": 2, "cdid": "HaT001"}]
 * Got:  ["move-card", {"cgid": 2, "faceup": true, "random": false, "pid": 1, "zid": 7, "y": 0, "x": -128.0, "mover": 1, "top": null}]
 * Got:  ["set-card-property", {"cgid": 2, "property": "tapped", "pid": 1, "value": true}]
 * Got:  ["move-card", {"cgid": 83, "faceup": true, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 82, "faceup": true, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 81, "faceup": true, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 80, "faceup": true, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 79, "faceup": true, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 78, "faceup": true, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": null}]
 * Got:  ["move-card", {"cgid": 125, "faceup": false, "random": false, "pid": 2, "zid": 7, "y": 0, "x": 0.0, "mover": 2, "top": null}]
 * Got:  ["move-card", {"cgid": 124, "faceup": false, "random": false, "pid": 2, "zid": 7, "y": 0, "x": 26.0, "mover": 2, "top": null}]
 * Got:  ["move-card", {"cgid": 123, "faceup": false, "random": false, "pid": 2, "zid": 7, "y": 0, "x": 52.0, "mover": 2, "top": null}]
 * Got:  ["move-card", {"cgid": 122, "faceup": false, "random": false, "pid": 2, "zid": 7, "y": 0, "x": 78.0, "mover": 2, "top": null}]
 * Got:  ["reveal-card", {"cgid": 134, "cdid": "Celestial381"}]
 * Got:  ["move-card", {"cgid": 134, "faceup": true, "random": false, "pid": 2, "zid": 7, "y": 0, "x": -76.0, "mover": 2, "top": null}]
 * Got:  ["set-family-honor", {"pid": 2, "honor": 6}]
 * Got:  ["reveal-card", {"cgid": 84, "cdid": "Emperor028"}]
 * Got:  ["move-card", {"cgid": 84, "faceup": true, "random": false, "pid": 2, "zid": 7, "y": 0, "x": -102.0, "mover": 2, "top": null}]
 * Got:  ["reveal-card", {"cgid": 85, "cdid": "HaT001"}]
 * Got:  ["move-card", {"cgid": 85, "faceup": true, "random": false, "pid": 2, "zid": 7, "y": 0, "x": -128.0, "mover": 2, "top": null}]
 * Got:  ["set-card-property", {"cgid": 85, "property": "tapped", "pid": 2, "value": true}]
 * Got:  ["reveal-card", {"cgid": 166, "cdid": "Emperor229"}]
 * Got:  ["move-card", {"cgid": 166, "faceup": true, "random": false, "pid": 2, "zid": 5, "mover": 2, "top": null}]
 * Got:  ["reveal-card", {"cgid": 165, "cdid": "Emperor351"}]
 * Got:  ["move-card", {"cgid": 165, "faceup": true, "random": false, "pid": 2, "zid": 5, "mover": 2, "top": null}]
 * Got:  ["reveal-card", {"cgid": 164, "cdid": "Celestial299"}]
 * Got:  ["move-card", {"cgid": 164, "faceup": true, "random": false, "pid": 2, "zid": 5, "mover": 2, "top": null}]
 * Got:  ["reveal-card", {"cgid": 163, "cdid": "GotE092"}]
 * Got:  ["move-card", {"cgid": 163, "faceup": true, "random": false, "pid": 2, "zid": 5, "mover": 2, "top": null}]
 * Got:  ["reveal-card", {"cgid": 162, "cdid": "Celestial237"}]
 * Got:  ["move-card", {"cgid": 162, "faceup": true, "random": false, "pid": 2, "zid": 5, "mover": 2, "top": null}]
 * Got:  ["reveal-card", {"cgid": 161, "cdid": "Celestial345"}]
 * Got:  ["move-card", {"cgid": 161, "faceup": true, "random": false, "pid": 2, "zid": 5, "mover": 2, "top": null}]
 * Got:  ["reveal-card", {"cgid": 42, "cdid": "IG2017"}]
 * Got:  ["set-card-property", {"cgid": 42, "property": "faceUp", "pid": 1, "value": true}]
 * Got:  ["reveal-card", {"cgid": 43, "cdid": "Celestial381"}]
 * Got:  ["move-card", {"cgid": 43, "faceup": null, "random": false, "pid": 1, "zid": 7, "y": 0.0, "x": -75.0, "mover": 1, "top": true}]
 * Got:  ["set-card-property", {"cgid": 43, "property": "tapped", "pid": 1, "value": true}]
 * Got:  ["reveal-card", {"cgid": 42, "cdid": "IG2017"}]
 * Got:  ["move-card", {"cgid": 42, "faceup": null, "random": false, "pid": 1, "zid": 7, "y": -65.0, "x": -5.0, "mover": 1, "top": true}]
 * Got:  ["move-card", {"cgid": 38, "faceup": false, "random": false, "pid": 1, "zid": 1, "mover": 1, "top": true}]
 * Got:  ["move-card", {"cgid": 38, "faceup": false, "random": false, "pid": 1, "zid": 7, "y": 0.0, "x": 0.0, "mover": 1, "top": true}]
 * Got:  ["reveal-card", {"cgid": 38, "cdid": "IG2044"}]
 * Got:  ["move-card", {"cgid": 38, "faceup": null, "random": false, "pid": 1, "zid": 3, "mover": 1, "top": true}]
 * Got:  ["reveal-card", {"cgid": 41, "cdid": "Emperor042"}]
 * Got:  ["move-card", {"cgid": 41, "faceup": null, "random": false, "pid": 1, "zid": 6, "mover": 1, "top": true}]
 * 
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
			Map<Card, Integer> cardList = new HashMap<Card, Integer>();
			JSONArray cards = jobj.getJSONArray("cards");
			for (int i = 0; i < cards.length(); i++) {
				JSONArray card = cards.getJSONArray(i);
				int num = card.getInt(0);
				String xmlID = card.getString(1);
				cardList.put(new Card(xmlID), num);
			}
			network.opponentSubmitDeck(clientID, cardList);
			// Broadcast to all clients that the user has submitted a deck
			String message = NetworkHandler.encode("deck-submitted", "clid", clientID);
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
				String message = NetworkHandler.encode("welcome", "clid", clientID);
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
				message = NetworkHandler.encode("client-names", "names", intValues, stringValues);
				send(message);

				// Let the other clients know about the join
				message = NetworkHandler.encode("client-join", "clid", clientID);
				EggServer.this.broadcast(message);
			} else {
				// We've encountered a different protocol version
				// Report back failure to the client
				String message = NetworkHandler.encode("rejected", "msg", "Your client protocol version is wrong (got " + jobj.getInt("version") + ", needs " + protocolVersion + ")");
				send(message);
			}
		}
	}

	public void submitDeck() throws JSONException {
		String message = NetworkHandler.encode("deck-submitted", "clid", clientID);
		broadcast(message);
	}
}
