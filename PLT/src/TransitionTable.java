
public class TransitionTable {
	Character[] inputs;
	int index;
	//array of epsilon transitions of each state 
	
	String[][] transition;
	
	public TransitionTable()
	{
		inputs = new Character[100];
		transition = new String[100][100];
	}
	public void addInput(Character add)
	{
		//check for duplicates first
		if(add == '~')
			return; 
		for(int i=0 ; i<index;i++)
		{
			if(inputs[i] == add)
				return;
		}
		inputs[index] = add;
		index++;
	}
	public void endInput() //FOR NFA --epsilon--
	{
		inputs[index] = '~';
		index++;
	}
	public Character[] printInputLine()
	{
		return inputs;
	}

	
	public void buildTable(Node[] node,int nodeNum)
	{
		
		
		for(int i=0;i<nodeNum;i++)
		{
			node[i].intialArray(index);
			
			for(int j=0;j<index;j++)
			{
				
				for(int k=0;k<node[i].index;k++)
				{
					
					if(node[i].value[k] == inputs[j])
					{
     						node[i].addInput(j,k, node[i].next[k].name);
					}
				}
				
					
			}
		}
	}

}