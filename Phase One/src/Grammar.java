
public class Grammar {
	
	//  #E(name) ---> "AB" (exp)
	private String first=new String();
	private String follow=new String();
	private String name =new String();
	private String exp = new String();
	int eps;
	
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
		String split[] = add.split(" ");
		for(int i=0;i<split.length;i++)
		{
			if(!first.contains(split[i]))
				first = first + add +" " ;
		}
		
		first=first.replaceAll("( +)"," "); //replace the non necessary spaces
	}
	
	public void addFollow(String add)
	{	
		String split[] = add.split(" ");
		
		for(int i=0;i<split.length;i++)
		{
			if(!follow.contains(split[i]))
				follow = follow + add +" " ;
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
		return name;
	}
	public void addEps()
	{
		eps++;
		if(eps>1) {
			addFirst("~");
			eps=0;
		}
	}

}
