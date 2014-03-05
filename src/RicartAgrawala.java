import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Date;
import java.util.Random;


public class RicartAgrawala {
	
	static long requestTS;
	static int criticalSectionCount = 0;
	static boolean requestCS = false;
	static boolean criticalSection = false;
	static int nodeCompletetionCount = 0;
	static boolean nodeZeroCompletetion = false;
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
            //requestTS = new Timestamp(date.getTime());
			requestTS = TimeStamp.getTimestamp();
            
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
            	copyOfParticipants.clear();
            	copyOfParticipants.addAll(participants);
            	participantsCount = copyOfParticipants.size();
            	if (copyOfParticipants.isEmpty())
            	{
            		
            		new Thread()
    				{
            		public void run(){
            		RicartAgrawala.criticalSection = true;
            		long currentTS1 = TimeStamp.getTimestamp();
    	            WriteToFile.execute(RicartAgrawala.requestTS,currentTS1,"entered");
		            java.util.Date date1= new java.util.Date();
		            long currentTS = TimeStamp.getTimestamp();
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
					long currentTS2 = TimeStamp.getTimestamp();
		            WriteToFile.execute(RicartAgrawala.requestTS,currentTS2,"exited");
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
    				}.start();
					
				}
            	else
            	{
            		
            		for(String item: copyOfParticipants)
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
            	}
            }
		}
		else
		{
			System.out.println("GAMEOVER");
			// TODO Implement Terminating Condition
			/*if (Server.nodeID !=0)
			{
				Socket bs = Server.socketMap.get(0);
				PrintWriter writer = Server.writers.get(bs);
				writer.println("COMPLETE"+","+Server.nodeID);
	            writer.flush();
			}
			else
			{
				nodeZeroCompletetion = true;
				if (RicartAgrawala.nodeCompletetionCount == Server.NUMNODES-1)
					System.out.println("ALLLLLL OVERRRRR");
			}*/
		}
	}
	
	public static void sendDeferredReplies()
	{
		System.out.println("Sending deferred replies");
		// TODO
		for(int i=0; i<deferred.size(); i++)
		{
			String deferredNode = deferred.get(i);
			System.out.println("Sending deferred replies to:"+deferredNode);
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
	
	public static synchronized void checkCS()
	{
		//if (RicartAgrawala.replyCount == Server.NUMNODES-1)
			if ((RicartAgrawala.criticalSectionCount == 0 && 
					RicartAgrawala.replyCount == Server.NUMNODES-1) || 
					RicartAgrawala.replyCount == RicartAgrawala.participantsCount)
			{
				RicartAgrawala.criticalSection = true;
	            
				/*java.util.Date date= new java.util.Date();
	            Timestamp currentTS1 = new Timestamp(date.getTime());*/
				
				long currentTS1 = TimeStamp.getTimestamp();
	            WriteToFile.execute(RicartAgrawala.requestTS,currentTS1,"entered");

				System.out.println("CRITICAL SECTION:"+ RicartAgrawala.criticalSectionCount +":"
	            +currentTS1);
				
				// Delay of 20 milliseconds
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// Reset this node
				RicartAgrawala.criticalSection = false;
				RicartAgrawala.requestCS = false;
				RicartAgrawala.replyCount = 0;
				RicartAgrawala.criticalSectionCount++;
				
				/*java.util.Date date1= new java.util.Date();
				Timestamp currentTS2 = new Timestamp(date1.getTime());*/
				
				long currentTS2 = TimeStamp.getTimestamp();
				WriteToFile.execute(RicartAgrawala.requestTS,currentTS2,"exited");
				RicartAgrawala.sendDeferredReplies();
				
				if (Server.nodeID % 2 == 0 && criticalSectionCount > 20)
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
	
	public static synchronized void incrementCount() {
		replyCount++;
    }
}
