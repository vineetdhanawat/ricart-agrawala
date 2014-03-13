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
	
	InputOutputHandler IOH;
	RicartAgrawala(InputOutputHandler IOH)
	{
		this.IOH = IOH;
	}
	
	public long requestTS;
	public int criticalSectionCount = 0;
	public boolean requestCS = false;
	public boolean criticalSection = false;
	public int nodeCompletetionCount = 0;
	public boolean nodeZeroCompletetion = false;
	public int replyCount = 0;
	public long totalRequestsSent = 0;
	public long timeElapsed = 0;
	public long maxMessagesExchanged = 0;
	public long minMessagesExchanged = 0;
	
	public ArrayList<String> deferred = new ArrayList<String>();
	public ArrayList<String> participants = new ArrayList<String>();
	public ArrayList<String> copyOfParticipants = new ArrayList<String>();
	public int participantsCount = 0;
	
	// TODO Algorithm Class
	public void requestCriticalSection()
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
			
			System.out.println(Node.nodeID+" ready to enter CS");
			requestCS = true;
			
            java.util.Date date= new java.util.Date();
            //requestTS = new Timestamp(date.getTime());
			requestTS = TimeStamp.getTimestamp();
            
            if (criticalSectionCount == 0)
            {
            	maxMessagesExchanged = 2*(Node.NUMNODES-1);
            	minMessagesExchanged = 2*(Node.NUMNODES-1);
            	
    			for(int i=0; i<Node.NUMNODES; i++)
    			{
    				if (i!=Node.nodeID)
    				{
    					try
    					{
    						Socket bs = Node.socketMap.get(Integer.toString(i));
    						PrintWriter writer = Node.writers.get(bs);
    			            writer.println("REQUEST,"+requestTS+","+Node.nodeID);
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
            		// self execution
            		minMessagesExchanged = 0;
            		
            		new Thread()
    				{
            		public void run(){
            		criticalSection = true;
            		long currentTS1 = TimeStamp.getTimestamp();
    	            IOH.log(requestTS,currentTS1,"entered");
		            java.util.Date date1= new java.util.Date();
		            long currentTS = TimeStamp.getTimestamp();
					System.out.println("CRITICAL SECTION:"+ criticalSectionCount +":"
		            +currentTS);
					
					timeElapsed = currentTS - requestTS;
					
					// Delay of 20 milliseconds
					try {
						Thread.sleep(20);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					IOH.report(" Messages:"+2*replyCount+" Time Elapsed:"+timeElapsed);
					
					// Reset this node
					criticalSection = false;
					requestCS = false;
					replyCount = 0;
					criticalSectionCount++;
					long currentTS2 = TimeStamp.getTimestamp();
		            IOH.log(requestTS,currentTS2,"exited");
					sendDeferredReplies();
					
					if (criticalSectionCount> 20 && Node.nodeID % 2 == 0)
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
					requestCriticalSection();
    				}
    				}.start();
					
				}
            	else
            	{
            		
            		for(String item: copyOfParticipants)
                	{
            			String Null = null;
        				if (Node.nodeID != Integer.parseInt(item))
        				{
        					try
        					{
        						Socket bs = Node.socketMap.get(item);
        						PrintWriter writer = Node.writers.get(bs);
        			            writer.println("REQUEST,"+requestTS+","+Node.nodeID);
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
			IOH.report(" Maximum Messages:"+maxMessagesExchanged
					+" Minimum Messages:"+minMessagesExchanged);

			if (Node.nodeID !=0)
			{
				Socket bs = Node.socketMap.get("0");
				PrintWriter writer = Node.writers.get(bs);
				writer.println("COMPLETE"+","+totalRequestsSent);
	            writer.flush();
			}
			else
			{
				nodeZeroCompletetion = true;
				if (nodeCompletetionCount == Node.NUMNODES-1)
				{
					System.out.println("ALLLLLL OVERRRRR:"+totalRequestsSent);
					IOH.report("TOTAL MESSAGES:"+totalRequestsSent);
					/*try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					Node.broadcast("HALT");
					Node.closeSockets();
				}
			}
		}
	}
	
	public void sendDeferredReplies()
	{
		System.out.println("Sending deferred replies");
		// TODO
		for(int i=0; i<deferred.size(); i++)
		{
			String deferredNode = deferred.get(i);
			System.out.println("Sending deferred replies to:"+deferredNode);
			try
			{
				Socket bs = Node.socketMap.get(deferredNode);
				PrintWriter writer = Node.writers.get(bs);
				writer.println("REPLY"+","+Node.nodeID);
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
	
	public synchronized void checkCS()
	{
		//if (RicartAgrawala.replyCount == Node.NUMNODES-1)
			if ((criticalSectionCount == 0 && 
					replyCount == Node.NUMNODES-1) || 
					replyCount == participantsCount)
			{
				criticalSection = true;
				totalRequestsSent += (2*replyCount);
				if (2*replyCount < minMessagesExchanged)
				{
					minMessagesExchanged = 2*replyCount;
				}
	            
				/*java.util.Date date= new java.util.Date();
	            Timestamp currentTS1 = new Timestamp(date.getTime());*/
				
				long currentTS1 = TimeStamp.getTimestamp();
	            IOH.log(requestTS,currentTS1,"entered");

				System.out.println("CRITICAL SECTION:"+ criticalSectionCount +":"
	            +currentTS1);
				
				timeElapsed = currentTS1 - requestTS;
				
				// Delay of 20 milliseconds
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				IOH.report(" Messages:"+2*replyCount+" Time Elapsed:"+timeElapsed);
				
				// Reset this node
				criticalSection = false;
				requestCS = false;
				replyCount = 0;
				criticalSectionCount++;
				
				/*java.util.Date date1= new java.util.Date();
				Timestamp currentTS2 = new Timestamp(date1.getTime());*/
				
				long currentTS2 = TimeStamp.getTimestamp();
				IOH.log(requestTS,currentTS2,"exited");
				sendDeferredReplies();
				
				if (Node.nodeID % 2 == 0 && criticalSectionCount > 20)
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
				requestCriticalSection();
			}
	}
	
	public synchronized void incrementCount() {
		replyCount++;
    }
}
