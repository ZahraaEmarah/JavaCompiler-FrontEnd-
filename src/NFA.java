import java.util.Stack;

public class NFA {
	Node node[];
	int nodeNum;
	int finishNum;
	Stack star;
	TransitionTable table;
	DFA dfa;
	int num;
	// after adding all regular expressions to the Arraylist in LexicalRules
	// We need to combine them and make NFA for each of them
	// Note that all of them will have only ONE start node
	// but for each regular expression there will be a finish node(end node)
	// epsilon is represented by "ep"

	public NFA() {
		nodeNum = 1;
		node = new Node[100];
		node[0] = new Node();
		node[0].nameIt(0);
		finishNum = 0;
		table = new TransitionTable();

	}

	// The function that starts building the NFA depending on the input regular
	// expression
	public void buildNFA(String[] expression) {
		// if the expression has only one state
		// S-->a
		num = nodeNum;
		if (!expression[1].contains("*") == true && !expression[1].contains("(") == true
				&& !expression[1].contains("|") == true) {
			// add a simple node
			addNode(expression[1].charAt(0));
			andNFA(expression[1], 1);
			addFinalNode(expression[0]);
			nodeNum++;
			node[0].addStart('~', node[num]);
		}
		// if star,plus alone
		else if (expression[1].contains("\\*") == true || !expression[1].contains("(") == true) {
			NFAOR(expression); // OR and AND --We assume everything as
		}

	}

	private void addNode(Character exp) {
		table.addInput(exp);
		// node[0].addStart('~');
		node[nodeNum] = new Node();
		node[nodeNum].nameIt(nodeNum);
		node[nodeNum].addArrow(exp);
		Node temp = node[nodeNum++];
		node[nodeNum] = temp.getNext();
	}

	private void addFinalNode(String finish) {
		// make the start state point to the first node of this expression with value
		// epsilon

		node[nodeNum].finish(finish);
		finishNum = nodeNum;
		node[nodeNum].nameIt(nodeNum);
	}

	private void NFAOR(String[] expression) {
		// ----------------- IN CASE OF HAVING AN EXPRESSION WITH OR -------
		finishNum = 0;
		// we need to split the expression when we find "|"
		String exp = expression[1];
		String[] divide = exp.split("\\|");
		for (int i = 0; i < divide.length; i++) {
			// replace the "\" that is between the expressions RESERVED SYMBOLS
			divide[i] = divide[i].replace("\\", "");
			divide[i] = divide[i].replace(" ", "");
			num = nodeNum;
			addNode(divide[i].charAt(0));
			// we need to check if its length is greater than 1 then this is S-->AB
			// ---TIMES---
			if (divide[i].length() > 1) {
				// loop to make new nodes after each other
				andNFA(divide[i], 1);

			}
			node[nodeNum].nameIt(nodeNum);
			node[nodeNum].addArrow('~');
			if (finishNum == 0) {
				Node temp = node[nodeNum];
				nodeNum++;
				node[nodeNum] = temp.getNext(); // final node
				addFinalNode(expression[0]);
				nodeNum--;
				node[nodeNum].next[node[nodeNum].index] = node[finishNum]; // make the node point to finish state
				nodeNum = nodeNum + 2;
			} else {
				node[nodeNum++].next[0] = node[finishNum];
			}
			node[0].addStart('~', node[num]);
		}

	}

	private void andNFA(String exp, int s) // we dont add the finish state here
	{
		for (int j = s; j < exp.length(); j++) {
			table.addInput(exp.charAt(j));
			node[nodeNum].addArrow(exp.charAt(j));
			node[nodeNum].nameIt(nodeNum);
			Node temp = node[nodeNum++];
			node[nodeNum] = temp.getNext();
		}
	}

	private void starNFA(String[] exp) // we use the stack to determine which one has (*) on it
	{
		// note that the start node and finish node are connected by an arrow , epsilon
		// without brackets
		star = new Stack<Character>();
		int start = 0;
		int finish = 0;
		int i = 0;
		while (exp[1].charAt(i) != '*') {// push till you find (*)
			star.push(exp[1].charAt(i));
			i++;
		}
		// pop the expression then add this node as this is (*)
		// But we need to check the length of the stack to determine if we will add the
		// start node or not
		if (i <= 1)
			// add the start node
			addNode('~');

		else {
			start = nodeNum;
			node[nodeNum].addArrow('~');
			node[nodeNum].nameIt(nodeNum);
			Node temp = node[nodeNum++];
			node[nodeNum] = temp.getNext();
			node[nodeNum].nameIt(nodeNum);
		}
		if (i <= 1) {
			addFinalNode(exp[0]);
			nodeNum++;
		}
	}

	public void keywords(String keyword) // handles the keywords and punctuations
	{
		// build NFA for the keywords
		num = nodeNum;

		node[nodeNum] = new Node();
		// fill the nfa char by char
		for (int i = 0; i < keyword.length(); i++) {
			table.addInput(keyword.charAt(i));
			node[nodeNum].addArrow(keyword.charAt(i));
			node[nodeNum].nameIt(nodeNum);
			Node temp = node[nodeNum];
			nodeNum++;
			node[nodeNum] = temp.getNext();

		}
		// make the final node --ACCEPTANCE STATE--
		node[nodeNum].finish(keyword);
		node[nodeNum].nameIt(nodeNum);
		nodeNum++;
		node[0].addStart('~', node[num]); // add the start at the end
	}

	public void printTransTable() {
		int i = 0;
		table.endInput();
		table.buildTable(node, nodeNum);
		table.printInputLine();
		for (int i1 = 0; i1 < nodeNum; i1++) {
			System.out.print("||" + i1 + "||      ");
			table.printTransitionTable(node[i1], nodeNum);
		}
		
		dfa = new DFA(node);	
		dfa.convert_NFA_to_DFA(nodeNum, table.index);
	}
}
