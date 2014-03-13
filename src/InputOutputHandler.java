import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputOutputHandler
{
	public int totalNodes=0;
	public Map<String, List<String>> map = new HashMap<String, List<String>>();
	
	public int readConfig()
	{
		try (BufferedReader br = new BufferedReader(new FileReader("config.txt")))
		{
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null)
			{
				if (!sCurrentLine.startsWith("#"))
				{
					String[] tokens = sCurrentLine.split(" ");
					if(tokens[1].equals("#"))
					{
						totalNodes = Integer.parseInt(tokens[0]);
						System.out.println(totalNodes);
					}
					else
					{
						List<String> valueList = new ArrayList<String>();
						valueList.add(tokens[1]);
						valueList.add(tokens[2]);
						map.put(tokens[0], valueList);
					}
				}
			}
			
			// Testing the HashMap output
			for (Map.Entry<String, List<String>> entry : map.entrySet())
			{
				String key = entry.getKey();	
				List<String> values = entry.getValue();							
				System.out.println("Key = " + key);
				System.out.println("Values = " + values);
				// get(o) is host get(1) is port
				//System.out.println("Values = " + values.get(0));
				//System.out.println("Values = " + values.get(1));
			}
	
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return totalNodes;
	}
	
	public synchronized void log(long rq, long ts, String status)
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
	
	public synchronized void report(String status)
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
