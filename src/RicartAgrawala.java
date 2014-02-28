import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

public class RicartAgrawala 
{
	public static ServerSocket server;
    public static ArrayList<Socket> sockets = new ArrayList<Socket>();
    
    public static HashMap<String,Socket> socketMap = new HashMap<String,Socket>();
    public static HashMap<Socket,BufferedReader> readers = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> writers = new HashMap<Socket,PrintWriter>();

	// Total number of nodes in the system
	public static int NUMNODES = 0;
	public static int nodeID = 0;
	
	public static void main(String[] args)
	{		
		
		// User will let the node know its nodeID

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
			
			//Must Be Run In A New Thread To Avoid Thread Blocking
			ListenerThread LT = new ListenerThread(nodeID,NUMNODES);
			System.out.println("Listener Started");
			
			Thread.sleep(5000);
			
			ConnectionThread CT = new ConnectionThread(nodeID,NUMNODES);
			
			Thread.sleep(5000);
			
			Socket socket;
			
			// Starting threads to always read listeners
			for (int i=0;i<NUMNODES;i++)
			{
				if (i!=nodeID)
				{
					ReadSocket RS = new ReadSocket(socketMap.get(Integer.toString(i)));
					System.out.println("SocketID"+RS);
					System.out.println("Started thread at "+nodeID+" for listening "+i);
				}
			}
			
			//broadcast("ALERT");
			
			// TEST SEND
			/*if (nodeID == 1)
			send("BROADCAST",socketMap.get(Integer.toString(2)));
			 */
			
		}
		catch (Exception e)
		{
			//TODO add error handling
		}
	}
	
	// Send message over a socket
    public static void send(String str,Socket so)
    {
        PrintWriter writer = writers.get(so);
        writer.println(str);
        writer.flush();
    }
    
    /**
	* Broadcasts a message to all writers in the outputStreams arraylist.
	* Note this should probably never be used as RicartAgrawala is unicast
	*/
	public static void broadcast(String message)
	{
		for(int i = 0; i < NUMNODES; i++)
		{
			if (i!=nodeID)
			{
				try
				{
					System.out.println("Sending "+message+" to "+i);
					Socket bs = socketMap.get(Integer.toString(i));
					PrintWriter writer = writers.get(bs);
		            writer.println(message+nodeID);
	                writer.flush();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
}
