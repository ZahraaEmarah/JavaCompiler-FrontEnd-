import java.util.ArrayList;

public class ParseTable {
	Grammar grammar[];
	ArrayList<String> CFG = new ArrayList<String>();
	int grammarCounter ;
	String entriesPassed = "";
	ArrayList<String> nonResolved = new ArrayList<String>();
	Terminals terminals[] = new Terminals[20];
	int sizeOfTerminals;
	public ParseTable(ArrayList<String> cfg)
	{
		CFG = cfg;
		//Each grammar will be in this class , having it's own First & Follow
		grammar = new Grammar[CFG.size()];
		grammarCounter = CFG.size();
		for(int i=CFG.size()-1;i>=0;i--)
		{
			grammarCounter--;
			grammar[grammarCounter] = new Grammar();
			String grammarT = CFG.get(i);
			calculateFirst(grammarT);
		}
		for(int i=0;i<nonResolved.size();i++)
		{
			grammarCounter = Integer.parseInt(nonResolved.get(i));
			calculateFirst(CFG.get(Integer.parseInt(nonResolved.get(i))));
			nonResolved.remove(i);
		}
		printFirsts();
		System.out.println("*********************");
		//after calculating first of each grammar now calculate the follow
		//The start will always have follow of $
		grammar[0].addFollow("$");
		for(int i=0;i<CFG.size();i++) {
			//Make the follow of each grammar 
			for(int j=0;j<CFG.size();j++)
			{
				String temp = grammar[j].getExpression();
				String split[] = temp.split("\\|");
				for(int k=0;k<split.length;k++) {
					String checkIt = " " + grammar[i].getName();
				if(split[k].contains(checkIt) == true)
				{
					//for loop at split (|)
					String check = grammar[i].getName();
					calculateFollow(i,split[k],check,j);
				}
				}
			
		}
		}
		for(int i=0;i<nonResolved.size();i++)
		{
			//calculateFollow(Integer.parseInt(nonResolved.get(i)));
			nonResolved.remove(i);
		}
		System.out.println("*********************");
		printFollow();
		entriesPassed="";
		makeParseTable();
		//How to get entry in the table by knowing the non terminal and the terminal
		System.out.println(getExpression("(","bexpr"));
	}
	private void calculateFirst(String grammarString)
	{
		//3 Cases 
		String firstTemp;
		String[] splitGrammar = grammarString.split("::=");
		grammar[grammarCounter].setName(splitGrammar[0]);
		firstTemp = splitGrammar[1];
		grammar[grammarCounter].setExpression(firstTemp);
		//we need to split when we see '|'
		String firstSplits[] = firstTemp.split("\\|");
		entriesPassed = entriesPassed + grammar[grammarCounter].getName() +" " +grammarCounter +" ";
		for(int i=0;i<firstSplits.length;i++)
		{
			//if it is a nonterminal add it to the first 
			String temp = firstSplits[i];
			
			temp=temp.replaceAll("( +)"," ");
			String getF[] = temp.split(" ");
			if(!temp.equals(" ")) {
			if(getF.length<=1)
				temp = getF[0];
			else
			temp = getF[1];
			if(temp.compareTo("~")==0)
				grammar[grammarCounter].addFirst("~");
			else if(grammar[grammarCounter].getName() != temp) 
			{
			if(Character.toString(temp.charAt(0)).compareTo("'")== 0)// it is a terminal 
			{
				splitGrammar = temp.split("\\'");
				grammar[grammarCounter].addFirst(splitGrammar[1]);
			}
			else//non terminal
			{
					splitGrammar = temp.split("\\'"); //make sure there is no terminal without space
					temp = splitGrammar[0]; 
					if(replaceChanges(grammarCounter,temp)==1)
					{
						firstSplits[i] = firstSplits[i].replace(temp, "");
						firstSplits[i]=firstSplits[i].replaceAll("( +)"," ");
						if(firstSplits[i].length()>1) {
						i--;
						}
						else if(firstSplits[i].compareTo(" ") == 0)
							grammar[grammarCounter].addFirst("~");
					}
					else if (replaceChanges(grammarCounter,temp)==-1) {
						nonResolved.add(Integer.toString(grammarCounter));
					}
			}
			}
			}
		}
		
	}
	private int replaceChanges(int i,String temp)
	{
		//this function is used to replace the non terminals in firsts
			if(entriesPassed.contains(temp)==true)
			{
				//change to it's first
				String spaceSplit[] = entriesPassed.split(" ");
				
				for(int j=0 ; j<spaceSplit.length;j++)
				{
					if(spaceSplit[j].compareTo(temp)==0)
					{
						int t = Integer.parseInt(spaceSplit[++j]);
						String first = grammar[t].getFirst();
						//grammar[grammarCounter].addFirst(first);
						if(!first.contains("~")) {
						grammar[grammarCounter].addFirst(first);
						return 0;
						}
						else {//handle if it has epsilon
							 first = first.replace("~", "");
							 grammar[grammarCounter].addEps();
							 grammar[grammarCounter].addFirst(first);
						     return 1;
						}
							
						
						
					}
				}
			}
			return -1;
	}
	private void printFirsts()
	{
		for(int i =0;i<CFG.size();i++)
		{
			System.out.println(grammar[i].getName()+" "+grammar[i].getFirst());
		}
	}

