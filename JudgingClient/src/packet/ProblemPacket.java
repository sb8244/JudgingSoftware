package packet;

import java.util.ArrayList;

public class ProblemPacket extends Packet
{
	ArrayList<String> problems;
	
	public ProblemPacket(ArrayList<String> problemNames)
	{
		super(null, null, null);
		problems = problemNames;
	}
	
	public ArrayList<String> getProblemNames()
	{
		return problems;
	}
	
	public String toString()
	{
		return "ProblemPacket";
	}
}
