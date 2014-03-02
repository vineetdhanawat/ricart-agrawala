import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Date;
import java.util.Random;


public class RicartAgrawala {
	
	static Timestamp requestTS;
	static int criticalSectionCount = 0;
	static boolean requestCS;
	static boolean criticalSection = false;
	static int replyCount = 0;
	
	public static ArrayList<String> deferred = new ArrayList<String>();
	
	// TODO Algorithm Class
	public static void requestCriticalSection()
	{
		if (criticalSectionCount < 20)
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
		}
		//clearing arraylist for reuse
		deferred.clear();
	}
}
