package dojo.network;
//Emulator for Egg client

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import dojo.TextActionListener;

/*
Loading card database: success!
Sent: ["protocol",{"version":8}]
Connect succeeded.
Got:  ["welcome", {"clid": 5}]
Got:  ["client-names", {"names": [[0, "Toku-san"]]}]
Got:  ["client-join", {"clid": 5}]
 */
public class EggClient extends Thread
{
	// Protocol version. Keep incompatible versions from trying to play together
	private int protocolVersion = 8;
	private BufferedReader in;
	private BufferedWriter out;
	private int clientID;
	
	public EggClient(Socket s)
	{
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
						else if(command.equals("welcome"))
						{
							handleWelcome(jarray.getJSONObject(1));
						}
						else if(command.equals("client-names"))
						{
							handleClientNames(jarray.getJSONObject(1));
						}
						else if(command.equals("client-join"))
						{
							handleClientJoin(jarray.getJSONObject(1));
						}
					} catch (JSONException e) {
						e.printStackTrace();
						System.err.println("** Failed to parse JSON command from client");
					}
				}
			} catch (IOException e) {
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
			out.write(message + "\n");
			out.flush();
			System.out.println("Sent: " + message);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("** Failed to send: " + message);
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
	
	// Handle the "protocol" message from client for exchanging protocol versions as a handshake
	private void handleProtocol(JSONObject jobj) throws JSONException
	{
		if(jobj.getInt("version") == protocolVersion )
		{
			// Handshake okay, continue on
			// send welcome clid clientID
			// send client-names names clientNames
			// send deck-submitted clid clientID
			// broadcast client-join clid clientID
		}
		else
		{
			// We've encountered a different protocol version
			// Report back failure to the client
			String message = encode("rejected", "msg", "Your client protocol version is wrong (got " + jobj.getInt("version") + ", needs " + protocolVersion + ")");
			send(message);
		}
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
		
		//TODO: Send name to server
	}
	
	private void handleClientJoin(JSONObject jobj) throws JSONException
	{
		int clientID = jobj.getInt("clid");
		// Ignore the join if it is us joining
		if(this.clientID == clientID)
		{
			return;
		}
	}

	private void handleClientNames(JSONObject jobj) throws JSONException
	{
	}
}
