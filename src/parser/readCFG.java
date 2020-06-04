package parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class readCFG {
	ArrayList<String> CFG = new ArrayList<String>();
	ReadTokens read;
	ParseTable parse;
	public String console = "";
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
		int identify =0;
		int identify2 = 0;
		
		while((tempString=buffer.readLine())!=null)
		{
			tempString = tempString.replace("\\L", "~");
			if(tempString.charAt(0) == '#') {
			newTemp = tempString;
			identify = identifyLeftRecursion(tempString,-1);
			if(identify == 0)
			identify2 = identifyLeftFactoring(tempString,-1);
			if(identify ==0 && identify2 == 0) {
			CFG.add(tempString);
			i++;
			}
			else
				i=i+2;
			}
			else {
			newTemp = newTemp + tempString;
			int check2 =0;
			if(identify == 1 || identify2 == 1)
				i--; 
			int check1=identifyLeftRecursion(newTemp,i);
			
			check2 = identifyLeftFactoring(newTemp,i);
			if(check1 == 0 && check2 ==0)
			CFG.set(i,newTemp);
			else 
				i++;
			
			}
		}
		buffer.close();
		
		parse = new ParseTable(CFG);
		if(parse.isLL == 1)
		read = new ReadTokens(parse,parse.startNonTerminal); 
		console = read.con;
		
	}
	private int identifyLeftFactoring(String exp,int j)
	{
		String split[] = exp.split("::=");
		String name = split[0];
		String expression = split[1];
		ArrayList<String> store = new ArrayList<String>();
		String[] split2 = expression.split("\\|");
		for(int i=0;i<split2.length;i++)
		{
			String similar = checkSimilar(store,split2[i]);
			if(!similar.equals(" "))
			{
				System.out.println("There is left factoring in " + exp);
				name = name.replace("#", "");
				removeLeftFactoring(name,split2,j , similar);
				return 1;
			}
			store.add(split2[i]);
		}
		return 0;
		
	}
	private void removeLeftFactoring(String name , String[] exp,int j ,String similar)
	{
		String name2 = " " + name.replace(" ", "") +"DASH" + " ";
		String newExp = ""; //Store for new exp
		String temp = ""; //Store for the exp
		int eps=0;
		//removes left Factoring for common 
		for(int i=0;i<exp.length;i++)
		{
			
			if(exp[i].startsWith(similar))
			{
				//new exp
				
				exp[i] = exp[i].replaceFirst(similar, " ");
				
				if(exp[i].equals(" "))
					eps=1;
				else	
				newExp = newExp + "|" + exp[i];
				 
			}
			else {
				//add to the old exp
				temp = temp + "|" + exp[i];
			}
		}
		
		temp = temp + "|" + " "+similar + name2 ;
		temp = temp.replaceAll("\\s+"," ");
		
		if(eps==1)
			newExp = newExp + "|" + " ~ ";
		newExp = newExp.replaceFirst("\\|", "");
		temp = temp.replaceFirst("\\|", "");
		newExp = newExp.replaceAll("\\s+"," ");
		String new1 = makeExpression(name,temp);
		String new2 = makeExpression(name2 , newExp);
		if(j==-1) {
			
		CFG.add(new1);
		CFG.add(new2);
		}
		else {
			CFG.set(j, new1);
			j++;
			if(CFG.size() == j)
			CFG.add(new2);
			else {
				CFG.set(j, new2);
			}
				
		}
		
	}
	private String checkSimilar(ArrayList<String> store,String exp)
	{
		String[] compare = exp.split(" ");
		
		compare[1] =" "+compare[1] + " ";
		for(int i=0;i<store.size();i++)
		{
			String temp = store.get(i);
			if(temp.startsWith(compare[1]))
			{
				temp = temp.replace(compare[1], "");
				
				if(temp.startsWith(compare[2]))
					return compare[1] + compare[2] +" ";
				return compare[1];
			}
		}
		return " ";
	}
	private int identifyLeftRecursion(String exp,int j)
	{
		String[] split = exp.split("::=");
		
		String compare = split[0];
		String expression = split[1];
		
		compare = compare.replace("#", ""); 
		//split the expression when (|)
		String[] split2 = expression.split("\\|");
		for(int i=0;i<split2.length;i++)
		{
			String temp = split2[i];
			
			if(temp.startsWith(compare))
			{
				System.out.println("There is a left recursion in " + exp);
				removeLeftRecursion(compare,split2,j);
				return 1;	
			}
		}
		return 0;
	}
	private void removeLeftRecursion(String name,String[] exp,int j)
	{
		String temp ="";
		String newExp = "";
		String name2 = name.replace(" ", "");
		name2 = " " + name2 + "DASH" + " ";
		for(int i=0;i<exp.length;i++)
		{
			if(exp[i].startsWith(name))
			{
				exp[i] = exp[i].replace(name, " ");
				newExp = newExp+"|" + exp[i] + name2 + " ";
			}
			else {
				
				temp =temp+ "|" + exp[i]+ name2+ " ";
			}
		}
		
		newExp = newExp.replaceFirst("\\|", "");
		temp = temp.replaceFirst("\\|", "");
		
		String new1 = makeExpression(name,temp);
		newExp = newExp + "| ~ ";
		String new2 = makeExpression(name2 , newExp);
		if(j==-1) {
		CFG.add(new1);
		CFG.add(new2);
		}
		else {
			CFG.set(j, new1);
			j++;
			if(CFG.size() == j)
			CFG.add(new2);
			else {
				CFG.set(j, new2);
			}
				
		}
				
	}
	private String makeExpression(String name,String exp)
	{
		String temp = "#" + name + "::=" + exp;
		return temp;
	}
	

}