import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadSocket extends Thread
{
	Socket socket;
	BufferedReader BR;
	ReadSocket(Socket socket)
	{
		super();
		start();
		this.socket = socket;
		try {
			BR = RicartAgrawala.readers.get(socket);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.NUMNODES = NUMNODES;
	}

	public void run()
	{
		String message;
		try
		{
			while(( message = BR.readLine() ) != null)
			{
				System.out.println("RECEIVED MESSAGE:::" + message);
			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}