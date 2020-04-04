import java.util.*;

public class DFA_minimization {

	ArrayList<String> entry;
	ArrayList<String> states = new ArrayList<String>();
	ArrayList<String> finish = new ArrayList<String>();
	ArrayList<ArrayList<String>> DFA = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> P = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> Pk = new ArrayList<ArrayList<String>>();

	public DFA_minimization(ArrayList<String> states, ArrayList<ArrayList<String>> DFA, ArrayList<String> finish) {
		this.states = states;
		this.DFA = DFA;
		this.finish = finish;
	}

	public void zero_equivalence() {
		entry = new ArrayList<String>();
		for (int i = 0; i < states.size(); i++) {
			if (finish.get(i).contains("*")){
				entry.add(states.get(i));
				}
		}
		P.add(entry);
		
		entry = new ArrayList<String>();
		for (int i = 0; i < states.size(); i++) {
			if (!(finish.get(i).contains("*")))
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
		//System.out.println(temp);
		//System.out.println(P);
		
		for (int i = 0; i < P.size(); i++) { // first set
			for (int j = 0; j < P.get(i).size(); j++) { // first element
				
				String first = P.get(i).get(j);
				entry = new ArrayList<String>();
				entry.add(first);
				P.get(i).remove(j);	
				//System.out.println(temp);
				//System.out.println(P);
				
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
        
		//System.out.println(temp);
		//System.out.println(Pk);
		temp.removeAll(Pk);
		//System.out.println(temp);
		System.out.println(Pk);
		
		if(!temp.isEmpty())
			N_equivalence(Pk);
			
		Filter_DFA();
	}
	
	private boolean is_equivalent(String one, String two, ArrayList<ArrayList<String>> P)
	{
		int state1 = Integer.parseInt(one);
		int state2 = Integer.parseInt(two);
		
		//System.out.println(DFA.get(state1));
		//System.out.println(DFA.get(state2));
		
		for(int i=0; i<DFA.get(0).size(); i++)
		{
			for(int j=0; j<P.size(); j++)
			{
				//System.out.println(DFA.get(state1).get(i));
				//System.out.println(DFA.get(state2).get(i));
				//System.out.println(P.get(j));
				if(P.get(j).contains(DFA.get(state1).get(i)))
				{
					if(!(P.get(j).contains(DFA.get(state2).get(i))))
						return false;
					break;
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
		System.out.println();
		for (int i = 0; i < Pk.size(); i++) {
			System.out.print(Pk.get(i).get(0) + finish.get(Integer.parseInt(Pk.get(i).get(0))) + " -- > ");
			for(int j=0; j< DFA.get(i).size(); j++) {
			System.out.print(DFA.get(Integer.parseInt(Pk.get(i).get(0))).get(j) + " ");
			}
			
			System.out.println();
		}
	}
}
