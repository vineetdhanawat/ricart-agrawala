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
			
			//Must Be Run In A New Thread To Avoid Thread Blocking
			ListenerThread LT = new ListenerThread(nodeID,NUMNODES);
			System.out.println("Listener Started");
			
			Thread.sleep(5000);
			
			ConnectionThread CT = new ConnectionThread(nodeID,NUMNODES);
			
		} 
		catch (Exception e)
		{
			//TODO add error handling
		}
	}
	
	// Send message over a socket
    public void send(String str,Socket so)
    {
        for (Socket sock:sockets)
        {
        	if (!sock.equals(so))
            {
                PrintWriter writer = writers.get(sock);
                writer.println(str);
                writer.flush();
            }
        }
    }
}
