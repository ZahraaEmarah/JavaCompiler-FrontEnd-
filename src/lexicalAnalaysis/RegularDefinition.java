package lexicalAnalaysis;
import java.util.ArrayList;

public class RegularDefinition {
	private ArrayList<String> definitions = new ArrayList<String>();
	 ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> actualNames = new ArrayList<String>();
	
	public void name(String exp,String actualName)
	{
		int j=0;
		while(true) {
		if(!names.contains(Character.toString(Character.toUpperCase(exp.charAt(j))))) {
			actualName = actualName.replace(" ","");
			makeStringDef(actualName); 
			names.add(Character.toString(Character.toUpperCase(exp.charAt(j))));
			exp=exp.replace(" ", "");
			actualNames.add(exp);
			break;
		}
		else
		{
			//if there is a match in name 
			int i=names.size()-1;
			int found=0;
			String temp=actualName;
			while(i>=0) {
				
			if(actualName.contains(actualNames.get(i)))
			{
			  
			   temp=temp.replace(actualNames.get(i), names.get(i));
			   
				found=1;
			}
			i--;
			}
			if(found==1)
			{
				if(temp.contains("+"))
				{
					temp = removePlus(temp);
				}
			
				names.add(temp);
				exp = exp.replace(" ", "");
				actualNames.add(exp);
				return;
			}
			
		}
		j++;
		}
	}
	public void makeStringDef(String exp)
	{
		String[] temp = exp.split("\\|");
		String add="";
		for(int i=0;i<temp.length;i++) {
		String[] parse = temp[i].split("-");
		int start = parse[0].charAt(0);
		int end = parse[parse.length-1].charAt(0);
	    for(int j=start;j<=end;j++)
	    {
	    	add = add + (char)j;
	    }
	    
		}
		definitions.add(add);
	}
	
	public void endNames() //end with epsilon
	{
		//printDef();
		names.add("~");
		actualNames.add("\\L");
	}
	public String removePlus(String exp)
	{
		exp = exp.replace(" ", "");
		int last=0;
		if(exp.charAt(exp.length()-1) == '+') {
			last=1;
		}
		String[] t= exp.split("\\+");
		
		String combine= "";
		int i1;
		Character lastt;
	    for(i1=0;i1<t.length-1;i1++)
	    {
	    	lastt = t[i1].charAt(t[i1].length()-1);
	    	if(lastt == ')')
	    		lastt='\0';
	    	combine=combine + t[i1] + lastt+ "*";
	    }
	    lastt = t[i1].charAt(t[i1].length()-1);
    	if(lastt == ')')
    		lastt='\0';
	    if(i1 <= 0) {
	    	combine +=t[i1] +lastt+ "*";
	    }
	    else if(last == 1)
	    	combine = combine + t[i1] + lastt+ "*";
	    else if (last == 0)
	    	combine = combine + t[i1];
	    return combine;
	}
	public String contain(String exp)
	{
		if(exp.contains("E"))
			exp = exp.replace("E", "^");
		for(int i=actualNames.size()-1;i>-1;i--)
		{
			if(exp.contains(actualNames.get(i)))
			{
				
				exp = exp.replace(actualNames.get(i), (names.get(i)));
				
			}
		}
		//we also change any thing that has (+) to (character)(character)*
		if(exp.contains("+") && !exp.contains("\\+"))
		{
			
		    exp = removePlus(exp);
		}
		//remove the exponent to be (^)
		
		return exp;
	}
	public String getDefinition(Character letter)
	{
		
		String temp ="";
		
		if(letter == '~')
			return temp;
		for(int i=0;i<actualNames.size();i++)
		{	
			if(names.get(i).charAt(0) == letter) 
				return definitions.get(i);
		}
		return temp;
	}

}