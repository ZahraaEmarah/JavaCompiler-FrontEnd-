
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
	public void printInputLine()
	{
		System.out.print("||STATE" + "||   ");
		for(int i=0;i<index;i++)
			System.out.print(inputs[i] + "  ");
	}

	public void printTransitionTable(Node node,int nodeNum) //print the transition table 
	{
		for(int i=0;i<nodeNum;i++)
		{
			System.out.println("");
			System.out.print("||"+i+"||      ");
			for(int j=0;j<index;j++)
			{
					System.out.print(transition[i][j] +"|");
			}
			
		}
		
	}
	public void intializeTransition(int nodeNum)
	{
		for(int i=0;i<nodeNum;i++)
		{
			for(int j=0;j<index;j++)
				transition[i][j]="_";
		}
	}
	public void buildTable(Node[] node,int nodeNum)
	{
		intializeTransition( nodeNum);
		for(int i=0;i<nodeNum;i++)
		{
			
			for(int j=0;j<index;j++)
			{
				
				for(int k=0;k<node[i].index;k++)
				{
					
					if(node[i].value[k] == inputs[j])
					{
						transition[i][j] += Integer.toString(node[i].next[k].name) + "_";
					}
				}
				
					
			}
		}
	}

}
