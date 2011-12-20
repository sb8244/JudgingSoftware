package packet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class SourcePacket extends Packet
{
	String language;
	String problemNumber;
	String fileContents = "";
	String fileName;
	
	public SourcePacket(String p, File f) throws FileNotFoundException
	{
		super(null, null, null);
		problemNumber = p;
		
		StringTokenizer splitInfo = new StringTokenizer(f.getName(), ".");
		fileName = splitInfo.nextToken();
		String nextToken = splitInfo.nextToken();
		if(nextToken.toLowerCase().equals("java"))
		{
			language = "java";
		}
		Scanner fileScanner = new Scanner(f);
		while(fileScanner.hasNextLine())
		{
			fileContents += fileScanner.nextLine() + "\n";
		}
	}
	
	public String getProblemName()
	{
		return problemNumber;
	}
	
	public String toString()
	{
		return "Language: " + language + "\tProblem: " + problemNumber
		+ "\tFile Name: " + fileName;
	}
	
	public String getFileInformation()
	{
		return fileName + ":\n" + fileContents;
	}
	
	public String getSource()
	{
		return fileContents;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public String  getLanguage()
	{
		return language;
	}
}
