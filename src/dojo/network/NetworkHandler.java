package dojo.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONStringer;

/**
 *  For any client or server component that requires network communication
 *  Provides JSON encoding and read/write to socket
 * @author Brian Ouellette
 */
// Provides JSON encoding and read/write to socket
public class NetworkHandler extends Thread
{
	private BufferedReader in;
	private BufferedWriter out;
	private int clientID;

	public NetworkHandler(Socket s)
	{
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("** Failed to get in/out stream to client");
		}
	}
	
	public String readLine() throws IOException
	{
		return in.readLine();
	}

	// Encode values into a JSON compatible string to send over the network
	public String encode(String type, String key, int value) throws JSONException
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
	public String encode(String type, String key, String value) throws JSONException
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

	// send ["client-names", {"names": [[0, "Toku-san"]]}]
	public String encode(String type, String key, int[] intValues, String[] stringValues) throws JSONException
	{
		if(intValues.length != stringValues.length) {
			throw new IllegalArgumentException("Must have same number of integer and string values");
		}
		
		JSONStringer message = new JSONStringer();
		message.array();
		message.value(type);
		message.object();
		message.key(key);
		message.array();
		for (int i = 0; i < intValues.length; i++) {
			message.array();
			message.value(intValues[i]);
			message.value(stringValues[i]);
			message.endArray();
		}		
		message.endArray();
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
