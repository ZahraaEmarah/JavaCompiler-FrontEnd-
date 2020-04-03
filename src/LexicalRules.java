import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LexicalRules {
	
	ArrayList<String> regExp = new ArrayList<String>();
	ArrayList<String> regDef = new ArrayList<String>();
	ArrayList<String> keywords = new ArrayList<String>();
	ArrayList<String> punctuations = new ArrayList<String>();
	NFA nfa ;
	RegularDefinition regDefinition = new RegularDefinition();
	
	public void readFile() throws IOException
	{
		//reading the txt file to fill up the regular Expressions and regular Definitions
		File file = new File("LexicalRules.txt");
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		String tempString = new String();
		while ((tempString=buffer.readLine())!= null) {
		   if(tempString.startsWith("["))
				//handle the Punctuations
				punctuations.add(tempString);
			else if(tempString.startsWith("{")) 
				//Handle the key words
				keywords.add(tempString);
			else if(tempString.contains(":"))
				//This is an expression 
				regExp.add(tempString);
			else if (tempString.contains("="))
				//this is a definition
				regDef.add(tempString);
			
			}
		//by having all the informations we can now build the NFA 
		buildNFA();
		}
	private void buildNFA()
	{
		System.out.println("Building the NFA...");
		
		//start node is comon to all the expressions
		//loop on all the expressions in the array list , build each NFA with same start 
		//Split the expressions 
		for(int i = 0 ; i<regDef.size();i++)
		{
			//represent each regular definition with a symbol 
			//which is a letter from it , make it Capital letter 
			//we need to substitute the regular expression with this symbol 
			String temp = regDef.get(i);
			String[] temp2 = temp.split("=");
			regDefinition.name(temp2[0] , temp2[1]);
		}
		regDefinition.endNames();
		nfa = new NFA(regDefinition);
		for(int i=0;i<regExp.size();i++)
		{
			//split the expression when you see ":" 
			// the name of this expression LHS will be the name of it's NODE --FINISH STATE--
			String temp = regExp.get(i);
			String[] temp2 = temp.split(":");
			//build the NFA for this expression
			if(temp2.length>2)
			{
				for(int j=2;j<temp2.length;j++) {
					
					temp2[1] = temp2[1]  + ':' + temp2[j];			
				}
			}
			
			temp2[1]=regDefinition.contain(temp2[1]);
		    nfa.buildNFA(temp2);
		}
		
		
		//Add the keywords to the NFA
		
		for(int i=0;i<keywords.size();i++)
		{
			//split the expression when you see ":" 
			// the name of this expression LHS will be the name of it's NODE --FINISH STATE--
			String temp = keywords.get(i);
			temp=temp.replace("{", "");
			temp=temp.replace("}", "");
			String[] temp2 = temp.split(" ");
			for(int i1=0;i1<temp2.length;i1++)
			nfa.keywords(temp2[i1]);
			
	     }
		for(int i=0;i<punctuations.size();i++)
		{
			String temp = punctuations.get(i);
			temp = temp.replace("[", "");
			temp = temp.replace("]", "");
			temp = temp.replace("\\", "");
			String[] temp2 = temp.split(" ");
			for(int j=0; j<temp2.length;j++)
				nfa.keywords(temp2[j]);
		}
		
		nfa.printTransTable();
	
}
}