import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TimeStamp {
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
	static Timestamp finalTS;
	public synchronized static long getTimestamp()
	{
		/*java.util.Date date= new java.util.Date();
        Timestamp currentTS = new Timestamp(date.getTime());
        try {
			Date date2 = dateFormat.parse(currentTS.toString());
			finalTS = new Timestamp(date2.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finalTS;*/
		return new Date().getTime();
	}
	
	public static void main(String[] args)
	{
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
