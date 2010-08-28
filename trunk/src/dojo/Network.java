package dojo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import org.json.*;

public class Network extends Thread
{
	// Protocol version. Keep incompatible versions from trying to play together
	private int protocolVersion = 8;
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
	
	public boolean connect(String server)
	{
		try {
			Socket hostSocket = new Socket(server, gamePort);
			NetworkHandler nh = new NetworkHandler(hostSocket, connections);
			nh.handshake();
			nh.start();
			connections.add(nh);
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

private class NetworkHandler extends Thread
{
	private BufferedReader in;
	private BufferedWriter out;
	private List<NetworkHandler> connections;
	
	public NetworkHandler(Socket s, List<NetworkHandler> connections)
	{
		this.connections = connections;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("** Failed to get in/out stream to client");
		}
	}
	
	public void run()
	{
		while (true)
		{
			try {
				String inputLine = in.readLine();
				if(inputLine != null)
				{
					System.out.println("Got:  " + inputLine);
					try {
						JSONArray jarray = new JSONArray(inputLine);
						String command = jarray.getString(0);
						if(command.equals("protocol"))
						{
							handleProtocol(jarray.getJSONObject(1));
						}
						else if(command.equals("rejected"))
						{
							handleRejected(jarray.getJSONObject(1));
						}
					} catch (JSONException e) {
						e.printStackTrace();
						System.err.println("** Failed to parse JSON command from client");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("** Couldn't get new line from client");
			}
		}
	}
	
	// Encode values into a JSON compatible string to send over the network
	private String encode(String type, String key, int value) throws JSONException
	{
		// Create an array where:
		// 1st: String of message type
		// 2nd: Object with multiple key value pairs corresponding to information to send
		JSONStringer message = new JSONStringer();
		message.array();
		message.value(type);
		message.object();
		message.key(key);
        message.value(value);
        message.endObject();
		message.endArray();
		return message.toString();
	}
	
	// Encode values into a JSON compatible string to send over the network
	private String encode(String type, String key, String value) throws JSONException
	{
		// Create an array where:
		// 1st: String of message type
		// 2nd: Object with multiple key value pairs corresponding to information to send
		JSONStringer message = new JSONStringer();
		message.array();
		message.value(type);
		message.object();
		message.key(key);
        message.value(value);
        message.endObject();
		message.endArray();
		return message.toString();
	}
	
	public void send(String message)
	{
		try {
			out.write(message);
			System.out.println("Sent: " + message);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("** Failed to send: " + message);
		}
	}

	private void broadcast(String message)
	{
		for(NetworkHandler nh : connections)
		{
			if(!nh.equals(this))
			{
				nh.send(message);
			}
		}
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
		String message = encode("protocol", "version", protocolVersion);
		send(message);
	}
	
	// Exchanging protocol versions as a handshake
	private void handleProtocol(JSONObject jobj) throws JSONException
	{
		if(jobj.getInt("version") == protocolVersion )
		{
			// Handshake okay, continue on
		}
		else
		{
			// We've encountered a different protocol version
			// Report back failure to the client
			String message = encode("rejected", "msg", "Your client protocol version is wrong (got " + jobj.getInt("version") + ", needs " + protocolVersion + ")");
			send(message);
		}
	}
	
	// Sent a rejected message if our client protocol handshake wasn't established correctly
	private void handleRejected(JSONObject jobj) throws JSONException
	{
		String message = jobj.getString("msg");
	}
}
}