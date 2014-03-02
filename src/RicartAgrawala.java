import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Date;
import java.util.Random;


public class RicartAgrawala {
	
	static Timestamp requestTS,currentTS;
	static int criticalSectionCount = 0;
	static boolean requestCS;
	static boolean criticalSection = false;
	static int replyCount = 0;
	
	public static ArrayList<String> deferred = new ArrayList<String>();
	public static ArrayList<String> participants = new ArrayList<String>();
	static ArrayList<String> copyOfParticipants = new ArrayList<String>();
	public static int participantsCount = 0;
	// TODO Algorithm Class
	public static void requestCriticalSection()
	{
		// limiting total no of desired critical sections
		if (criticalSectionCount < 40)
		{
			// delay critical section call randomly
			Random rn = new Random();
			int time = 10 + rn.nextInt(90);
			try {
				Thread.sleep(time);
				System.out.println("delay of "+time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(Server.nodeID+" ready to enter CS");
			RicartAgrawala.requestCS = true;
            java.util.Date date= new java.util.Date();
            requestTS = new Timestamp(date.getTime());
            
            if (criticalSectionCount == 0)
            {
    			for(int i=0; i<Server.NUMNODES; i++)
    			{
    				if (i!=Server.nodeID)
    				{
    					try
    					{
    						Socket bs = Server.socketMap.get(Integer.toString(i));
    						PrintWriter writer = Server.writers.get(bs);
    			            writer.println("REQUEST,"+requestTS+","+Server.nodeID);
    			            writer.flush();
    			            System.out.println("Sending request to others at:"+requestTS);
    					}
    					catch(Exception ex)
    					{
    						ex.printStackTrace();
    					}
    				}
    			}
            }
            else
            {
            	copyOfParticipants.addAll(participants);
            	participantsCount = copyOfParticipants.size();
            	if (copyOfParticipants.isEmpty())
            	{
            		RicartAgrawala.criticalSection = true;
		            java.util.Date date1= new java.util.Date();
		            currentTS = new Timestamp(date1.getTime());
					System.out.println("CRITICAL SECTION:"+ RicartAgrawala.criticalSectionCount +":"
		            +currentTS);
					
					// Delay of 20 milliseconds
					try {
						Thread.sleep(20);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					// Reset this node
					RicartAgrawala.criticalSection = false;
					RicartAgrawala.requestCS = false;
					RicartAgrawala.replyCount = 0;
					RicartAgrawala.criticalSectionCount++;
					RicartAgrawala.sendDeferredReplies();
					
					if (RicartAgrawala.criticalSectionCount> 20 && Server.nodeID % 2 == 0)
					{
						Random rn1 = new Random();
						int time1 = 200 + rn1.nextInt(300);
						try {
							Thread.sleep(time1);
							System.out.println("delay of "+time1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					RicartAgrawala.requestCriticalSection();
				}
            	else
            	{
            		for(String item: participants)
                	{
        				if (Integer.parseInt(item)!= Server.nodeID)
        				{
        					try
        					{
        						Socket bs = Server.socketMap.get(item);
        						PrintWriter writer = Server.writers.get(bs);
        			            writer.println("REQUEST,"+requestTS+","+Server.nodeID);
        			            writer.flush();
        			            System.out.println("Sending request to"+item+":"+requestTS);
        					}
        					catch(Exception ex)
        					{
        						ex.printStackTrace();
        					}
        				}
                	}
                	copyOfParticipants.clear();
            	}
            }
		}
		else
		{
			System.out.println("GAMEOVER");
		}
	}
	
	public static void sendDeferredReplies()
	{
		// TODO
		for(int i=0; i<deferred.size(); i++)
		{
			String deferredNode = deferred.get(i);
			try
			{
				Socket bs = Server.socketMap.get(deferredNode);
				PrintWriter writer = Server.writers.get(bs);
				writer.println("REPLY"+","+Server.nodeID);
	            writer.flush();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			participants.add(deferredNode);
		}
		//clearing arraylist for reuse
		deferred.clear();
	}
}
