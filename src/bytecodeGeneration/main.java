package bytecodeGeneration;
import java.io.IOException;

import lexicalAnalaysis.LexicalRules;
import parser.readCFG;

public class main {
	static LexicalRules lexicalRules;
	static readCFG CFG;
	

	public static void main(String[] args) throws IOException {
		 lexicalRules = new LexicalRules();
		 //call the read file then it will do everything
		 lexicalRules.readFile();
		 CFG = new readCFG();
	}
	
} 
