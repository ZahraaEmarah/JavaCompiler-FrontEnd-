import java.util.Stack;

public class NFA {
	Node node[];
	int nodeNum;
	int finishNum;
	Stack<Character> stack;
	
	TransitionTable table;
	int num ;
	//these two integers are used for STAR
	int startNode;
	int finishNode;
	
	//int dontStart;
	//after adding all regular expressions to the Arraylist in LexicalRules
	//We need to combine them and make NFA for each of them
	//Note that all of them will have only ONE start node
	//but for each regular expression there will be a finish node(end node)
	//epsilon is represented by "~"
	
	public NFA()
	{
		nodeNum=1;
		node = new Node[200];
		node[0] = new Node();
		node[0].nameIt(0);
		finishNum=0;
		table = new TransitionTable();
	}
	//The function that starts building the NFA depending on the input regular expression
	public void buildNFA(String[] expression)
	{
		    num = nodeNum;
			handleAll(expression[1]);
			addFinalNode(expression[0],1);
			nodeNum++;
			node[0].addStart('~', node[num]);	
		
	}
	private void handleAll(String expression)
	{
		expression = expression.replace(" ", ""); //remove spaces
		stack= new Stack<Character>();
		int begin= 0;
		int openB = 0;
		int OR = 0;
		int push = 1;
		System.out.println(expression);
		for(int i=0;i<expression.length();i++)
		{
			push = 1;
			if(expression.charAt(i)=='(') {
				
				openB=1;
				begin = popStack(begin,OR,1);
				OR = 0;
				push = 0;
				
			}
			else if(expression.charAt(i)==')') {
				
				//pop till you find the closed bracket
				begin = popStack(begin,OR,1);
				OR=0;
				openB=0;
			}
			 if(expression.charAt(i) == '|')
				{
				    push = 1;
					OR = 1;	
				}
			 if(push == 1) {
     			 stack.push(expression.charAt(i));
			 }
		}
		if(!stack.empty()) 
			begin = popStack(begin,OR,0);
		
	}
	private int popStack(int begin,int OR,int bracket) //if bracket == 1 then it will begin from the start
	{
		if(stack.empty() == true)
			return begin;
		String temp = "";
		
		while(!stack.empty())
		{
			//AND
			String t = Character.toString(stack.pop());
			if(!t.contains(")") == true) 
			temp=temp+ t;
		}
		
		temp = reverseString(temp);
		
		if(temp.length() <=0)
			return begin;
		int s=0;
		if(temp.charAt(0) == '*')
		{
			repeatStar();
			if(temp.length()==1)
			return begin;
			s=1;
		}
			
		
		
		startNode = nodeNum;
		if(OR == 1)
		{
			String[] arr = new String[2];
			arr[0] = "";
			arr[1] = temp;
			NFAOR(arr,0,begin);
			return 1;
		}
		int newNode=0;
		if(begin == 0)
		{
			begin=1;
			startNode=nodeNum;
			addNode(temp.charAt(0));
			s++;
			newNode=1;
		}
		finishNode = nodeNum;
		for(int t=s;t<temp.length();t++) {
		if(bracket == 0 && newNode==0) 
			startNode = nodeNum;
		newNode=0;
		addN(temp.charAt(t));
		finishNode = nodeNum;
		}
		finishNode = nodeNum;
		return begin;
	}
	private String reverseString(String reverse)
	{
		String temp = "";
		
		for(int i=reverse.length()-1 ; i>=0 ; i--)
			temp = temp + reverse.charAt(i);
		finishNode = nodeNum;
		return temp;
	}
	private void addNode(Character exp)
	{
		table.addInput(exp);
		node[nodeNum]=new Node();
		node[nodeNum].nameIt(nodeNum);
		node[nodeNum].addArrow(exp);
		Node temp = node[nodeNum++];
		node[nodeNum] = temp.getNext();
	}
	private void addFinalNode(String finish,int fin)
	{
		
		if(fin == 1)
		node[nodeNum].finish(finish); //if it is final state 
		finishNum = nodeNum;
		node[nodeNum].nameIt(nodeNum);
	}
	private void NFAOR(String[] expression,int fin,int begin)
	{
		//----------------- IN CASE OF HAVING AN EXPRESSION WITH OR -------
		//We will make the first node be the start node ALWAYS 
		int start= nodeNum;
		if(begin == 0) 
		addNode('~');
		else
		addN('~');
		//start node of them 
		finishNum=0;
		//we need to split the expression when we find "|"
		String exp = expression[1];
		String[] divide = exp.split("\\|");
		node[start].index--;
		int star;
		for(int i=0;i<divide.length ; i++)
		{
			//replace the "\" that is between the expressions RESERVED SYMBOLS 
			divide[i] = divide[i].replace("\\","");
			divide[i] = divide[i].replace(" ", "");
			int num = nodeNum;
			addNode(divide[i].charAt(0));
			node[nodeNum].nameIt(nodeNum);
			node[start].addStart('~' , node[num]);
			//System.out.println(node[start].next[1].name);
			//we need to check if its length is greater than 1 then this is S-->AB ---TIMES---
			if(divide[i].length() > 1) {
				//loop to make new nodes after each other 
				andNFA(divide[i] , 1);
				
			}
			node[nodeNum].nameIt(nodeNum);
			
			if(finishNum==0) { //comon finish node of them 
				node[nodeNum].addArrow('~');
				Node temp=node[nodeNum];
				nodeNum++;
				node[nodeNum] = temp.getNext(); //final node
				addFinalNode(expression[0],fin);
				
				nodeNum--;
				node[nodeNum].next[node[nodeNum].index] = node[finishNum];
				nodeNum = nodeNum+2;
			}
			else {
				node[nodeNum].addStart('~', node[finishNum]);
				nodeNum++;
			}
		}
		
		node[finishNum].addArrow('~');
		node[nodeNum] = node[finishNum].getNext();
		//this is used if this expression is repeated
		startNode = start;
		
		finishNode = finishNum;
		
		

	}
	private void andNFA(String exp,int s) //handles REPEAT --OR--
	{
		for(int j=s;j<exp.length();j++) {
			startNode=nodeNum;
			if(exp.charAt(j) == '*' && j+1<exp.length()) {
				repeatStar();
				j++;
			}
			addN(exp.charAt(j));
			finishNode=nodeNum;
		}
	}

