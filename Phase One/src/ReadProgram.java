import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadProgram {
	DFA_minimization dfa ;
	FileWriter output;
	
	public ReadProgram(DFA_minimization dfa) throws IOException
	{
		this.dfa = dfa;
		readProg();
	}
	
	public void readProg() throws IOException
	{
		//read line by line 
		//then seperate this line when you see a space 
		//then read each string character by character 
		File file = new File("program.txt");
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		//open output file
		output = new FileWriter("output.txt");
		String tempString = new String();
		while((tempString=buffer.readLine())!=null)
		{
			String[] splitIt = tempString.split("\\s+");
			//handle each String --Seperated by space 
			for(int i=0;i<splitIt.length;i++) {
		
				if(splitIt[i].length()!=0) {
					handle(splitIt[i],0);
				}
			}
		}
		output.close();
	}

    public void handle(String exp,int check) throws IOException
    {
    	
    	String[] get = new String[2];
    	get[0] ="0";
    	String finish = "";
    	String temp = "";
    	String replace = "";
    	for(int i=0;i<exp.length();i++) {
    		
		get = dfa.get_dfa(get[0], exp.charAt(i),check);
		
		temp += exp.charAt(i);
		if(get[1] !=" " && get[1]!="dead") {
			finish = get[1];
			
			replace = temp;
			
		}
		if(get[1] == "dead" && finish=="")
		{
			
			//go to another posibility 
			handle(exp,1);
			return;
			
		}
		else if(get[1] == "dead" || i== exp.length()-1) {
			//return to the last one not dead
			output_file(finish);
			//remove only the begining
			if(exp.length() == replace.length()) 
				return;
			
			if(replace.length()>1)
			exp = exp.replaceFirst(replace, "");
			else
			exp = exp.substring(exp.indexOf(replace.charAt(replace.length()-1)) +1);
		    
		    if(exp.length()==0) 
		    	return;
		    
		    handle(exp,0);
		    return;
		}
    	}
    	//read this expression character by character 
    	//
    	//1---> We reach the end of the expression having a final node ,, then we add this normally to the file
    	//2-->We reach the end of the expression but we dont reach a final node ,, then it is an error
    	//3--> we reach the end of the expression but the node we are at is NOT a final node , 
    	//BUT we already reached a final node before that then we add the expression till this
    	// THIS FINAL NODE then handle the rest of the expression	
    }
    private void output_file(String exp) throws IOException
    {
    	System.out.println("I AM WRITING IN FILE" + exp);
    	output.write(exp + "\n");
    	
    }
}