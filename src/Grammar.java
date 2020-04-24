import java.util.ArrayList;

public class Grammar {
	
	//  #E(name) ---> "AB" (exp)
	private String first=new String();
	private String follow=new String();
	private String name =new String();
	private String exp = new String();
	private ArrayList<String> ParseTableEntries = new ArrayList<String>();
	int eps;
	int entry;
	
	public Grammar()
	{
		first = " ";
		follow = " ";
		name = "";
		eps=0;
	}
	public void setName(String name)
	{
		this.name =name;
	}
	public void setExpression(String expression)
	{
		exp=expression;
	}
	public void addFirst(String add)
	{
		entry++; //used to add eps if all of the first has it
		String split[] = add.split(" ");
		for(int i=0;i<split.length;i++)
		{
			if(!first.contains(split[i]))
				first = first + split[i] +" " ;
		}
		
		first=first.replaceAll("( +)"," "); //replace the non necessary spaces
	}
	
	public void addFollow(String add)
	{	
		String split[] = add.split(" ");
		
		for(int i=0;i<split.length;i++)
		{
			if(!follow.contains(split[i])) {
				
				
				follow = follow + split[i] +" " ;
			}
		}
		
		follow=follow.replaceAll("( +)"," "); //replace the non necessary spaces
	}
	
	public String getFirst()
	{
		
		return first;
		
	}
	public String getFollow()
	{
		return follow;
	}
	public String getExpression()
	{
		return exp;
	}
	public String getName()
	{
		name = name.replaceFirst("# ", "");
		
		name = name.replace(" ", "");
		//name = " " + name;
		
		return name;
	}
	public void addEps()
	{
		eps++;
		
	}
	public void checkEps()
	{
		System.out.println(eps + " " + entry);
		if(eps == entry) {
			addFirst("~");
			eps=0;
		}
	}
	public void intializeParseTableEntries(int sizeOfTerminals)
	{
		for(int i=0;i<sizeOfTerminals;i++)
			ParseTableEntries.add("none");

	}
	public int addEntry(int index,String exp)
	{
		if(ParseTableEntries.get(index).equals("none")) {
		ParseTableEntries.set(index, exp);
		return 0; // no error
		}
		return 1; //if there is an error
	}
	public String getEntry(int index)
	{
		return ParseTableEntries.get(index);
	}

}
