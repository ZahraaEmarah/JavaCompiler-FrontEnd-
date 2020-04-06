import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

public class DFA {

	int rows;
	int column;
	String START;
	Node[] nodes;
	int count = 0;
	int s_counter = 0;
	Character[] inputs;
	int inner_loop = 0;
	int[][] count_table;
	DFA_minimization min;
	ArrayList<String> row;
	int first_time_flag = 0;
	String[] epsilon_closure;
	Queue<String> q = new LinkedList<>();
	ArrayList<String> arr = new ArrayList<String>();
	ArrayList<String> finish = new ArrayList<String>();
	ArrayList<String> DFA_states = new ArrayList<String>();
	ArrayList<ArrayList<String>> DFA = new ArrayList<ArrayList<String>>();
	RegularDefinition regDef;


	public DFA(Node[] nodes, int rows, int column, Character[] inputs,RegularDefinition regDef) {
		this.nodes = nodes;
		this.rows = rows;
		this.column = column;
		this.inputs = inputs;
		this.regDef = regDef;
	}

	/** This function counts the number of arrows coming out of each state given a certain input 
	    and stores it in a 2D array (count_table) to help with some calculations later **/
	public void Parse_NFA(int node_num, int inputs_num) {

		String[] tmp;
		count_table = new int[rows][column];
		int x = -1;
		for (int i = 0; i < node_num; i++) {
			tmp = new String[inputs_num];
			for (int j = 0; j < inputs_num; j++) {
				for (int k = 0; k < nodes[i].index; k++) {
					x = nodes[i].getInput(j, k);
					if (x != -1) {
						count++;
					} else {
						count = 0;
					}
				}
				if (count == 0)
					count_table[i][j] = count + 1;
				else
					count_table[i][j] = count;

				count = 0;
			}
		}
		epsilon_closure(count_table); // Calculate the epsilon closure of all states in the NFA transition table
		Append_Empty_state(); // Add the Empty state (Ø)
		finalize_DFA(); // Rename the states to replace for example state: (1,2,5) with a single number
		Minimize_DFA(DFA); // Minimize the generated DFA
	}

	private void epsilon_closure(int count_table[][]) {

		epsilon_closure = new String[rows]; 
		String temp = "";
		for (int i = 0; i < rows; i++) { // loop on all states (rows of the transition table)
			first_time_flag = 0;
			START = Integer.toString(i); // Mark the
			temp = get_closure(i, count_table); // Get a string of all possible paths from this state given input = epsilon
			epsilon_closure[i] = remove_white_space(temp); // remove duplicates and unnecessary spaces and store it in the array
		}
		construct_DFA_table(epsilon_closure[0], count_table); 
	}

	private String get_closure(int start, int c[][]) {

		if (start == -1) // break condition of the recursion (if no paths given input epsilon)          
			return "-1";

		String str = Integer.toString(start); // start state (Beginning of the path)
		
		if (arr.contains(Integer.toString(start))) { // Detect inner infinite cycles ex: 2,3,(6),8,(6)
			if (inner_loop == 1) { // if inner cycle flag is set it means we've started the infinite loop
				inner_loop = 0; // reset flag
				return str; // break the loop
			}
			inner_loop = 1; // if not set, set it 
		}

		// global variable START is the state that we're fetching its epsilon closure
		// if the input is equal to it it means we might have a cycle
		// ex: (2),3,6,8,(2)
		/** It might also means that this is the very first recursive call -epsilon closure of a state begins with it-
	        ex: epsilon_closure[2] = 2, ..., .., etc. so a flag (first_time_flag) is created for this purpose**/
 		if (START.equals(Integer.toString(start))) {
			if (cycle_detected()) 
				return str;
		}
		
 	    // global flag will be set by the very first loop and will be reset again from outside when a new state is fetched
		first_time_flag = 1;  
		
		for (int i = 0; i < c[start][column - 1]; i++) {
			// call the function recursively for each state that appears in the epsilon path
			String tmp = get_closure(nodes[start].getInput(column - 1, i), c); 
			arr.add(tmp);
			str = str + "," + tmp;
		}
		return str;
	}

