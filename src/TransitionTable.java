
public class TransitionTable {
	Character[] inputs;
	int index;
	
	// array of epsilon transitions of each state

	String[][] transition;

	public TransitionTable() {
		inputs = new Character[100];
		transition = new String[100][100];
	}

	public void addInput(Character add) {
		// check for duplicates first
		for (int i = 0; i < index; i++) {
			if (inputs[i] == add)
				return;
		}
		inputs[index] = add;
		index++;
	}

	public void endInput() // FOR NFA --epsilon--
	{
		inputs[index] = '~';
		index++;
	}

	public void printInputLine() {
		System.out.print("||STATE" + "||   ");
		for (int i = 0; i < index; i++) {
			System.out.print(inputs[i] + "  ");
		}
		System.out.println("");
	}

	public void printTransitionTable(Node node, int nodeNum) // print the transition table
	{
		int in = node.index;
		if (in <= 0)
			in = 1;

		for (int i = 0; i < index; i++) {
			for (int j = 0; j < in; j++) {
				if (node.inputsTable[i][j] == -1) {
					System.out.print("_");
					break;
				}
				System.out.print(node.getInput(i, j) + " ");
			}
			System.out.print("|");
		}
		System.out.println("");
        	
	}

	public void intializeTransition(int nodeNum) {
		for (int i = 0; i < nodeNum; i++) {
			for (int j = 0; j < index; j++)
				transition[i][j] = "_";
		}
	}

	public void buildTable(Node[] node, int nodeNum) {

		for (int i = 0; i < nodeNum; i++) {
			node[i].intialArray(index);

			for (int j = 0; j < index; j++) {

				for (int k = 0; k < node[i].index; k++) {

					if (node[i].value[k] == inputs[j]) {
						// transition[i][j] += Integer.toString(node[i].next[k].name) + "_";
						node[i].addInput(j, k, node[i].next[k].name);
					}
				}

			}
		}
	}

}
