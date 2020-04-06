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
		finalize_DFA();
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
		construct_DFA_table(epsilon_closure[0], count_table);
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
			Construct_output_row(tmp);
		}

		for (String a : row) {
			if (!q.contains(a)) {
				q.add(a);
			}
		}

		DFA.add(row);

		while (!q.isEmpty() && (q.peek().equals("E") || DFA_states.contains(q.peek())))
			q.poll();
		if (!q.isEmpty())
			construct_DFA_table(q.poll(), count_table);
	}

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

	private void print_DFA(ArrayList<ArrayList<String>> DFA) {
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