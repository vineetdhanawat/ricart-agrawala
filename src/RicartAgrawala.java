import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class RicartAgrawala 
{
	public static ServerSocket server;
    public static ArrayList<Socket> sockets = new ArrayList<Socket>();
    public static HashMap<Socket,BufferedReader> readers = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> writers = new HashMap<Socket,PrintWriter>();

	// Total number of nodes in the system
	public static int NUMNODES = 0;
	
	public static void main(String[] args)
	{		
		
		// User will let the node know its nodeID
		int nodeID = 0;
		if (args.length > 0)
		{
			try
			{
		    	nodeID = Integer.parseInt(args[0]);
		    }
			catch (NumberFormatException e)
			{
				System.err.println("Argument must be an integer");
				System.exit(1);
		    }
		}
		try 
		{
			// Bad way of reading config
			// TODO: Fix later.
			NUMNODES = ReadConfig.main();
			
			// Listeners
			initializeSystem(nodeID);
			
			makeConnections(nodeID);
			
		} 
		catch (Exception e)
		{
			//TODO add error handling
		}
	}
	
	public static void initializeSystem(int nodeID)
	{
		try
		{
			// Start Server at the specified port
			int port = Integer.parseInt(ReadConfig.map.get(Integer.toString(nodeID)).get(1));
			server = new ServerSocket(port);
			System.out.println("Node "+nodeID+" listening at "+port);
			
			// First connection
			int i = NUMNODES;
			while (i>=0)
			{
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				System.out.println("Before Accept");
				Socket tmp = server.accept();
				System.out.println("After Accept");
				sockets.add(tmp);
	            readers.put(tmp,new BufferedReader(new InputStreamReader(tmp.getInputStream())));
	            writers.put(tmp,new PrintWriter(tmp.getOutputStream()));
	            
	            PrintWriter writer = writers.get(tmp);
	            writer.println("Hello World:"+nodeID);
                writer.flush();

	            System.out.println("Reading Message"+readers.get(tmp).readLine());
	            i--;
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void makeConnections(int nodeID)
	{
		Socket socket;
		for(int i=1;i<=NUMNODES;i++)
		{
			if (nodeID > i)
			{
				int port = Integer.parseInt(ReadConfig.map.get(Integer.toString(i)).get(1));
				String host = ReadConfig.map.get(Integer.toString(i)).get(2);
				try
				{
					System.out.println("Connecting "+host+":"+port);
					socket = new Socket(host,port);
					System.out.println("Connection established");
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}
