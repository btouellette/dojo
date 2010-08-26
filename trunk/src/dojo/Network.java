package dojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.*;

public class Network
{
	// Protocol version. Keep incompatible versions from trying to play together
	private int protocolVersion = 8;
	// Port the game communications use
	private int gamePort = 18072;
	
	public Network()
	{
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(gamePort);
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				System.out.println("Accept succeeded.");
			} catch (IOException e) {
				System.err.println("Accept failed.");
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String outputLine;
			JSONArray jarray;
			while (true)
			{
				outputLine = in.readLine();
				if(outputLine != null)
				{
					jarray = new JSONArray(outputLine);
					String command = jarray.getString(0);
					if(command.equals("protocol"))
					{
						handleProtocol(jarray.getJSONObject(1));
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + gamePort + ".");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Encode values into a JSON compatible string to send over the network
	public String encode(String type, String key, int value) throws JSONException
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
	public String encode(String type, String key, String value) throws JSONException
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
	
	public void sendProtocol() throws JSONException
	{
		String message = encode("protocol", "version", protocolVersion);
	}
	
	public void handleProtocol(JSONObject jobj) throws JSONException
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
		}
	}
}
