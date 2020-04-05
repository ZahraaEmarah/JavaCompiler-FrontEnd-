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
		//by initializing this class the whole process of seperating the input program to tokens begins
		this.dfa = dfa;
		readProg();
	}
	
	private void readProg() throws IOException
	{
		//read line by line 
		//then separate this line when you see a space 
		//then read each string character by character 
		File file = new File("program.txt");
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		//open output file
		output = new FileWriter("output.txt");
		String tempString = new String();
		while((tempString=buffer.readLine())!=null)
		{
			String[] splitIt = tempString.split("\\s+");
			//handle each String --Separated by multiple spaces or tabs 
			for(int i=0;i<splitIt.length;i++) {
		
				if(splitIt[i].length()!=0) {
					handle(splitIt[i],0);
				}
			}
		}
		output.close();
	}

    private void handle(String exp,int check) throws IOException
    {
    	//read this expression character by character 
    	//1---> We reach the end of the expression having a final node ,then we add this normally to the file
    	//2-->We reach the end of the expression but we don't reach a final node 
    	//, then check if there is another posibility (using Regular Definitions) check==1
    	//3--> we reach the end of the expression but the node we are at is NOT a final node , 
    	//BUT we already reached a final node before that then we add the expression till this
    	// THIS FINAL NODE then handle the rest of the expression by removing the expression that reached it's
    	//final state by using replaceFirst or if it's a special character we used substring
    	//4-->if after all of that we can't still find the token then print exp + error 
    	// if the symbol that is entered isn't found in the grammer then print exp+error
    	// if there is an error we continue with the other tokens normally 
    	
    	String[] get = new String[2];
    	get[0] ="0";
    	String finish = "";
    	String temp = "";
    	String replace = "";
    	for(int i=0;i<exp.length();i++) { 
    		
		get = dfa.get_dfa(get[0], exp.charAt(i),check);
		
		temp += exp.charAt(i); //String that has the expression till we find the new FINISH state.
		if(get[1] == "error")
		{
			String printE = exp.charAt(i) + "error";
			output_file(printE);
			exp = exp.replace(Character.toString(exp.charAt(i)), "");
		}
		if(get[1] !=" " && get[1]!="dead") {
			finish = get[1];
			replace = temp;
		}
		if(get[1] == "dead" && finish=="")
		{
			//Check another possibility 
			handle(exp,1);
			return;
		}
		else if(get[1] == "dead" || i== exp.length()-1 ) {
			//return to the last one not dead
			output_file(finish);
			
			//remove only the beginning
			if(exp.length() == replace.length())  //reached the end
				return;
			
			if(replace.length()>1) //use replaceFirst if we are removing a string
			exp = exp.replaceFirst(replace, "");
			else //use substring if we are removing a special character 
			exp = exp.substring(exp.indexOf(replace.charAt(replace.length()-1)) +1);
		    
		    if(exp.length()==0) //reached the end 
		    	return;
		    //handle the rest of the expression
		    handle(exp,0);
		    return;
		}
		
    	}
    		
    }
    private void output_file(String exp) throws IOException //write in output file
    {
    	output.write(exp + "\n");
    	
    }
}