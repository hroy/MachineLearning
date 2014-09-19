import java.io.BufferedReader;
import java.io.InputStreamReader;

//1.txt 2.txt output.txt
public class ID3Main {

	public static String splitChar = " ";
	public static int maxDistinctFeatures = 5;
	public static int iValue = -1;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String userInput = "";
			
			do
			{
				userInput = br.readLine();
			}while(!userInput.equalsIgnoreCase("MyProgram"));
			
			System.out.println("Enter names of the files dataset input-partition output-partition");
			
			while(true)
			{
				userInput = br.readLine();
				if(userInput.equalsIgnoreCase("done")) return;
				
				while((userInput.split(ID3Main.splitChar)).length!=3)
				{
					System.out.println("Please mention three file names.");
					userInput = br.readLine();
				}
				
				ID3Tree dTree = new ID3Tree();
				if (!dTree.buildTree(userInput.split(ID3Main.splitChar)))
				{
					System.out.println("Error in building decision tree.");
				}
			}
			
		}
		catch(Exception ex)
		{
			System.out.println("Input Error! Please try again.");
		}
	}

}
