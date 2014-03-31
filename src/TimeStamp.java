import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TimeStamp {
	public synchronized static long getTimestamp()
	{
		// TS in long format, TimeStamp format
		// was generating error
		return new Date().getTime();
	}
	
	public static void main(String[] args)
	{
		// TEST CODE
		 System.out.println(new Date().getTime());
		 try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println(new Date().getTime());
		 ArrayList<String> lol = new ArrayList<String>();
		 lol.add("3");
		 lol.add("4");
		 System.out.println(lol.contains("3"));
	}
}
