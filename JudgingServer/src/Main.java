import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import server.Server;

/**
 * Runner for the server class
 * Must be run from the JDK JRE, not a distributable JRE
 * I find it easiest to just run from Eclipse, otherwise you must invoke the correct JRE by command line
 * 	You cannot set this JRE to be default, it is only meant to be invoked by hand
 * @author Stephen Bussey
 *
 */
public class Main 
{
	//default port is 1000, changed in the ServerProperties.prop file
	private static int port = 1000;
	private static String[] problems = null;
	
	/**
	 * Start the server
	 * @param args unused
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		try 
		{
			loadProperties();
			File tempFolder = new File("TempFiles");
			clearDir(tempFolder);
			tempFolder.mkdir();

			Server server = new Server(port, problems);	
			server.start();
			while(server.isAlive())
			{}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes all files and subdirectories under dir.
	 * @param dir
	 * @return true if all deletions were successful, false is any fail
	 */
	private static boolean clearDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = clearDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}

	/**
	 * Load the server properties:
	 * 	port
	 * 	each problem information
	 * @return The success of loading the properties
	 */
	private static boolean loadProperties()
	{
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("ServerProperties.prop"));
			port = Integer.parseInt(prop.getProperty("port"));
			problems = prop.getProperty("problems").split(" ");
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}
}
