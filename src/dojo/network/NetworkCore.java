package dojo.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import dojo.Card;
import dojo.Card.GameArea;
import dojo.StoredCard;
import dojo.Main;

public class NetworkCore extends Thread
{
	// Port the game communications use
	private int gamePort = 18073;
	private EggClient client;
	private EggServer server;
	private volatile ServerSocket serverSocket;
	private volatile boolean connectFlag = false;

	public void run()
	{
		try {
			serverSocket = new ServerSocket(gamePort);
			// Once we're listening on the game port start a new server
			server = new EggServer(this);
			while (true) {
				// If we're not trying to connect to another host
				if(!connectFlag) {
					try {
						// Block till we see a new connection incoming
						Socket clientSocket = serverSocket.accept();
						if(client == null || !client.isConnected()) {
							// Accept it and pass it into the server code
							server.clientConnect(clientSocket);
							System.out.println("Accept succeeded.");
						}
					} catch (IOException e) {
						if(!connectFlag) {
							System.err.println("Accept failed.");
						}
					}
				}
			}
		} catch (IOException e) {
			System.err.println("** Could not listen on port: " + gamePort + ".");
		} finally {
			if (serverSocket != null && !serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.err.println("** Failed to close ServerSocket.");
				}
			}
		}
	}

	public boolean connectEgg(String url)
	{
		boolean success = true;
		try {
			if(server.isClientConnected())
			{
				System.err.println("** Already hosting a game.");
				return false;
			}
			// Shut down the hosting listener
			// TODO: Turn this flag off when client connection is closed
			connectFlag = true;
			if (serverSocket != null && !serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.err.println("** Failed to close ServerSocket.");
				}
			}
			// Parse the URL given and connect to the host
			Socket hostSocket;
			// Check for if port syntax was given
			if (!url.contains(":")) {
				hostSocket = new Socket(url, gamePort);
			} else {
				String[] splitServer = url.split(":");
				String hostname = splitServer[0];
				String port = splitServer[1];
				hostSocket = new Socket(hostname, Integer.parseInt(port));
			}
			// Start up our client code and handshake with the host
			client = new EggClient(hostSocket, this);
			client.start();
			client.handshake();
			System.out.println("Connect succeeded.");
		} catch (UnknownHostException e) {
			System.err.println("** Could not find server " + url);
			success = false;
		} catch (IOException e) {
			System.err.println("** Could not connect to server " + url);
			success = false;
		}
		return success;
	}
	
	public void setZone(GameArea zone, int[] cardIDs, int playerID) {
		Main.state.setGameAreaContents(zone, cardIDs, playerID);		
	}
	
	public void opponentConnect(int clientID, String name)
	{
		Main.state.opponentConnect(clientID, name);
	}

	public void opponentSubmitDeck(int clientID, Map<Card, Integer> cardList)
	{
		Main.state.loadOpponentDeck(cardList, clientID);
	}
	
	public void markDeckSubmitted(int clientID)
	{
		// TODO: Mark somewhere that the user has a deck loaded
	}
	
	public void submitDeck(List<StoredCard> deck) throws JSONException
	{
		if(server.isClientConnected()) {
			server.submitDeck();
		} else if(client != null && client.isConnected()) {
			client.submitDeck(deck);
		}
	}

	public void playerJoined(int clientID, int playerID) {
		Main.state.playerJoined(clientID, playerID);
	}

	// Translate between Egg zone IDs and GameArea enum
	public GameArea zidToGameArea(int zid) {
		if(zid == 0) {
			return GameArea.Table;
		} else if(zid == 1) {
			return GameArea.DynastyDeck;
		} else if(zid == 2) {
			return GameArea.FateDeck;
		} else if(zid == 3) {
			return GameArea.DynastyDiscard;
		} else if(zid == 4) {
			return GameArea.FateDiscard;
		} else if(zid == 5) {
			return GameArea.Hand;
		} else if(zid == 6) {
			return GameArea.RemovedFromGame;
		} else if(zid == 7) {
			return GameArea.Table;
		} else if(zid == 8) {
			return GameArea.FocusPool;
		}
		throw new IllegalArgumentException();
	}

	// Translate between GameArea enum and Egg zone IDs
	public int gameAreaToZID(GameArea gameArea) {
		if(gameArea == GameArea.DynastyDeck) {
			return 1;
		} else if(gameArea == GameArea.FateDeck) {
			return 2;
		} else if(gameArea == GameArea.DynastyDiscard) {
			return 3;
		} else if(gameArea == GameArea.FateDiscard) {
			return 4;
		} else if(gameArea == GameArea.Hand) {
			return 5;
		} else if(gameArea == GameArea.RemovedFromGame) {
			return 6;
		} else if(gameArea == GameArea.Table) {
			return 7;
		} else if(gameArea == GameArea.FocusPool) {
			return 8;
		}
		throw new IllegalArgumentException();
	}

	// Translate between our and Egg X/Y locations
	public int[] eggToDojoXY(int x, int y) {
		/* X/Y:
		 * 
		 * EGG
		 * Origin is center bottom of play area (of game owner?)
		 * > is +x
		 * v is +y
		 * 190, 5 is bottom right edged
		 * -190, 5 is bottom left edged
		 * -210, 30 is off bottom left edge
		 * 190, -65 is top right edge
		 * cards are 20 wide and 30 tall
		 * 
		 * DOJO
		 * Origin is top left of play area
		 * > is +x
		 * v is +y
		 * Card origin is top left of card
		 */
		int[] newXY = new int[2];
		int height = Main.playArea.getHeight();
		int width = Main.playArea.getWidth();
		return newXY;
	}
	
	public void coinFlipped(boolean value, int playerID) {
		Main.state.coinFlipped(value, playerID);
	}

	public void setHonor(int honor, int playerID) {
		Main.state.setHonor(honor, playerID);
	}

	public void playerIDAssigned(int playerID) {
		Main.state.playerID = playerID;
	}

	public void setCardProperty(int cardID, String property, boolean value,	int playerID) {
		Main.state.setCardProperty(cardID, property, value, playerID);
	}

	public void dieRolled(int result, int size, int playerID) {
		Main.state.dieRolled(result, size, playerID);
	}

	public void moveCard(int cardID, Double x, Double y, Boolean faceUp, int moverPlayerID, GameArea destGameArea,
					 	 int destOwnerPlayerID, boolean random, Boolean toTopOfDestGameArea) {
		Main.state.moveCard(cardID, x, y, faceUp, moverPlayerID, destGameArea, destOwnerPlayerID, random, toTopOfDestGameArea);
	}

	public void revealCard(int cardID, String xmlID) {
		Main.state.revealCard(cardID, xmlID);
	}
}