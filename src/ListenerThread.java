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
import java.util.Random;

public class ListenerThread extends Thread
{
	Timestamp messageTS, currentTS;
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
	}

	public void run()
	{
		String message;
		try
		{
			while((message = BR.readLine() ) != null)
			{
				//System.out.println("RECEIVED MESSAGE:::" + message);
				String tokens[] = message.split(",");
				String messageType = tokens[0];
				
				if(messageType.equals("START"))
				{
					RicartAgrawala.requestCriticalSection();
				}
				
				if(messageType.equals("REPLY"))
				{
					++RicartAgrawala.replyCount;
					// optimization
					RicartAgrawala.participants.remove(tokens[1]);
					
					System.out.println("REPLYCOUNT:"+RicartAgrawala.replyCount+":REPLYFROM"+tokens[1]);
					
					//if (RicartAgrawala.replyCount == Server.NUMNODES-1)
					if ((RicartAgrawala.criticalSectionCount == 0 && 
							RicartAgrawala.replyCount == Server.NUMNODES-1) || 
							RicartAgrawala.replyCount == RicartAgrawala.participantsCount)
					{
						RicartAgrawala.criticalSection = true;
			            java.util.Date date= new java.util.Date();
			            currentTS = new Timestamp(date.getTime());
						System.out.println("CRITICAL SECTION:"+ RicartAgrawala.criticalSectionCount +":"
			            +currentTS);
						
						// Delay of 20 milliseconds
						Thread.sleep(20);
						
						// Reset this node
						RicartAgrawala.criticalSection = false;
						RicartAgrawala.requestCS = false;
						RicartAgrawala.replyCount = 0;
						RicartAgrawala.criticalSectionCount++;
						RicartAgrawala.sendDeferredReplies();
						
						if (Server.nodeID % 2 == 0)
						{
							Random rn = new Random();
							int time = 200 + rn.nextInt(300);
							try {
								Thread.sleep(time);
								System.out.println("delay of "+time);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						RicartAgrawala.requestCriticalSection();
					}
				}
				
				if(messageType.equals("REQUEST"))
				{
					System.out.println("SERVER-TS REQUEST:"+RicartAgrawala.requestTS);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					Date date = dateFormat.parse(tokens[1]);
					messageTS = new Timestamp(date.getTime());
					System.out.println("MESSAGE-TS REQUEST:"+tokens[2]+":"+messageTS);
					System.out.println("----------------------------------");
					if((RicartAgrawala.requestCS == false &&  RicartAgrawala.criticalSection == false)
							|| (RicartAgrawala.requestCS == true && RicartAgrawala.requestTS.after(messageTS))
							|| (RicartAgrawala.requestCS == true && RicartAgrawala.requestTS.equals(messageTS)
							 && Server.nodeID > Integer.parseInt(tokens[2])))
            		{
						// Reply
						PrintWriter writer = Server.writers.get(socket);
						writer.println("REPLY"+","+Server.nodeID);
			            writer.flush();
			            
			            // optimization
			            RicartAgrawala.participants.add(tokens[2]);
            		}
					else
					{
						// defer REPLY
						RicartAgrawala.deferred.add(tokens[2]);
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