
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
	public void addStart(Character val)
	{
		name = 0;
		next[index] = new Node();
		value[index] = val;
		index++;
	}
	public Node getNext()
	{
		Node temp = next[--index];
		index++;
		return temp;
	}
	
}
