package packet.results;

/**
 * Should be treated as an abstract class
 * 
 * Must remain a public class so that Gson can properly encode it
 * 
 * @author steve
 *
 */
public class Result
{
	String message;
	
	public Result(String m)
	{
		message = m;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String toString()
	{
		return getMessage();
	}
}
