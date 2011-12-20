package classRunner;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Loads a class given it's location
 * @author Stephen Bussey
 *
 */
public class MyClassLoader extends ClassLoader
{
	/**
	 * Attempt to load the class data
	 * @param name The name of the class
	 * @param location The location of the class 
	 * @return A Class Object referring to the invoked target class
	 */
	public Class<?> findClass(String name, String location)
	{
		byte[] b = null;
		try {
			b = loadClassData(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defineClass(name, b, 0, b.length);

	}

	private byte[] loadClassData(String location) throws Exception
	{
		File f = new File( location + ".class");
		DataInputStream is = new DataInputStream(new FileInputStream(location + ".class"));
		int len = (int)f.length();
		byte[] buff = new byte[len];
		is.readFully(buff);
		is.close();
		return buff;
	}
}

