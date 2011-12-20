import java.util.Scanner;
public class OneCorrect
{
	
	public OneCorrect()
	{
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine())
		{
			System.out.println(in.nextLine());
		}
	}
	public static void main(String[] args)
	{
		OneCorrect main = null;
		main = new OneCorrect();
	}
	
}