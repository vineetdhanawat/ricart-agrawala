import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import java.util.logging.Logger;

public class ListenerThread extends Thread
{
	long messageTS, currentTS;
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
				
				// TODO Implement Terminating Condition
				if(messageType.equals("COMPLETE"))
				{
					RicartAgrawala.nodeCompletetionCount++;
					if (RicartAgrawala.nodeCompletetionCount == Server.NUMNODES-1 && 
					RicartAgrawala.nodeZeroCompletetion == true)
						System.out.println("ALLLLLL OVERRRRR");
				}
				
				if(messageType.equals("REPLY"))
				{
					
					//++RicartAgrawala.replyCount;
					RicartAgrawala.incrementCount();

					// optimization
					RicartAgrawala.participants.remove(tokens[1]);
					
					System.out.println("REPLYCOUNT:"+RicartAgrawala.replyCount+":REPLYFROM"+tokens[1]);
					RicartAgrawala.checkCS();
				}
				
				if(messageType.equals("REQUEST"))
				{
						
					System.out.println("SERVER-TS REQUEST:"+RicartAgrawala.requestTS);
					/*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS");
					Date date = dateFormat.parse(tokens[1]);
					messageTS = new Timestamp(date.getTime());*/
					
					//messageTS = Timestamp.valueOf(tokens[1]);
					messageTS = Long.parseLong(tokens[1]);
					
					System.out.println("MESSAGE-TS REQUEST:"+tokens[2]+":"+messageTS);
					System.out.println("----------------------------------");
					
					if(RicartAgrawala.criticalSection == false && 
							((RicartAgrawala.requestCS == false)
							|| (RicartAgrawala.requestCS == true && RicartAgrawala.requestTS > messageTS)
							|| (RicartAgrawala.requestCS == true && RicartAgrawala.requestTS == messageTS
							 && Server.nodeID > Integer.parseInt(tokens[2]))))
					/*if(RicartAgrawala.criticalSection == false &&
							(RicartAgrawala.requestCS == false ||
							(RicartAgrawala.requestCS == true && 
							(RicartAgrawala.requestTS.after(messageTS) || 
									Server.nodeID > Integer.parseInt(tokens[2])))))*/
						
					
            		{
						if (RicartAgrawala.requestCS == true && RicartAgrawala.criticalSection == false 
			            		&& RicartAgrawala.criticalSectionCount != 0 && !(RicartAgrawala.copyOfParticipants.contains(tokens[2])))
			            {
			            	++RicartAgrawala.participantsCount;
			            	PrintWriter writer2 = Server.writers.get(socket);
			            	long requestTS = TimeStamp.getTimestamp();
    			            writer2.println("REQUEST,"+requestTS+","+Server.nodeID);
    			            writer2.flush();
    			            System.out.println("Sending delayed request to"+tokens[2]+":"+requestTS);
			            }
						
						System.out.println("REPLY SENT TO:"+tokens[2]);
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
						System.out.println("Deferred reply to "+tokens[2]);
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