	private void calculateFollow(int index,String expression,String check,int i)
	{
		//check the expressions of each grammar then see if it is present in it
		//ex: calculateFollow(2) --> calculate follow of grammar[2]
		//for loop on ALL grammars to check if grammar[2].name is found in the expression 
		
		check = check + " ";
		
        /////////////////////////////
			String temp =expression;
			temp = " " + temp;
			temp=temp.replaceAll("( +)"," ");
				String split[] = temp.split(check);
				String found = " "; //this is intial state
				
			    
			    if(split.length==1)
			    	grammar[index].addFollow(grammar[i].getFollow());
			    
			   
				for(int j=1;j<split.length;j++) {
					
					 found = split[j];
					 
				if(Character.toString(found.charAt(0)).compareTo("'")== 0)// it is a terminal 
				{
					
					String splitGrammar[] = found.split("\\'");
					grammar[index].addFollow(splitGrammar[1]);
				}
				else if(found!=" " && Character.toString(found.charAt(0)).compareTo("|")!=0) { // non terminal --if the follow is non terminal take it's first 
					found=found.replaceAll("( +)"," ");
					String splitIt[] = found.split(" ");
					//splitIt[0] = " " + splitIt[0];
					String first = grammar[findIndex(splitIt[0])].getFirst();
					if(first.contains("~")) { //if there is an epsilon then remove this non terminal with the one after 
						first = first.replace("~", "");
						//then we remove the found non terminal
						String replace =found;
						replace = replace.replace(check, "");
						replace=replace.replaceAll("( +)"," ");
						grammar[index].addFollow(first);
						
						calculateFollow(index,found,splitIt[0] ,i);
						
					}
					grammar[index].addFollow(first);
				}
				else // take the follow of the grammar you are at
				{
					
					if(grammar[i].getFollow().equals(" ")) {
						
						nonResolved.add(Integer.toString(index));
					}
					else
					grammar[index].addFollow(grammar[i].getFollow());
				}
				}
				
			

		//makeParseTable();
	}
	private int findIndex(String name)
	{
		for(int i=0;i<CFG.size();i++)
		{
			if(name.compareTo(grammar[i].getName()) == 0)
				return i;
		}
		return -1;
	}
	private void printFollow()
	{
		for(int i =0;i<CFG.size();i++)
		{
			System.out.println(grammar[i].getName()+" "+grammar[i].getFollow());
		}
	}
	private void makeParseTable()
	{
		checkTerminals();
		intializeGrammarEntries();
		System.out.println("**************THE TABLE***************");
		//if multiple values then print error , return --Not LL(1)-- 
		for(int i=0;i<grammar.length;i++)
		{
			for(int j=0;j<sizeOfTerminals;j++)
			{
				String term = terminals[j].getValue();
				if(grammar[i].getFirst().contains(term)) {  
					
					String expression = grammar[i].getExpression();
					int checkError = addEntry(expression,term,i,j);
					if(checkError == 1)
					{
						System.out.println("This is NOT a LL(1) Grammar");
						return;
					}
					
				}
				if(grammar[i].getFirst().contains("~"))
				{
					//take the follow 
					if(grammar[i].getFollow().contains(term))
					{
						String expression = grammar[i].getName() + "->" + "~";
						int checkError =grammar[i].addEntry(terminals[j].getIndex(), expression); ;
						if(checkError == 1)
						{
							System.out.println("This is NOT a LL(1) Grammar");
							return;
						}
					}
				}
				
			}
		}
		//print the first line 
		printTerminals();
		//print the table 
		for(int i=0;i<grammar.length;i++) {
			System.out.print(grammar[i].getName() +"             ");
			for(int j=0;j<sizeOfTerminals;j++)
			{
				String exp = grammar[i].getEntry(j);
				if(exp == "none")
					exp =  exp + "      ";
				System.out.print("	"+exp + "	  "  );
			}
			System.out.println("");
		}
		
	}
	private int addEntry(String expression , String term,int i,int j)
	{
		// expression = grammar[i].getExpression();
	
		String split[] = expression.split("\\|");
		expression = checkSimilar(split,term);
		if(expression.equals(""))
			expression = grammar[i].getName() + "->" + grammar[i].getExpression();
		else {
			if(expression.startsWith("|"))
				expression = expression.replace("|", "");
			expression = grammar[i].getName() + "->" +" "+ expression;
		}
		int checkError = grammar[i].addEntry(terminals[j].getIndex(), expression);
		return checkError;
	}
	
	
	private String checkSimilar(String[] split,String terminal) {
		String expression = "";
		for(int i=0;i<split.length;i++)
		{
			String temp = split[i].replace("'", "");
			if(temp.contains(terminal)) {
				expression = expression +"|"+ split[i];
			}
		}
		expression = expression.replaceFirst("|", "");
		return expression;
	}
	private void checkTerminals()
	{
		sizeOfTerminals=-1;
		for(int i=0;i<grammar.length;i++)
		{
			String line = grammar[i].getExpression();
			if(line.contains("'"))
			{
				String split[] = line.split(" ");
				checkSimlarity(split);
			}
		}
		sizeOfTerminals++;
		terminals[sizeOfTerminals] = new Terminals(sizeOfTerminals,"$");
		sizeOfTerminals++;
	}
	private void checkSimlarity(String[] split)
	{
		for(int i=0;i<split.length;i++)
		{
			if(split[i].startsWith("'")) {
			
			String found = " " + split[i].replace("'", "")+" ";
			
			
			if(!entriesPassed.contains(found)) {
				sizeOfTerminals++;
				entriesPassed= entriesPassed + found ;
				terminals[sizeOfTerminals] = new Terminals(sizeOfTerminals,split[i].replace("'", ""));
			}
			}
		}
	}
	private void printTerminals() {
		System.out.print("NON TERMINALS       ");
		for(int i=0;i<sizeOfTerminals;i++){
			System.out.print(terminals[i].getValue() + "                    ");
		}
		System.out.println("  ");
	}
	private void intializeGrammarEntries()
	{
		
		for(int i=0;i<CFG.size();i++)
			grammar[i].intializeParseTableEntries(sizeOfTerminals);
	}
	public String getExpression(String terminal,String nonTerminal)
	{
		String expression="none";
		int indexTerminal = getTerminal(terminal);
		int indexNonTerminal = getNonTerminal(nonTerminal);
		expression = grammar[indexNonTerminal].getEntry(indexTerminal);
		return expression;
	}
	private int getTerminal(String terminal)
	{
		for(int i=0;i<sizeOfTerminals;i++)
		{
			if(terminals[i].getValue().equals(terminal))
				return terminals[i].getIndex();
		}
		return -1;
	}
	private int getNonTerminal(String nonTerminal) {
		for(int i=0;i<grammar.length;i++)
		{
			if(grammar[i].getName().equals(nonTerminal))
				return i;
		}
			return -1;
	}
}
