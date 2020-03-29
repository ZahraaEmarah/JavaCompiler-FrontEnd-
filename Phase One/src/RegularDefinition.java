import java.util.ArrayList;

public class RegularDefinition {
	String[] definition;
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> actualNames = new ArrayList<String>();
	
	public void name(String exp,String actualName)
	{
		int j=0;
		while(true) {
		if(!names.contains(Character.toString(Character.toUpperCase(exp.charAt(j))))) {
			names.add(Character.toString(Character.toUpperCase(exp.charAt(j))));
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
				actualNames.add(exp);
				return;
			}
			
			
			
		}
		j++;
		}
	}
	public void endNames() //end with epsilon
	{
		names.add("~");
		actualNames.add("\\L");
	}
	public String removePlus(String exp)
	{
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
		return exp;
	}

}
