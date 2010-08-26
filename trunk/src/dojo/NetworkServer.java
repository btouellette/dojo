package dojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer
{
	// Protocol version
	int versionNumber = 8;
	
	public NetworkServer()
	{
	    ServerSocket serverSocket = null;
	    try {
	        serverSocket = new ServerSocket(18072);
	
	        Socket clientSocket = null;
	        try {
	            clientSocket = serverSocket.accept();
	        } catch (IOException e) {
	            System.err.println("Accept failed.");
	            System.exit(1);
	        }
	        System.out.println("Accept succeeded.");
	        BufferedReader in = new BufferedReader(
					new InputStreamReader(
					clientSocket.getInputStream()));
	        String outputLine;
	
	        while (true) {
	             outputLine = in.readLine();
	             System.out.println(outputLine);
	        }
	    } catch (IOException e) {
	        System.err.println("Could not listen on port: 4444.");
	        System.exit(1);
	    }
	}
	
	public void handleProtocol(String message)
	{
		if(message.equals("[\"protocol\", {\"version\": " + versionNumber + "}]"))
		{
		}
	}
}
