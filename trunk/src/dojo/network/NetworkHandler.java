package dojo.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONStringer;

//TODO: Remove when developing this class
@SuppressWarnings("unused")
public class NetworkHandler extends Thread
{
	private BufferedReader in;
	private BufferedWriter out;
	private List<NetworkHandler> connections;
	private int clientID;
	private Map<Integer,String> clientNames;
	
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
			out.write(message + "\n");
			out.flush();
			System.out.println("Sent: " + message);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("** Failed to send: " + message);
		}
	}
}
