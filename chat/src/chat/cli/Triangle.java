package chat.cli;

public class Triangle {

	public static void main (String [] args)
	{
		char [][] coucou = new char[5][5];
		
		for(int i = 0; i < 5; ++i)
			for(int j = 0; j < 5; ++j)
				coucou[i][j] = '*';
			
		
		System.out.println("U|1|2|3|4|5|");
		for(int i = 0; i < 5; ++i)
		{
			System.out.print((i+1)+"|");
			for(int j = 0; j < 5-i; ++j)
				System.out.print(coucou[i][j] + "|");

			System.out.println();
			for(int j = 0; j < 5-i; ++j)
				System.out.print("__");
			System.out.println();
		}
	}
}
