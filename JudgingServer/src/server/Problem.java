package server;

import java.io.File;

/**
 * A Problem has an input file, output file, and name
 * @author Stephen Bussey
 *
 */
public class Problem 
{
	private File inputFile, outputFile;
	private String problemName;
	
	/**
	 * Create a new problem given its name
	 * The input and output must be in the format name.in and name.out
	 * @param name The name of the problem
	 */
	public Problem(String name)
	{
		problemName = name;
		inputFile = new File(name+".in");
		outputFile = new File(name+".out");
	}
	
	/**
	 * 
	 * @return true if both an input and output file exist
	 */
	public boolean isValid()
	{
		return inputFile.exists() && outputFile.exists();
	}
	
	/**
	 * 
	 * @return This problem's name
	 */
	public String getName()
	{
		return problemName;
	}
	
	/**
	 * 
	 * @return The input file that will be used on this problem
	 */
	public File getInputFile()
	{
		return inputFile;
	}
	
	/**
	 * 
	 * @return The output file that will be used on this problem
	 */
	public File getOutputFile()
	{
		return outputFile;
	}
}
