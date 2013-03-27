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
	public boolean hosting = false;

	public void run()
	{
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(gamePort);
			// Once we're listening on the game port start a new server
			server = new EggServer(this);
			while (true) {
				try {
					// Block till we see a new connection incoming
					Socket clientSocket = serverSocket.accept();
					// Accept it and pass it into the server code
					server.clientConnect(clientSocket);
					hosting = true; // TODO: Set this to false whenever the total number of connected clients drops to zero
					System.out.println("Accept succeeded.");
				} catch (IOException e) {
					System.err.println("Accept failed.");
				}
			}
		} catch (IOException e) {
			System.err.println("** Could not listen on port: " + gamePort + ".");
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.err.println("** Failed to close ServerSocket.");
				}
			}
		}
	}

	public boolean connectEgg(String server)
	{
		try {
			if(hosting)
			{
				System.err.println("** Already hosting a game.");
				return false;
			}
			Socket hostSocket;
			// Check for if port syntax was given
			if (!server.contains(":")) {
				hostSocket = new Socket(server, gamePort);
			} else {
				String[] splitServer = server.split(":");
				String hostname = splitServer[0];
				String port = splitServer[1];
				hostSocket = new Socket(hostname, Integer.parseInt(port));
			}
			client = new EggClient(hostSocket, this);
			client.start();
			client.handshake();
			System.out.println("Connect succeeded.");
		} catch (UnknownHostException e) {
			System.err.println("** Could not find server " + server);
			return false;
		} catch (IOException e) {
			System.err.println("** Could not connect to server " + server);
			return false;
		}
		return true;
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
	
	public void submitDeck(List<StoredCard> deck) throws JSONException
	{
		if(hosting) {
			// TODO: server deck send
		} else if(client.isConnected()) {
			client.submitDeck(deck);
		}
	}
}