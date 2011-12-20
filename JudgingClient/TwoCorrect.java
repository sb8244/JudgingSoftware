import java.util.*;

public class TwoCorrect
{
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine())
		{
			StringTokenizer line = new StringTokenizer(in.nextLine(), " ");
			int sum = 0;
			while(line.hasMoreTokens())
			{
				sum += Integer.parseInt(line.nextToken());
			}
			System.out.println(sum);
		}
	}
}
