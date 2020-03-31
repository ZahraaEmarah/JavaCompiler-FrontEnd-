import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DFA {

	Node[] nodes;
	ArrayList<ArrayList<String>> DFA = new ArrayList<ArrayList<String>>();
	ArrayList<String> row;
	ArrayList<String> DFA_states = new ArrayList<String>();
	ArrayList<String> arr = new ArrayList<String>();
	Queue<String> q = new LinkedList<>();
	String[] epsilon_closure;
	int count = 0;
	int[][] count_table;
	int column;
	int rows;
	String START;
	int first_time_flag = 0;
	int inner_loop = 0;

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

		epsilon_closure(count_table);
		Append_Empty_state();
		print_DFA(DFA);
	}

	private void epsilon_closure(int count_table[][]) {

		epsilon_closure = new String[rows];
		String temp = "";
		for (int i = 0; i < rows; i++) {
			first_time_flag = 0;
			START = Integer.toString(i);
			temp = closure(i, count_table);
			epsilon_closure[i] = remove_white_space(temp);
		}
		/**
		 * for (int k = 0; k < epsilon_closure.length; k++) { System.out.println();
		 * System.out.println(k + " epsilon is " + epsilon_closure[k]); }
		 **/

		construct_DFA_table(epsilon_closure[0]);
	}

	private String closure(int start, int c[][]) {

		if (start == -1)
			return "-1";
		String str = Integer.toString(start);

		if (arr.contains(Integer.toString(start))) {
			if (inner_loop == 1) {
				inner_loop = 0;
				return str;
			}
			inner_loop = 1;
		}

		if (START.equals(Integer.toString(start))) {
			if (cycle_detected())
				return str;
		}
		first_time_flag = 1;
		for (int i = 0; i < c[start][column - 1]; i++) {
			String tmp = closure(nodes[start].getInput(column - 1, i), c);
			arr.add(tmp);
			str = str + "," + tmp;
		}
		return str;
	}

	private boolean cycle_detected() {
		if (first_time_flag == 0)
			return false;
		return true;
	}

	private String remove_white_space(String string) {
		String[] val = string.split(",");
		String ret = "";
		for (String a : val)
			if (!(a.equals("-1"))) {
				ret = ret + a + ",";
			}
		return ret;
	}

	private void construct_DFA_table(String state) {
		System.out.println("state: " + state + " Entered");
		row = new ArrayList<String>();
		DFA_states.add(state);
		String[] states = state.split(",");
		StringBuilder link;
		String tmp;

		for (int j = 0; j < column - 1; j++) {

			link = new StringBuilder();
			for (int i = 0; i < states.length; i++) {
				int out = nodes[Integer.parseInt(states[i])].getInput(j, 0); /////// BEWARE: NEEDS ADJUST
				if (out != -1) {
					link.append(epsilon_closure[out]);
					//link.append(",");
				}
			}
			tmp = j + "-" + link.toString();
			Construct_output_row(tmp);
		}
		System.out.println("Row: " + row + "\n");

		for (String a : row) {
			if (!q.contains(a)) {
				q.add(a);
			}
		}

		DFA.add(row);
		System.out.println("Queue: " + q);
		System.out.println("List of states: " + DFA_states);

		while (!q.isEmpty() && (q.peek().equals("E") || DFA_states.contains(q.peek())))
			q.poll();
		if (!q.isEmpty())
			construct_DFA_table(q.poll());

	}

	private void Construct_output_row(String linker) {
		String[] link = linker.split("-");
		int index = Integer.parseInt(link[0]);
		StringBuilder s = new StringBuilder();
		;
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

	private void print_DFA(ArrayList<ArrayList<String>> DFA) {
		for (int i = 0; i < DFA.size(); i++) {
			System.out.print("|| " + DFA_states.get(i) + "\t|| ");
			for (int j = 0; j < DFA.get(i).size(); j++) {
				System.out.print(DFA.get(i).get(j) + " ");
			}
			System.out.println();
		}
	}

	private void Append_Empty_state() {
		DFA_states.add("E");
		row = new ArrayList<String>();
		for (int i = 0; i < column - 1; i++) {
			row.add("E");
		}
		DFA.add(row);
	}
}
