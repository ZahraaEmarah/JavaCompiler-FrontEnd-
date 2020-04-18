import java.util.ArrayList;

public class ParseTable {
	Grammar grammar[];
	ArrayList<String> CFG = new ArrayList<String>();
	int grammarCounter ;
	String entriesPassed = "";
	ArrayList<String> nonResolved = new ArrayList<String>();
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
			String grammar = CFG.get(i);
			
			calculateFirst(grammar);
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
			calculateFollow(i);
		}
		for(int i=0;i<nonResolved.size();i++)
		{
			calculateFollow(Integer.parseInt(nonResolved.get(i)));
			nonResolved.remove(i);
		}
		System.out.println("*********************");
		printFollow();
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
						if(firstSplits[i].length()>1) {
						i--;
						}
						else
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

	private void calculateFollow(int index)
	{
		//check the expressions of each grammar then see if it is present in it
		//ex: calculateFollow(2) --> calculate follow of grammar[2]
		//for loop on ALL grammars to check if grammar[2].name is found in the expression 
		String check = grammar[index].getName();
		check = check + " ";
		String redo = check;
		int setRedo =0;
		int store=0;
		
		for(int i=0;i<CFG.size();i++)
		{
			String temp = grammar[i].getExpression();
			if(temp.contains(check) == true)
			{
				String split[] = temp.split(check);
				String found = " "; //this is intial state
			for(int j=1;j<split.length;j++) { 
				found = split[j];		
				if(setRedo == 1 && store == 0) {
					check = redo;
					setRedo =0;
				}
				if(Character.toString(found.charAt(0)).compareTo("'")== 0)// it is a terminal 
				{
					
					String splitGrammar[] = found.split("\\'");
					grammar[index].addFollow(splitGrammar[1]);
				}
				else if(found!=" " && Character.toString(found.charAt(0)).compareTo("|")!=0) { // non terminal --if the follow is non terminal take it's first 
					found=found.replaceAll("( +)"," ");
					split = found.split(" ");
					String first = grammar[findIndex(split[0])].getFirst();
					if(first.contains("~")) { //if there is an epsilon then remove this non terminal with the one after 
						first = first.replace("~", "");
						//then we remove the found non terminal
						if(split.length>1)
							store =1;
						else
							store =0;
						check = split[0] + " "; 
						i--;
						if(i== -1)
							i=0;
						setRedo = 1;
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
			if(found == " ")
			{
				if(grammar[i].getFollow().equals(" ")) {
					
					nonResolved.add(Integer.toString(index));
				}
				else
				grammar[index].addFollow(grammar[i].getFollow());
			}
			}
			
		}
		makeParseTable();
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
		//if multiple values then print error , return --Not LL(1)--
	}
}
