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
	        while (true) {
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

	public void sendProtocol() throws JSONException
	{
		JSONWriter version = new JSONStringer().object()
		                                       .key("version")
		                                       .value(protocolVersion)
		                                       .endObject();
		String message = new JSONStringer().array()
		                                   .value("protocol")
		                                   .value(version)
		                                   .endArray()
		                                   .toString();
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
			JSONWriter msg = new JSONStringer().object()
			                                   .key("msg")
			                                   .value("Your client protocol version is wrong (got " + jobj.getInt("version") + ", needs " + protocolVersion + ")")
			                                   .endObject();
			String message = new JSONStringer().array()
			                                   .value("rejected")
			                                   .value(msg)
			                                   .endArray()
			                                   .toString();
		}
	}
}
