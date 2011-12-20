package packet;

public class Packet 
{
	public ProblemPacket problemPacket;
	public ResultPacket resultPacket;
	public String type;
	
	public Packet(ProblemPacket problem, ResultPacket resultPacket, String t)
	{
		type = t;
		problemPacket = problem;
		this.resultPacket = resultPacket;
	}
	
	public Packet getPacket()
	{
		if(problemPacket != null)
			return problemPacket;
		else if(resultPacket != null)
			return this.resultPacket;
		return null;
	}
}
