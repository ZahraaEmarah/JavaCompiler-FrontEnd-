import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class readCFG {
	ArrayList<String> CFG = new ArrayList<String>();
	public readCFG() throws IOException
	{
		readProg();
	}
	private void readProg() throws IOException
	{
		//read line by line 
		//then separate this line when you see a space 
		//then read each string character by character 
		File file = new File("CFG.txt");
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		//open output file
		String tempString = new String();
		int i=-1;
		String newTemp ="";
		while((tempString=buffer.readLine())!=null)
		{
			tempString = tempString.replace("\\L", "~");
			if(tempString.charAt(0) == '#') {
			newTemp = tempString;
			CFG.add(tempString);
			i++;
			}
			else {
			newTemp = newTemp + tempString;
			CFG.set(i,newTemp);
			}
			
		}
		
		buffer.close();
		ParseTable parse = new ParseTable(CFG);
		ReadTokens read = new ReadTokens(parse,parse.startNonTerminal);
		
	}
	

}