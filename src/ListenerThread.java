import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ListenerThread extends Thread
{
	int nodeID, NUMNODES;
	ListenerThread(int nodeID, int NUMNODES)
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
			while (NUMNODES>1)
			{
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				System.out.println("Before Accept");
				Socket tmp = server.accept();
				System.out.println("After Accept");
				RicartAgrawala.sockets.add(tmp);
				RicartAgrawala.readers.put(tmp,new BufferedReader(new InputStreamReader(tmp.getInputStream())));
				RicartAgrawala.writers.put(tmp,new PrintWriter(tmp.getOutputStream()));
				
				PrintWriter writer = RicartAgrawala.writers.get(tmp);
                writer.println("Hello World from:"+nodeID);
                writer.flush();
	            System.out.println("Reading Message "+RicartAgrawala.readers.get(tmp).readLine());
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