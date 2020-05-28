import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class ReadTokens {

	ParseTable parseTable;
	String startNonTerminal;
	Stack<String> input = new Stack<>();
	Stack<String> stack = new Stack<>();
	ArrayList<String> output = new ArrayList<String>();
	FileWriter outputFile;

	public ReadTokens(ParseTable parseTable, String startNonTerminal) throws IOException {
		this.parseTable = parseTable;
		this.startNonTerminal = startNonTerminal;
		read_file();
		
	}

	// example to getExpression
	// getExpression(";","METHOD_BODY")
	// get the expression of method_body when the terminal is ";"

	private void read_file() throws IOException {
		BufferedReader reader;
		outputFile = new FileWriter("outputPhaseTwo.txt");
		try {
			reader = new BufferedReader(new FileReader("output.txt"));
			String line = reader.readLine();
			while (line != null) {
				
				if(!line.contains("error")) {
				input.push(line);
				}
				else
				{
					System.out.println("PHASE ONE HAS ERROR");
					System.exit(0);
				}
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stack.push(startNonTerminal);
		input.push("$");
		reverse(); // reverse input stack
	
		while(!stack.isEmpty())
			track_stack(stack.peek(), input.peek());
		System.out.println("\n\n" + output);
		outputFile.close();
	}
	
	private void insert_at_bottom(String x) 
    { 
        if(input.isEmpty()) 
            input.push(x); 
        else
        { 
            String a = input.peek(); 
            input.pop(); 
            insert_at_bottom(x); 
            input.push(a); 
        } 
    } 
      
	private void reverse() 
    { 
        if(input.size() > 0) 
        { 
            String x = input.peek(); 
            input.pop(); 
            reverse(); 
            insert_at_bottom(x); 
        } 
    } 
	
	private void track_stack(String st, String ip) throws IOException
	{
		if(st.contains("'"))
		{
			ip = ip.replace(" ", "");
			st = st.replace(" ", "");
			if(st.replaceAll("'", "").equals(ip))
			{
				stack.pop();
				input.pop();
				System.out.print(stack + "\t\t\t");
				System.out.println(input);
				
				writeOutputFile(stack,"",input);
				output.add(ip);
				return;
			}else
			{
				String Err = "Error: Missing " + st + " inserted";
				//output.add(Err);
				output.add(st.replaceAll("'", ""));
				System.err.println(Err);
				stack.pop();
				
				System.out.print(stack + "\t\t\t"); 
				System.out.println(input);
				writeOutputFile(stack,Err,input);
				return;
			}
		}
		
		String out = parseTable.getExpression(ip.replaceAll(" ", ""), st);
		
		if(out.replaceAll(" ", "").equals("none"))
		{
			if(!ip.equals("$"))
			{
				String Err = "Error: Illegal " + st + " discard " + ip;
				System.err.println("Error: Illegal " + st + " discard " + ip);
				writeOutputFile(stack,Err,input);
				input.pop();
			}else {
				String Err = "Error: Illegal " + st;
				System.err.println("Error: Illegal " + st);
				writeOutputFile(stack,Err,input);
				stack.pop();
			}
			System.out.print(stack + "\t\t\t"); 
			System.out.println(input);
			writeOutputFile(stack,"",input);
			return;
		}
		
		if(out.contains("~"))
		{
			stack.pop();
			System.out.print(stack + "\t\t\t"); 
			System.out.println(input);
			writeOutputFile(stack,"",input);
			return;
		}
		
		String[] temp = out.split(" ");
		stack.pop();
		for(int i=temp.length-1; i>=0; i--)
		{
			stack.push(temp[i]);
		}
		
		System.out.print(stack + "\t\t\t"); 
		System.out.println(input); 
		writeOutputFile(stack,"",input);
	}
	
	private void writeOutputFile(Stack<String> str , String extra , Stack<String> input) throws IOException
	{
		
		
		outputFile.write(str +extra+"\t" + input+ "\n");
	}

}