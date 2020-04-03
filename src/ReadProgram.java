import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadProgram {
	
	public void readProg() throws IOException
	{
		//read line by line 
		//then seperate this line when you see a space 
		//then read each string character by character 
		File file = new File("program.txt");
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		String tempString = new String();
		while((tempString=buffer.readLine())!=null)
		{
			String[] splitIt = tempString.split(" ");
			//handle each String --Seperated by space 
			for(int i=0;i<splitIt.length;i++)
				handle(splitIt[i]);
		}	
	}

    public void handle(String exp)
    {
    	//read this expression character by character 
    	//
    	//1---> We reach the end of the expression having a final node ,, then we add this normally to the file
    	//2-->We reach the end of the expression but we dont reach a final node ,, then it is an error
    	//3--> we reach the end of the expression but the node we are at is NOT a final node , 
    	//BUT we already reached a final node before that then we add the expression till this
    	// THIS FINAL NODE then handle the rest of the expression
    	
    }
}