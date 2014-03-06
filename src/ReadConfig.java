import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadConfig
{
	public static int totalNodes=0;
	public static Map<String, List<String>> map = new HashMap<String, List<String>>();
	public static int main()
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
}
