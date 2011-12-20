import java.util.Scanner;
public class OneOFE
{
	
	public OneOFE()
	{
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine())
		{
			System.out.println(in.nextLine() + "\t");
		}
	}
	public static void main(String[] args)
	{
		OneOFE main = null;
		main = new OneOFE();
	}
	
}