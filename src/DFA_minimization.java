import java.util.*;

public class DFA_minimization {

	Character[] inputs;
	ArrayList<String> entry;
	ArrayList<String> states = new ArrayList<String>();
	ArrayList<String> finish = new ArrayList<String>();
	ArrayList<Character> in;
	ArrayList<ArrayList<String>> DFA = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> P = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> Pk = new ArrayList<ArrayList<String>>();

	public DFA_minimization(ArrayList<String> states, ArrayList<ArrayList<String>> DFA, ArrayList<String> finish, Character[] inputs) {
		this.states = states;
		this.DFA = DFA;
		this.finish = finish;
		this.inputs = inputs;
		in = new ArrayList<Character>(Arrays.asList(inputs));
	}

	public void zero_equivalence() {
		entry = new ArrayList<String>();
		for (int i = 0; i < states.size(); i++) {
			if (!finish.get(i).equals(" ")){
				entry.add(states.get(i));
				}
		}
		P.add(entry);
		
		entry = new ArrayList<String>();
		for (int i = 0; i < states.size(); i++) {
			if ((finish.get(i).equals(" ")))
				entry.add(states.get(i));
		}
		P.add(entry);
		N_equivalence(P);
		print_min_DFA();
	}
	
	private void N_equivalence(ArrayList<ArrayList<String>> P) {
		
		Pk = new ArrayList<ArrayList<String>>();
		temp = new ArrayList<ArrayList<String>>();
		temp = deep_copy(P);
		
		for (int i = 0; i < P.size(); i++) { // first set
			for (int j = 0; j < P.get(i).size(); j++) { // first element
				
				String first = P.get(i).get(j);
				entry = new ArrayList<String>();
				entry.add(first);
				P.get(i).remove(j);	
				
               for(int k=0; k< P.get(i).size(); k++) {
            	   String second = P.get(i).get(k);     
            	   if(is_equivalent(first,second,temp)){ 
            		   entry.add(P.get(i).get(k));
            		   P.get(i).remove(k);
            		   k--;
            		}      	   
               }	                    
               Pk.add(entry);
               if(!(P.get(i).isEmpty()))
            	   j--;
			}
		}	
        
		temp.removeAll(Pk);
		
		if(!temp.isEmpty())
			N_equivalence(Pk);
			
		Filter_DFA();
	}
	
	private boolean is_equivalent(String one, String two, ArrayList<ArrayList<String>> P)
	{
		int state1 = Integer.parseInt(one);
		int state2 = Integer.parseInt(two);
		
		for(int i=0; i<DFA.get(0).size(); i++)
		{
			for(int j=0; j<P.size(); j++)
			{
				if(P.get(j).contains(DFA.get(state1).get(i)))
				{
					if(!(P.get(j).contains(DFA.get(state2).get(i))))
						return false;
				}
				if(!(finish.get(state1).equals(finish.get(state2))))
				{
					return false;
				}
			}
		}		
		return true;
	}
	
	private ArrayList<ArrayList<String>> deep_copy(ArrayList<ArrayList<String>> original)
	{
		ArrayList<ArrayList<String>> copy_cat = new ArrayList<ArrayList<String>>();
		ArrayList<String> temp ;
		for (int i = 0; i < original.size(); i++) {
			temp = new ArrayList<String>();
			for (int j = 0; j < original.get(i).size(); j++) {
				temp.add(original.get(i).get(j));			
			}
			copy_cat.add(temp);
		}
		
		return copy_cat;
	}
	
	private void Filter_DFA()
	{
		// get pk of 1
		for(int i=0; i<Pk.size(); i++) {
		// if size = 1 return
			if(Pk.get(i).size() == 1)
				continue;		
		// choose an el
			String chosen = Pk.get(i).get(0);

			for(int j=1; j< Pk.get(i).size(); j++) {
		        // loop on dfa and replace all siblings with it
				
				//search for 3
				//replace with chosen			
				for (int k = 0; k < DFA.size(); k++) {
					for (int n = 0; n < DFA.get(i).size(); n++) {
						if(DFA.get(k).get(n).equals(Pk.get(i).get(j)))	
							DFA.get(k).set(n, chosen);
					}
				}
				
			}
		}
		
	}

	private void print_min_DFA() {
		System.out.println("\n\n*** Minimized DFA ***");
		System.out.println(in);
		for (int i = 0; i < Pk.size(); i++) {
			System.out.print(Pk.get(i).get(0) +"  " +  finish.get(Integer.parseInt(Pk.get(i).get(0))) + " -- > ");
			for(int j=0; j< DFA.get(i).size(); j++) {
			System.out.print(DFA.get(Integer.parseInt(Pk.get(i).get(0))).get(j) + " ");
			}
			System.out.println();
		}
		
		String[] get = new String[2];
		get = get_dfa("0", '=');
		System.out.println(get[0]);
		System.out.println(get[1]);
		
	}
	
	public String[] get_dfa(String start_state, Character ch) // returns lang name or " "
	{ 
       int index = in.indexOf(ch);
       String[] ret = new String[2];
       ret[0] = DFA.get(Integer.parseInt(start_state)).get(index);
       ret[1] = finish.get(Integer.parseInt(ret[0]));
       
       return ret;
	}
}
