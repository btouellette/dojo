package dojo.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import dojo.Main;

public class Network extends Thread
{
	// Port the game communications use
	private int gamePort = 18073;
	private List<NetworkHandler> connections;

	public Network()
	{
		// Using a vector since it is thread safe
		connections = new Vector<NetworkHandler>();
	}

	public void run()
	{
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(gamePort);
			// Once we're listening on the game port start a new server
			EggServer server = new EggServer(this);
			while (true) {
				try {
					// Block till we see a new connection incoming
					Socket clientSocket = serverSocket.accept();
					// Accept it and pass it into the server code
					server.clientConnect(clientSocket);
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
			EggClient client = new EggClient(hostSocket, this);
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

	public void broadcast(String message)
	{
		for (NetworkHandler nh : connections) {
			nh.send(message);
		}
	}
	
	public void opponentConnect(int id, String name)
	{
		Main.state.opponentConnect(id, name);
	}
}