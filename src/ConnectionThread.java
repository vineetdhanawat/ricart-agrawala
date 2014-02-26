import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionThread extends Thread
{
	int nodeID, NUMNODES;
	ConnectionThread(int nodeID, int NUMNODES)
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
					
		            PrintWriter writer = new PrintWriter(socket.getOutputStream());
		            writer.println("Hello World from:"+nodeID);
	                writer.flush();
	                BufferedReader BR = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                System.out.println("Reading Message "+ BR.readLine());
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