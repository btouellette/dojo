package dojo.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

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
	
	public void opponentConnect(int clientID, String name)
	{
		Main.state.opponentConnect(clientID, name);
	}
	
	public void opponentNameChange(int clientID, String name)
	{
		Main.state.setOpponentName(clientID, name);
	}

	public void opponentSubmitDeck(int clientID, Map<String, Integer> cardList)
	{
		Main.state.loadOpponentDecks(clientID, cardList);
	}
	
	public void markDeckSubmitted(int clientID)
	{
		// TODO: Mark somewhere that the user has a deck loaded
	}
	
	public void submitDeck(List<StoredCard> deck) throws JSONException
	{
		if(server.isClientConnected()) {
			server.submitDeck();
		} else if(client.isConnected()) {
			client.submitDeck(deck);
		}
	}
}