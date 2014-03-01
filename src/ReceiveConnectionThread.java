import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ReceiveConnectionThread extends Thread
{
	int nodeID, NUMNODES;
	ReceiveConnectionThread(int nodeID, int NUMNODES)
	{
		super();
		start();
		this.nodeID = nodeID;
		this.NUMNODES = NUMNODES;
	}
	public void run()
	{
		try
		{
			// Start Server at the specified port
			int port = Integer.parseInt(ReadConfig.map.get(Integer.toString(nodeID)).get(1));
			ServerSocket server = new ServerSocket(port);
			System.out.println("Node "+nodeID+" listening at "+port);
			
			// First connection
			int i = 0;
			while (NUMNODES>1)
			{
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				Socket socket = server.accept();
				System.out.println("Socket at "+nodeID+" for listening "+i + " "+ socket);
				System.out.println("-------------------------");
				
				Server.socketMap.put(Integer.toString(i),socket);
				Server.readers.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
				Server.writers.put(socket,new PrintWriter(socket.getOutputStream()));
				
	            // incrementing i so that all incoming connections can be put in array in order.
	            i++;
	            
	            // Total no of incoming connections left
	            NUMNODES--;
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}