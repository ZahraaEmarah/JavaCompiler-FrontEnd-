
public class ReadTokens {
	ParseTable parseTable;
	String startNonTerminal;
	public ReadTokens(ParseTable parseTable,String startNonTerminal)
	{
		this.parseTable = parseTable;
		this.startNonTerminal = startNonTerminal;
		System.out.println(parseTable.getExpression("int", startNonTerminal));
		System.out.println(parseTable.getExpression("id", "TERM"));
	}
	//example to getExpression 
	//getExpression(";","METHOD_BODY")
	//get the expression of method_body when the terminal is ";"

}
