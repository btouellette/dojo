package dojo.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dojo.Card.Location;
import dojo.StoredCard;
import dojo.TextActionListener;

/**
 * Hosting:
 * Got:  ["protocol", {"version": 8}]
 * Sent: ["welcome",{"clid":1}]
 * Sent: ["client-names",{"names":[[0,"New Player"]]}]
 * Sent: ["client-join",{"clid":1}]
 * Got:  ["name", {"value": "Toku-san"}]
 * Got:  ["submit-deck", {"cards": [[1, "Emperor393"], [1, "TSE005"], [1, "SC003"], [1, "P460"], [1, "Emperor005"], [1, "P440"], [1, "SC078"], [3, "Emperor038"], [2, "SoD009"], [3, "FL008"], [1, "Emperor054"], [1, "EEGempukku009"], [3, "EoW008"], [1, "FL007"], [1, "FL006"], [1, "EoW014"], [3, "TSE016"], [3, "FL010"], [1, "TSE018"], [3, "HaT012"], [2, "TSE015"], [1, "Emperor059"], [1, "Emperor060"], [3, "EoW013"], [3, "SoD014"], [3, "Emperor295"], [3, "TSE113"], [3, "TA110"], [1, "FL066"], [1, "FL059"], [1, "P475"], [3, "BtD122"], [3, "TSE125"], [3, "Emperor362"], [1, "Emperor364"], [1, "EoW147"], [3, "SC153"], [3, "SoD156"], [3, "FL063"], [1, "TSE138"], [3, "TA123"], [2, "P450"], [1, "P491"], [1, "Emperor245"]]}]
 * 
 -* game-setup  		 -- don't do anything
 -* player-join 		 -- mark player as active and not spectating, associates clid and pid
 -* set-zone    		 -- card creation? cgids == card ids, pid == playerid (associated in player-join event), zid (0: table, 1/2: dyn/fate deck)
 -*     stronghold, border keep + bamboo harvesters, 4 provs, 6 cards in hand, 2 decks
 -* game-start  	 	 -- don't do anything
 * move-card   		 -- cgid, faceup: false/true, random: false/true (if discarded card from hand is random), pid, zid, x/y/, mover: pid, top: true (to or from top of deck), false (to deck bottom, to hand???), null (table to table move)
 * reveal-card 		 -- cgid, cdid (cardID)
 -* set-family-honor  -- pid, honor
 -* set-card-property -- cgid, property (tapped, faceUp, dishonored), pid, value
 * peek-card		 -- cgid pid, report to chat only
 * peek-opponent	 -- cgid pid, when showing card send associated reveal-card
 * view-zone		 -- viewer (???), zid, pid, number (of cards)
 * show-zone		 -- zid, pid (entire zone is revealed)
 * show-zone-random	 -- cgids, zid, pid
 * zone-shuffled 	 -- zid, pid
 * set-markers		 -- cgid, token (+1F or w/e), pid, number (amount of tokens), image (URL: images/markers/marker_p1f.png)
 * set-favor		 -- zid (-1 for discard)
 -* flip-coin		 -- pid, result (true/false)
 -* roll-die		 	 -- pid, result, size
 * deck-unsubmitted  -- pid (leaving game)
 * new-card			 -- personal_honor, force, name, chi, cost, text, honor_req, type, id (first personality was _1 for this value???)
 * create-card		 -- cgid, cdid (_1???), pid, zid
 * 
 * ZoneIDs:
 * 0 - dummy (Border Keep and Bamboo Harvesters are in this zone)
 * 1 - dynasty deck
 * 2 - fate deck
 * 3 - dynasty discard
 * 4 - fate discard
 * 5 - hand
 * 6 - removed from game
 * 7 - table
 * 8 - focus pool (obsolete)
 * 
 * flip face down
 * Got:  ["set-card-property", {"cgid": 40, "property": "faceUp", "pid": 1, "value": false}]
 * 
 * peek at card
 * Got:  ["peek-card", {"cgid": 1, "pid": 1}]
 * 
 * show card to opponent
 * Got:  ["reveal-card", {"cgid": 1, "cdid": "Emperor028"}]
 * Got:  ["peek-opponent", {"cgid": 1, "pid": 1}]
 * 
 * look at top two cards of dynasty deck
 * Got:  ["view-zone", {"viewer": 1, "zid": 1, "pid": 1, "number": 2}]
 * 
 * draw three fate cards
 * Got:  ["move-card", {"cgid": 76, "faceup": null, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": true}]
 * Got:  ["move-card", {"cgid": 75, "faceup": null, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": true}]
 * Got:  ["move-card", {"cgid": 73, "faceup": null, "random": false, "pid": 1, "zid": 5, "mover": 1, "top": true}]
 * 
 * reveal hand
 * Got:  ["reveal-card", {"cgid": 81, "cdid": "Celestial237"}]
 * Got:  ["reveal-card", {"cgid": 82, "cdid": "Celestial216"}]
 * Got:  ["reveal-card", {"cgid": 76, "cdid": "Celestial345"}]
 * Got:  ["reveal-card", {"cgid": 75, "cdid": "Celestial237"}]
 * Got:  ["reveal-card", {"cgid": 73, "cdid": "Celestial311"}]
 * Got:  ["show-zone", {"zid": 5, "pid": 1}]
 * 
 * reveal random card from hand
 * Got:  ["reveal-card", {"cgid": 81, "cdid": "Celestial237"}]
 * Got:  ["show-zone-random", {"cgids": [81], "zid": 5, "pid": 1}]
 * 
 * discard random card from hand
 * Got:  ["reveal-card", {"cgid": 81, "cdid": "Celestial237"}]
 * Got:  ["move-card", {"cgid": 81, "faceup": true, "random": true, "pid": 1, "zid": 4, "mover": 1, "top": null}]
 * 
 * dishonor
 * Got:  ["set-card-property", {"cgid": 2, "property": "dishonored", "pid": 1, "value": true}]
 * 
 * discard dynasty
 * Got:  ["reveal-card", {"cgid": 41, "cdid": "GotE018"}]
 * Got:  ["move-card", {"cgid": 41, "faceup": null, "random": false, "pid": 1, "zid": 3, "mover": 1, "top": true}]
 * 
 * discard fate
 * Got:  ["reveal-card", {"cgid": 78, "cdid": "Celestial299"}]
 * Got:  ["move-card", {"cgid": 78, "faceup": null, "random": false, "pid": 1, "zid": 4, "mover": 1, "top": true}]
 * 
 * give control
 * Got:  ["reveal-card", {"cgid": 42, "cdid": "GotE018"}]
 * Got:  ["move-card", {"cgid": 42, "faceup": null, "random": false, "pid": 2, "zid": 7, "y": 0, "x": 0.0, "mover": 1, "top": null}]
 * 
 * no communication of attached state, just sends both card movements at once
 * 
 * remove from game 
 * Got:  ["reveal-card", {"cgid": 83, "cdid": "GotE086"}]
 * Got:  ["move-card", {"cgid": 83, "faceup": null, "random": false, "pid": 1, "zid": 6, "mover": 1, "top": true}]
 * 
 * move to dyn deck top
 * Got:  ["move-card", {"cgid": 37, "faceup": false, "random": false, "pid": 1, "zid": 1, "mover": 1, "top": true}]
 * 
 * move to fate deck top
 * Got:  ["move-card", {"cgid": 80, "faceup": false, "random": false, "pid": 1, "zid": 2, "mover": 1, "top": true}]
 * 
 * move to dyn deck bottom
 * Got:  ["move-card", {"cgid": 39, "faceup": false, "random": false, "pid": 1, "zid": 1, "mover": 1, "top": false}]
 * 
 * add 1F marker
 * Got:  ["set-markers", {"cgid": 80, "token": "+1F", "pid": 1, "number": 1, "image": "images/markers/marker_p1f.png"}]
 * 
 * remove 1F marker
 * Got:  ["set-markers", {"cgid": 80, "token": "+1F", "pid": 1, "number": 0, "image": "images/markers/marker_p1f.png"}]
 * 
 * add 2 -2C markers
 * Got:  ["set-markers", {"cgid": 80, "token": "-2C", "pid": 1, "number": 2, "image": "images/markers/marker_m2c.png"}]
 * 
 * shuffle fate
 * Got:  ["zone-shuffled", {"zid": 2, "pid": 1}]
 * 
 * take the favor then discard it
 * Got:  ["set-favor", {"pid": 1}]
 * Got:  ["set-favor", {"pid": -1}]
 * 
 * flip a coin and get tails
 * Got:  ["flip-coin", {"pid": 1, "result": false}]
 * 
 * roll a d20 and get 1
 * Got:  ["roll-die", {"pid": 1, "result": 1, "size": 20}]
 * 
 * create personality (which goes into my hand)
 * Got:  ["new-card", {"personal_honor": "5", "force": "1", "name": "TestDude", "chi": "2", "cost": "4", "text": "Test text", "honor_req": "3", "type": "personality", "id": "_1"}]
 * Got:  ["create-card", {"cgid": 167, "cdid": "_1", "pid": 1, "zid": 5}]
 * 
 * leave game
 * Got:  ["deck-unsubmitted", {"clid": 0}]
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
public class EggClient extends Thread
{
	// Protocol version. Keep incompatible versions from trying to play together
	private int protocolVersion = 8;
	private int clientID;
	private NetworkHandler handler;
	private NetworkCore network;

	public EggClient(Socket s, NetworkCore network)
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
						} else if (command.equals("deck-submitted")) {
							handleDeckSubmitted(jarray.getJSONObject(1));
						} else if (command.equals("game-setup")) {
							// Do nothing
						} else if (command.equals("player-join")) {
							handlePlayerJoin(jarray.getJSONObject(1));
						} else if (command.equals("set-zone")) {
							handleSetZone(jarray.getJSONObject(1));
						} else if (command.equals("game-start")) {
							// Do nothing
						} else if (command.equals("set-family-honor")) {
							handleSetHonor(jarray.getJSONObject(1));
						} else if (command.equals("set-card-property")) {
							handleSetCardProperty(jarray.getJSONObject(1));
						} else if (command.equals("flip-coin")) {
							handleFlipCoin(jarray.getJSONObject(1));
						} else if (command.equals("roll-die")) {
							handleDieRoll(jarray.getJSONObject(1));
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

	private void handleDieRoll(JSONObject jsonObject) throws JSONException {
		int playerID = jsonObject.getInt("pid");
		int result = jsonObject.getInt("result");
		int size = jsonObject.getInt("size");
		network.dieRolled(result, size, playerID);
	}

	private void handleFlipCoin(JSONObject jsonObject) throws JSONException {
		int playerID = jsonObject.getInt("pid");
		boolean value = jsonObject.getBoolean("result");
		network.coinFlipped(value, playerID);
	}

	private void handleSetCardProperty(JSONObject jsonObject) throws JSONException {
		int cardID = jsonObject.getInt("cgid");
		String property = jsonObject.getString("property");
		int playerID = jsonObject.getInt("pid");
		boolean value = jsonObject.getBoolean("value");
		network.setCardProperty(cardID, property, value, playerID);
	}

	private void handleSetHonor(JSONObject jsonObject) throws JSONException {
		int playerID = jsonObject.getInt("pid");
		int honor = jsonObject.getInt("honor");
		network.setHonor(honor, playerID);
	}

	public boolean isConnected()
	{
		return handler.isConnected;
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
		String message = NetworkHandler.encode("protocol", "version", protocolVersion);
		handler.send(message);
	}

	// Handle the "rejected" message from server if our protocol handshake wasn't established correctly
	private void handleRejected(JSONObject jobj) throws JSONException
	{
		final String message = jobj.getString("msg");
		// Report the error but do so on the event dispatch queue where it is safe to interact with Swing components
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TextActionListener.send(message, "Error");
			}
		});
	}

	// Handle the "welcome" message from server on connect
	private void handleWelcome(JSONObject jobj) throws JSONException
	{
		// Update our clientID with the one reported back to us
		clientID = jobj.getInt("clid");
	}

	private void handleClientJoin(JSONObject jobj) throws JSONException
	{
		int clientID = jobj.getInt("clid");
		// If it is us joining, report name to the server
		if (this.clientID == clientID) {
			String message = NetworkHandler.encode("name", "value", dojo.Main.state.name);
			handler.send(message);
		}
	}

	private void handleClientNames(JSONObject jobj) throws JSONException
	{
		JSONArray names = jobj.getJSONArray("names");
		for (int i = 0; i < names.length(); i++) {
			JSONArray nameID = names.getJSONArray(i);
			int id = nameID.getInt(0);
			String name = nameID.getString(1);
			network.opponentConnect(id, name);
		}
	}

	private void handleDeckSubmitted(JSONObject jobj) throws JSONException
	{
		int clientID = jobj.getInt("clid");
		network.markDeckSubmitted(clientID);
	}

	private void handlePlayerJoin(JSONObject jobj) throws JSONException
	{
		int clientID = jobj.getInt("clid");
		int playerID = jobj.getInt("pid");
		// Record the player ID
		if(this.clientID == clientID) {
			network.playerIDAssigned(playerID);
		} else {
			network.playerJoined(clientID, playerID);
		}
	}

	private void handleSetZone(JSONObject jsonObject) throws JSONException {
		JSONArray cgids = jsonObject.getJSONArray("cgids");
		int zid = jsonObject.getInt("zid");
		Location zone = network.zidToLocation(zid);
		int playerID = jsonObject.getInt("pid");
		int[] cardIDs = new int[cgids.length()];
		for(int i = 0; i < cgids.length(); i++) {
			cardIDs[i] = cgids.getInt(i);
		}
		network.setZone(zone, cardIDs, playerID);
	}
	
	public void submitDeck(List<StoredCard> deck) throws JSONException
	{
		// Coalesce the deck into a list of card IDs with their associated counts
		Map<String, Integer> cardList = new HashMap<String, Integer>();
		Collections.sort(deck);
		for (int i = 0; i < deck.size(); i++) {
			String cardID = deck.get(i).getID();
			if(cardList.containsKey(cardID)) {
				cardList.put(cardID, cardList.get(cardID) + 1);
			} else {
				cardList.put(cardID, 1);
			}
		}
		String[] cardIDs = new String[cardList.size()];
		int[] cardCounts = new int[cardList.size()];
		int i = 0;
		for (Map.Entry<String, Integer> entry : cardList.entrySet()) {
			cardIDs[i] = entry.getKey();
			cardCounts[i] = entry.getValue();
			i++;
		}
		String message = NetworkHandler.encode("submit-deck", "cards", cardCounts, cardIDs);
		handler.send(message);
	}
}
