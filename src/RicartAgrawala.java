import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;


public class RicartAgrawala {
	
	static Timestamp requestTS;
	static boolean requestCS;
	static int replyCount = 0;
	// TODO Algorithm Class
	public static void requestCriticalSection()
	{
		System.out.println(Server.nodeID+" ready to enter CS");
		RicartAgrawala.requestCS = true;
		for(int i=0; i<Server.NUMNODES; i++)
		{
			if (i!=Server.nodeID)
			{
				try
				{
					Socket bs = Server.socketMap.get(Integer.toString(i));
					PrintWriter writer = Server.writers.get(bs);
		            java.util.Date date= new java.util.Date();
		            requestTS = new Timestamp(date.getTime());
		            writer.println("REQUEST,"+requestTS+","+i);
		            writer.flush();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void requestCriticalSectionTo()
	{
		// TODO
	}
}
