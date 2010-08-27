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
			while(true)
			{
				ServerSocket serverSocket = new ServerSocket(gamePort);
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
				String outputLine = in.readLine();
				if(outputLine != null)
				{
					try {
						JSONArray jarray = new JSONArray(outputLine);
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
		JSONWriter object = new JSONStringer().object()
		                                      .key(key)
		                                      .value(value)
		                                      .endObject();
		String message = new JSONStringer().array()
		                                   .value(type)
		                                   .value(object)
		                                   .endArray()
		                                   .toString();
		return message;
	}
	
	// Encode values into a JSON compatible string to send over the network
	private String encode(String type, String key, String value) throws JSONException
	{
		JSONWriter object = new JSONStringer().object()
		                                      .key(key)
		                                      .value(value)
		                                      .endObject();
		String message = new JSONStringer().array()
		                                   .value(type)
		                                   .value(object)
		                                   .endArray()
		                                   .toString();
		return message;
	}
	
	public void send(String message)
	{
		try {
			out.write(message);
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