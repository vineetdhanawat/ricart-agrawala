import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

public class Node 
{
	public static ServerSocket server;
	
	// Hashmaps used to store sockets, read and write buffers
    public static HashMap<String,Socket> socketMap = new HashMap<String,Socket>();
    public static HashMap<Socket,BufferedReader> readers = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> writers = new HashMap<Socket,PrintWriter>();

	// Total number of nodes in the system
	public static int NUMNODES = 0;
	// ID number of this node instance
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
			InputOutputHandler IOH = new InputOutputHandler();
			RicartAgrawala RA = new RicartAgrawala(IOH);
			
			NUMNODES = IOH.readConfig();
			
			System.out.println("TOTAL NODES:"+NUMNODES);
			
			//Must Be Run In A New Thread To Avoid Thread Blocking
			ReceiveConnectionThread RCT = new ReceiveConnectionThread(nodeID,NUMNODES,IOH);
			System.out.println("Listener Started");
			
			// Sleep so that all servers/listeners can can be started
			Thread.sleep(15000);
			
			SendConnectionThread SCT = new SendConnectionThread(nodeID,NUMNODES,IOH);
			
			// Sleep so that socket connections can be made
			Thread.sleep(5000);
			
			// Starting threads for always read listeners
			for (int i=0;i<NUMNODES;i++)
			{
				if (i!=nodeID)
				{
					DaemonThread DT = new DaemonThread(socketMap.get(Integer.toString(i)),IOH, RA);
					System.out.println("SocketID"+DT);
					System.out.println("Started thread at "+nodeID+" for listening "+i);
				}
			}
			
			// TEST Broadcast
			//broadcast("ALERT");
			
			// Node
			//Thread.sleep(5000);
            
/*			Date date=new Date();
			Timestamp timestamp = new Timestamp(date.getTime());//instead of date put your converted date
			String ts = timestamp.toString();
			System.out.println("timestamp="+ts);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			Date date2 = dateFormat.parse(ts);

			Timestamp timestamp2 = new Timestamp(date.getTime());//instead of date put your converted date
			System.out.println("converted ts="+timestamp2);*/
			
			// Adding initial participants list for optimization
/*			for (int j=0; j < NUMNODES; j++)
			{
				RicartAgrawala.participants.add(String.valueOf(j));
				//public static ArrayList<String> participants = new ArrayList<String>();
			}*/
			
			// Initialization Message
			if (nodeID == 0)
			{
				new Thread()
				{
					public void run()
					{
						broadcast("START");
					}
				 }.start();
				 RA.requestCriticalSection();
			}
		}
		catch (Exception e)
		{
			//TODO add error handling
		}
	}
    
    /**
	* Broadcasts a message to all writers in the outputStreams arraylist.
	* Note this should probably never be used as RicartAgrawala is unicast
	*/
	public static void broadcast(String message)
	{
		for(int i=0; i<NUMNODES; i++)
		{
			if (i!=nodeID)
			{
				try
				{
					System.out.println("Sending "+message+" to "+i);
					Socket bs = socketMap.get(Integer.toString(i));
					PrintWriter writer = writers.get(bs);
		            writer.println(message);
	                writer.flush();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void closeSockets()
	{
		System.out.println("Halting this node");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0; i<NUMNODES; i++)
		{
			//if (i!=nodeID)
			{
				try
				{	
					System.out.println("Closing socket with "+Integer.toString(i));
					Socket socket = socketMap.get(Integer.toString(i));
					if (socket != null)
					{
						PrintWriter writer = writers.get(socket);
						BufferedReader BR = readers.get(socket);
						writer.close();
						BR.close();
						socket.close();
						System.out.println("Closed socket with "+Integer.toString(i));
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		System.exit(0);
	}
}
