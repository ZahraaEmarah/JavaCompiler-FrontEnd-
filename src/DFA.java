import java.util.ArrayList;
import java.util.List;

public class DFA {

	Node[] nodes;
	List<ArrayList<String>> DFA = new ArrayList<ArrayList<String>>();
	ArrayList<String> arr = new ArrayList<String>();
	String[] epsilon_closure;
	int count = 0;
	int[][] count_table;
	int column;
	int rows;

	public DFA(Node[] nodes, int rows, int column) {
		this.nodes = nodes;
		this.rows = rows;
		this.column = column;
	}

	public void Parse_NFA(int node_num, int inputs_num) {

		String[] tmp;
		count_table = new int[rows][column];
		int x = -1;
		for (int i = 0; i < node_num; i++) {
			// System.out.println("\n----- " + i + "----- ");
			tmp = new String[inputs_num];
			for (int j = 0; j < inputs_num; j++) {
				for (int k = 0; k < nodes[i].index; k++) {
					x = nodes[i].getInput(j, k);
					if (x != -1) {
						// System.out.println("input " + j + " " + x);
						count++;
					} else {
						count = 0;
					}
				}
				// System.out.println("count " + count);
				if (count == 0)
					count_table[i][j] = count + 1;
				else
					count_table[i][j] = count;

				count = 0;
			}
		}

		epsilon_closure(count_table);
	}

	public void epsilon_closure(int count_table[][]) {
		epsilon_closure = new String[rows];
		for (int i = 0; i < rows; i++) {
			String str = "";
			for (int j = 0; j < count_table[i][column - 1]; j++) {
				if (nodes[i].getInput(column - 1, j) != -1) {
					str = str + "," + nodes[i].getInput(column - 1, j);
				}
			}
			epsilon_closure[i] = i + str;
		}

		for (int k = 0; k < 1; k++) {
			if (epsilon_closure[k].contains(",")) {
				construct_DFA_table(epsilon_closure[k]);
			}
			//System.out.println();
		    //System.out.println(k + " epsilon is " + epsilon_closure[k]);
		}
	}

	public void construct_DFA_table(String state) { 
		arr.clear();
		String[] states = state.split(",");
		StringBuilder link ;
		String tmp;

		for (int j = 0; j < column - 1; j++) {
             
			link = new StringBuilder();
			for (int i = 0; i < states.length; i++) {
				int out = nodes[Integer.parseInt(states[i])].getInput(j, 0); ///////BEWARE: NEEDS ADJUST
				if (out != -1) {
					link.append(epsilon_closure[out]) ;
					link.append(",");
				}
			}
			tmp = j + "-" + link.toString();
			Add_output_row(tmp);
		
		}
		
		DFA.add(arr);
		System.out.println(arr);
		//for(String a: arr)
	
		//if(arr.get(0) != "E" && is_New_State(arr.get(0)))
		//////////// ENQUEUE //////////////////////
		
		//////////// DEQUEUE //////////////////////
		construct_DFA_table(arr.get(0));
	}

	public void Add_output_row(String linker) {
		String[] link = linker.split("-");
		int index = Integer.parseInt(link[0]);
		//System.out.print(index + " --> ");
		StringBuilder s = new StringBuilder();;
		if (link.length > 1) {
			String[] val = link[1].split(",");
			for (String a : val) {
				s.append(a);
				s.append(",");
			}
		}else
		{
			s.append("E");
		}
		//System.out.println(s);
        arr.add(s.toString());	
	}
	
	public boolean is_New_State(String state)
	{
		if(DFA.isEmpty())
			return true;
		else if(DFA.contains(state))
			return false;
		else
			return true;
	}
}

   
