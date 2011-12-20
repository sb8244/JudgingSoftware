package packet;

import packet.results.*;

public class ResultPacket extends Packet
{
	Result result = null;
	
	public Result getResult()
	{
		return result;
	}
	
	public ResultPacket(Result re)
	{
		super (null, null, null);
		result = re;
	}
	
	public String toString()
	{
		return result.getMessage();
	}
	
	public boolean isCompilationError()
	{
		return result instanceof CompilationResult;
	}
	
	public String getCompilationErrors()
	{
		if(isCompilationError())
		{
			return result.getMessage();
		}
		return null;
	}
	
	public boolean isCorrect()
	{
		return result instanceof CorrectResult;
	}
	
	public boolean isOutputFormatError()
	{
		return result instanceof OutputFormatResult;
	}
	
	public boolean isRuntimeError()
	{
		return result instanceof RunResult;
	}
	
	public boolean isWrongAnswer()
	{
		return result instanceof WrongAnswerResult;
	}
}
