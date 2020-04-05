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
	RegularDefinition regDef ;

	public DFA_minimization(ArrayList<String> states, ArrayList<ArrayList<String>> DFA, ArrayList<String> finish, Character[] inputs, RegularDefinition regDef) {
		this.states = states;
		this.DFA = DFA;
		this.finish = finish;
		
		this.inputs = inputs;
		in = new ArrayList<Character>(Arrays.asList(inputs));
		this.regDef = regDef;
	}
   
	public void zero_equivalence() {
		entry = new ArrayList<String>();
		for (int i = 0; i < states.size(); i++) {
			if (!(finish.get(i).equals(" "))){
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
		
		
		
	}
	
	public String[] get_dfa(String start_state, Character ch , int checkRegDef) // returns lang name or " "
	{ 
		
	   String[] ret = new String[2];
	   ret[0] = "0";//starts at the beginning node(0)
	   ret[1] = "";
	   
	   
       if(in.contains(ch) == false || checkRegDef == 1) { 
    	   // we need to check the regular expressions if the input is unique
     	    ch = check_reg_def(ch); 	    
    	    if(ch == ' ') {
    	    	ret[1] = "error";
    	    	return ret;
    	    }
       }
       
       int index = in.indexOf(ch);
       
       ret[0] = DFA.get(Integer.parseInt(start_state)).get(index);
       ret[1] = finish.get(Integer.parseInt(ret[0]));
       //if the finish state is dead end then check the regular definitions
       if(ret[1] == "dead")
       {
    	   ch = check_reg_def(ch);
    	   if(ch == ' ') //no other choice then return with the dead state 
    		   return ret; 
       //else continue with the regular definition found 
       index = in.indexOf(ch);
       ret[0] = DFA.get(Integer.parseInt(start_state)).get(index);
       ret[1] = finish.get(Integer.parseInt(ret[0]));
       }
       
       return ret ;
       
	}
	public Character check_reg_def(Character ch)  
	{
		//this function returns the equivalent regular definition to an input character
		for(int i=0;i<regDef.names.size() ; i++) {
		String t=regDef.getDefinition(regDef.names.get(i).charAt(0));
		if(t.contains(Character.toString(ch)) == true )
		{
			
			return regDef.names.get(i).charAt(0);
		}
		}
		Character empty =' ';
		return empty ;
		
	}
}