	private void repeatStar() //FUNCTION THAT REPEATS 
	{
		if(startNode==finishNode)
			startNode--;
		node[finishNode].nameIt(finishNode);
		node[startNode].addStart('~',node[finishNode]);
		node[finishNode].addStart('~',node[startNode]);
	}
	private void addN(Character exp) //HANDLES REPEAT
	{
		if(exp == '*') {
			repeatStar();
			return;
		}
		table.addInput(exp);
		node[nodeNum].addArrow(exp);
		node[nodeNum].nameIt(nodeNum);
		Node temp = node[nodeNum++];
		node[nodeNum] = temp.getNext();
	}
	public void keywords(String keyword) //handles the keywords and punctuations
	{
		//build NFA for the keywords 
		num = nodeNum;
		node[nodeNum] = new Node(); 
		//fill the nfa char by char
		for(int i=0;i<keyword.length();i++)
		{
			table.addInput(keyword.charAt(i));
			node[nodeNum].addArrow(keyword.charAt(i));
			node[nodeNum].nameIt(nodeNum);
			Node temp = node[nodeNum];
			nodeNum++;
			node[nodeNum] = temp.getNext();	
			
		}
		//make the final node --ACCEPTANCE STATE--
		node[nodeNum].finish(keyword);
		node[nodeNum].nameIt(nodeNum);
		nodeNum++;
		node[0].addStart('~',node[num]);  // add the start at the end 
	}
	
	public void printTransTable()
	{
		
		table.endInput();
		table.buildTable(node, nodeNum);
		table.printInputLine();
		for(int i1=0;i1<nodeNum;i1++) {
		System.out.print("||"+i1+"||      ");
	    table.printTransitionTable(node[i1],nodeNum);
		}
	}
}
