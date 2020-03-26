
public class Node {
	Node[] next; //Directed , and it may have several next states
	Character[] value; //Value on the arrow 
	int index;
	//each node I am directed to should have a value on it's arrow
	//next[0] ---> the value on the arrow is value[0] next[0] = node[1]
	//next[1] ---> the value on the arrow is value[1] and so on  
	 boolean finishState;
	 int name ; //we name the nodes by numbers by incrementing this variable
	 String langName; //in the finish state there will be a name for the language
	 int inputsTable[][];
	
	//[0] next --> NODE[1] 
	 //   value ARROW 0 --> ~  
	public Node()
	{
		this.finishState = false;
		next = new Node[100];
		value = new Character[100];
		index=0;
		name = -1;
	}
	public void nameIt(int n)
	{
		name = n ;
	}
	public void addArrow(Character val)
	{
		next[index] = new Node();
		value[index] = val;
		index++;
		
	}
	public void finish(String fname)
	{
		this.finishState=true;
		langName = fname;
		
		
	}
	public void addStart(Character val ,Node node)
	{
		name = 0;
		next[index] = node;
		value[index] = val;
		index++;
	}
	public Node getNext()
	{
		Node temp = next[--index];
		index++;
		return temp;
	}
	public void intialArray(int inputNum )
	{
		int in = index;
		if(in <= 0) {
			
			in = 1;
			
		}
		inputsTable = new int[inputNum][in];
		
		for(int i=0;i<inputNum;i++)
		{
			for(int j=0;j<in;j++)
				inputsTable[i][j] = -1;
		}
	}
	public void addInput(int i , int j,int node)
	{
		inputsTable[i][j] = node;
	}
	//////////////////////////////////////
	////// node[0]  inputsTable[0][0] = -1
	////// node[0]  inputsTable[28][0] = 1   inputsTable[28][1] = 5
	
	public int getInput(int i , int j)
	{
		return inputsTable[i][j];
		
	}
}
