import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DFA {

	Node[] nodes;
	// String DFA_table[][];
	List<String[]> rowList = new ArrayList<String[]>();
	int count = 0;

	public DFA(Node[] nodes) {
		this.nodes = nodes;
	}

	public void convert_NFA_to_DFA(int node_num, int inputs_num) {

		// DFA_table = new String[node_num][inputs_num];
		String[] tmp;
		int x = -1;
		int no_of_New_states = 0;
		for (int i = 0; i < node_num; i++) {

			System.out.println("\n----- " + i + "----- ");
			tmp = new String[inputs_num];

			for (int j = 0; j < inputs_num; j++) {
				for (int k = 0; k < nodes[i].index; k++) {

					x = nodes[i].getInput(j, k);

					if (x != -1) {
						System.out.println("input " + j + " " + x);
						count++;
					} else {
						count = 0;
					}
				}

				System.out.println("count " + count);
				if (count > 1)
					no_of_New_states++;

				tmp[j] = construct_DFA_table(x, count, i, j);
				count = 0;
			}

			rowList.add(tmp);
		}
		/**
		 * for (int i = 0; i < node_num; i++) { for (int j = 0; j < inputs_num; j++) {
		 * System.out.print(DFA_table[i][j] + " "); } System.out.println(); }
		 **/
		int c = 0;
		for (String[] row : rowList) {
			System.out.println("State " + c + " =" + Arrays.toString(row));
			c++;
		}
	}

	public String construct_DFA_table(int input, int count, int i, int j) {
		String str = "";
		if (count == 0) // Empty State
		{
			// DFA_table[i][j] = "E";
			str = "E";
		} else if (count == 1) {
			// DFA_table[i][j] = Integer.toString(input);
			str = Integer.toString(input);
		} else // New State
		{
			for (int l = 0; l < count; l++) {
				str = str + Integer.toString(nodes[i].getInput(j, l)) + "-";
			}

			ADD_New_State(str);
			// DFA_table[i][j] = str;
		}

		return str;
	}

	public void ADD_New_State(String state_name) {

		String[] str = new String[29] ;
		for (int i = 0; i < 29; i++)
			str[i] = "-1";

		rowList.add(str);
	}
}
