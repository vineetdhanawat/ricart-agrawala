import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ListenerThread extends Thread
{
	Timestamp messageTS;
	Socket socket;
	BufferedReader BR;
	ListenerThread(Socket socket)
	{
		super();
		start();
		this.socket = socket;
		try {
			BR = Server.readers.get(socket);
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
			while((message = BR.readLine() ) != null)
			{
				//System.out.println("RECEIVED MESSAGE:::" + message);
				
				if(message.equals("START"))
				{
					RicartAgrawala.requestCriticalSection();
				}
				
				if(message.equals("REPLY"))
				{
					RicartAgrawala.replyCount++;
					System.out.println("REPLYCOUNT:"+RicartAgrawala.replyCount);
					if (RicartAgrawala.replyCount == Server.NUMNODES-1)
						System.out.println("CRITICAL SECTION");
				}
				
				String tokens[] = message.split(",");
				String messageType = tokens[0];

				if(messageType.equals("REQUEST"))
				{
					System.out.println("SERVER REQUEST:"+RicartAgrawala.requestTS);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					Date date = dateFormat.parse(tokens[1]);
					messageTS = new Timestamp(date.getTime());
					System.out.println("MESSAGE REQUEST:"+messageTS);
					System.out.println("----------------------------------");
					if(RicartAgrawala.requestCS == false || (RicartAgrawala.requestCS == true && 
							RicartAgrawala.requestTS.after(messageTS)))
            		{
						// Reply
						System.out.println("Sending reply for this above request");
						System.out.println("requestCS"+RicartAgrawala.requestCS);
						PrintWriter writer = Server.writers.get(socket);
						writer.println("REPLY");
			            writer.flush();
            		}
					else
					{
						// defer REPLY
					}
				}
			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}