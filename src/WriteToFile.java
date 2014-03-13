import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;


public class WriteToFile {

	public synchronized static void log(long rq, long ts, String status)
	{
		try
		{
			File file = new File("ricartlog.txt");
			if (!file.exists())
			{
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
				bw.write("Node "+Node.nodeID+ " requested at "+ rq+ ":"+status + " cs:" + ts + "\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void report(String status)
	{
		try
		{
			File file = new File("ricartreport.txt");
			if (!file.exists())
			{
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
				bw.write("Node "+Node.nodeID+ status + "\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
