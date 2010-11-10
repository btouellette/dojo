package dojo.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

public class Network extends Thread
{
	// Port the game communications use
	private int gamePort = 18072;
	private List<NetworkHandler> connections;
	
	public Network()
	{
		// Using a vector since it is thread safe
		connections = new Vector<NetworkHandler>();
	}
	
	public void run()
	{
		try {
			ServerSocket serverSocket = new ServerSocket(gamePort);
			while(true)
			{
				try {
					Socket clientSocket = serverSocket.accept();
					NetworkHandler nh = new NetworkHandler(clientSocket, connections);
					nh.start();
					connections.add(nh);
					System.out.println("Accept succeeded.");
				} catch (IOException e) {
					System.err.println("Accept failed.");
				}
			}
		} catch (IOException e) {
			System.err.println("** Could not listen on port: " + gamePort + ".");
		}
	}
	
	public boolean connectEgg(String server)
	{
		try {
			Socket hostSocket = new Socket(server, gamePort);
			EggClient client = new EggClient(hostSocket);
			client.start();
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
		for(NetworkHandler nh : connections)
		{
			nh.send(message);
		}
	}
}