public class RicartAgrawala 
{
	// Total number of nodes in the system
	public static int NUMNODES = 0;
	
	public static void main(String[] args)
	{		
		
		// User will let the node know its nodeID
		int nodeID = 0;
		if (args.length > 0)
		{
			try
			{
		    	nodeID = Integer.parseInt(args[0]);
		    }
			catch (NumberFormatException e)
			{
				System.err.println("Argument must be an integer");
				System.exit(1);
		    }
		}
		try 
		{
			// Bad way of reading config
			// TODO: Fix later.
			NUMNODES = ReadConfig.main();
		} 
		catch (Exception e)
		{
			//TODO add error handling
		}
	}
}