	private boolean cycle_detected() {
		if (first_time_flag == 0) // if its the first loop
			return false;
		return true;
	}

	private String remove_white_space(String string) {
		String[] val = string.split(",");
		String ret = "";
		// remove duplicates //
		LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(Arrays.asList(val));
		val = linkedHashSet.toArray(new String[] {});
		// remove extra characters //
		for (String a : val)
			if (!(a.equals("-1"))) {
				ret = ret + a + ",";
			}
		return ret;
	}

	private void construct_DFA_table(String state, int count_table[][]) {
	// Example input state = 1,2,5,8,7,9 
	// so we split the string and loop on each state to get their output and construct a row from the DFA table
	// (is_finish) is a flag created to detect if an accepting state is in the input string or not

		row = new ArrayList<String>();
		DFA_states.add(state);
		String[] states = state.split(",");
		StringBuilder link;
		String tmp;
		int is_finish = 0;
		String lang = " ";

		for (String a : states) {
			if (nodes[Integer.parseInt(a)].finishState) {
				is_finish = 1;
				lang = nodes[Integer.parseInt(a)].langName;
				break;
			}
		}

		if (is_finish == 1) {
			is_finish = 0;
			finish.add(lang);
		} else {
			finish.add(" ");
		}

		for (int j = 0; j < column - 1; j++) {

			link = new StringBuilder();
			for (int i = 0; i < states.length; i++) {
				for (int k = 0; k < count_table[i][j]; k++) {
					int out = nodes[Integer.parseInt(states[i])].getInput(j, k);
					if (out != -1) {
						link.append(epsilon_closure[out]);
					}
				}
			}

			tmp = j + "-" + link.toString();
			Construct_output_row(tmp); // this function collects the output states separately to form a complete row
		}

		for (String a : row) { // Queue of states to be processed
			if (!q.contains(a)) {
				q.add(a);
			}
		}

		DFA.add(row);  // Add the generated row to the DFA table

		while (!q.isEmpty() && (q.peek().equals("E") || DFA_states.contains(q.peek())))
			q.poll();
		if (!q.isEmpty()) // Dequeue the next state
			construct_DFA_table(q.poll(), count_table); // repeat
	}

	// collects outputs to construct a row
	private void Construct_output_row(String linker) {
		String[] link = linker.split("-");
		int index = Integer.parseInt(link[0]);
		StringBuilder s = new StringBuilder();

		if (link.length > 1) {
			String[] val = link[1].split(",");
			for (String a : val) {
				s.append(a);
				s.append(",");
			}
		} else {
			s.append("E");
		}
		row.add(s.toString());
	}

	// rename DFA states
	private void finalize_DFA() {
		for (int i = 0; i < DFA_states.size(); i++) {
			String s1 = DFA_states.get(i).replaceAll(" ", "");
			for (int j = 0; j < DFA_states.size(); j++) {
				for (int k = 0; k < DFA.get(i).size(); k++) {
					String s2 = DFA.get(j).get(k).replaceAll(" ", "");
					if (s2.equals(s1))
						DFA.get(j).set(k, Integer.toString(s_counter));
				}
			}
			DFA_states.set(i, Integer.toString(s_counter));
			s_counter++;
		}
	}

	// Call the minimization class
	private void Minimize_DFA(ArrayList<ArrayList<String>> DFA) {
		min = new DFA_minimization(DFA_states, DFA, finish, inputs,regDef);
		min.zero_equivalence();
		//start reading the program after making the DFA minimized 
		try {
			ReadProgram read = new ReadProgram(min);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void Append_Empty_state() {
		DFA_states.add("E");
		finish.add("dead");
		row = new ArrayList<String>();
		for (int i = 0; i < column - 1; i++) {
			row.add("E");
		}
		DFA.add(row);
	}
}