import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class SendConnectionThread extends Thread
{
	int nodeID, NUMNODES;
	SendConnectionThread(int nodeID, int NUMNODES)
	{
		super();
		start();
		this.nodeID = nodeID;
		this.NUMNODES = NUMNODES;
	}
	public void run()
	{
		Socket socket;
		for(int i=0;i<NUMNODES;i++)
		{
			if (nodeID < i)
			{
				String host = ReadConfig.map.get(Integer.toString(i)).get(0);
				int port = Integer.parseInt(ReadConfig.map.get(Integer.toString(i)).get(1));
				try
				{
					System.out.println("Connecting "+host+":"+port);
					socket = new Socket(host,port);
					System.out.println("Connection established");
					
					System.out.println("Socket at "+nodeID+" for sending to "+i + " "+ socket);
					System.out.println("-------------------------");
					
					Server.socketMap.put(Integer.toString(i),socket);
					Server.readers.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
					Server.writers.put(socket,new PrintWriter(socket.getOutputStream()));
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