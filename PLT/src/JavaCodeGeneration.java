import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JavaCodeGeneration {
	// Generate the Java Byte Code
	FileWriter fileWriter;
	int numOfVariables;
	String variables = "";
	int line;
	int dontWrite;
	String writeTemp = "";
	String temp = "";
	String whileCondition = "";
	int isWhile;
	String tempWhile = "";
	String tempBoo = "";
	int whileNum1;
	int whileNum2;
	int incrementFor;
	String tempFor = "";
	int forNum;
	int booline;
	int total_condition_no;
	int condition_no;
	boolean isBoolean = false;
	boolean isLast = false;
	boolean orflag = false;
	ArrayList<String> variable = new ArrayList<String>();
	ArrayList<Character> variableDeclaration = new ArrayList<Character>();

	public JavaCodeGeneration() throws IOException {
		numOfVariables = 0;
		dontWrite = 0;
		isWhile = 0;
		line = 0;

		whileNum1 = whileNum2 = 0;
		incrementFor = 0;
		readProg();
	}

	private void readProg() throws IOException {
		File file = new File("program.txt");
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		// open output file
		fileWriter = new FileWriter("bytecode.txt");
		String tempString = new String();

		while ((tempString = buffer.readLine()) != null) {

			if (tempString.contains("+=") || tempString.contains("-=") || tempString.contains("*=")
					|| tempString.contains("/="))
				tempString = arrangeString(tempString);

			if (tempString.contains("}")) { // end of something

				if (dontWrite == 1) { // flag of if

					int t = line;
					tempString = buffer.readLine(); // else or not
					if (tempString != null) {
						if (tempString.contains("else")) // return to else
							t = line + 3; // start of else
					}
					dontWrite = 0;
					if (isWhile == 0) {
						// last line in if - take don't replace
						if (isBoolean) {
							System.out.println("total string \n" + tempBoo);

							tempBoo = tempBoo.replace("~", Integer.toString(t));
							if(tempBoo.contains("#"))
								tempBoo = tempBoo.replaceAll("#", Integer.toString(booline));
							writeByteCode(tempBoo);
							System.out.println("total string \n" + tempBoo + "\n" + booline);
						} else {
							writeTemp = writeTemp.replace("~", Integer.toString(t));
							writeByteCode(writeTemp);
						}
						isBoolean = false;
					} else
						tempWhile = tempWhile.replace("~", Integer.toString(t));

					writeTemp = "";

				}
				if (incrementFor != 0) {
					String write = "";
					if (tempFor.contains("="))
						handleConstants(tempFor, 0);
					else
						handleInc(tempFor);

					write = line + ":\t" + "goto\t" + forNum;
					line += 3;
					writeByteCode(write);
					incrementFor = 0;
					tempFor = "";
				}
				if (isWhile == 1 && tempString.contains("}")) {

					ifCondition(whileCondition);
					tempWhile = tempWhile.replace("^", Integer.toString(whileNum1));
					isWhile = whileNum1 = whileNum2 = 0;
					writeByteCode(tempWhile);
					tempWhile = "";
				}

				if (tempString == null)
					break;

			}

			if (tempString.contains("else")) {
				dontWrite = 1;
				if (isWhile == 0)
					writeTemp = line + ":\t" + "go to" + " ~";
				else
					tempWhile = tempWhile + "\n" + line + ":\t" + "go to" + " ~";
				line += 3;
			} else if (tempString.contains("while")) {
				whileCond(tempString);
			} else if (tempString.contains("for")) {
				handleFor(tempString);
			} else if (tempString.contains("int") || tempString.contains("float") || tempString.contains("boolean")) {
				handleConstants(tempString, 1); // declaration with/without arithmetic op
			} else if (tempString.contains("++") || tempString.contains("--")) {
				handleInc(tempString);
			} else if (tempString.contains("if")) {
				// we check the condition of the if
				if (tempString.contains("&&") || tempString.contains("or"))
					handleBoolean(tempString);
				else
					ifCondition(tempString);
			} else if (tempString.contains("=")) {
				handleConstants(tempString, 0); // assignment with/without arithmetic op
			}

		}
		writeByteCode(line + ":\treturn");
		fileWriter.close();

	}

	private void handleBoolean(String program) throws IOException {

		program = program.replaceAll("if", "");
		System.out.println(program);
	    if (program.contains("&&")) // expression has "&&" only
		{
			String[] temp = program.split("&&");
			total_condition_no = temp.length;
			for (int i = 0; i < temp.length; i++) {
				condition_no = i + 1;
				temp[i] = temp[i].replaceAll("\\(", "");
				temp[i] = temp[i].replaceAll("\\)", "");
				temp[i] = temp[i].trim();
				temp[i] = "if ( " + temp[i] + " ) ";
				System.out.println(temp[i]);
				isBoolean = true;
				ifCondition(temp[i]);
			}
		} 
		else if (program.contains("or")) // expressions has "or" only
		{
			orflag = true;
			String[] temp = program.split("or");
			total_condition_no = temp.length;
			for (int i = 0; i < temp.length; i++) {
				condition_no = i + 1;
				temp[i] = temp[i].replaceAll("\\(", "");
				temp[i] = temp[i].replaceAll("\\)", "");
				temp[i] = temp[i].trim();
				if (i < temp.length-1) {
					if(temp[i].contains(">"))
						temp[i] = temp[i].replaceAll(">", "<");
					else if(temp[i].contains("<"))
						temp[i] = temp[i].replaceAll("<", ">");
					else if(temp[i].contains("<="))
						temp[i] = temp[i].replaceAll("<=", ">=");
					else if(temp[i].contains(">="))
						temp[i] = temp[i].replaceAll(">=", "<=");
					else if(temp[i].contains("!="))
						temp[i] = temp[i].replaceAll("!=", "==");
					else if(temp[i].contains("=="))
						temp[i] = temp[i].replaceAll("==", "!=");
				}else
				{
					isLast = true;
				}
				temp[i] = "if ( " + temp[i] + " ) ";
				System.out.println(temp[i]);
				isBoolean = true;
				ifCondition(temp[i]);
			}
			orflag = false;
			isLast = false;
		}

	}

	private void handleFor(String program) throws IOException {
		program = program.replace("for", "");
		String[] split = program.split(";");
		String declaration = split[0].replace("(", "");
		String ifCondition = split[1];
		String increment = split[2].replace(")", "");
		declaration = declaration.trim();
		ifCondition = "(" + ifCondition + ")";

		if (declaration.contains("int") || declaration.contains("float"))
			handleConstants(declaration, 1);
		else
			handleConstants(declaration, 0);
		forNum = line;
		ifCondition(ifCondition);

		incrementFor = 1;
		tempFor = increment;

	}

	private void whileCond(String program) {
		whileCondition = program;

		isWhile = 1;
		tempWhile = line + ":\t" + "go to" + "\t^";
		line += 3;
		whileNum2 = line;

	}

	private void ifCondition(String program) throws IOException {

		String[] split = program.split("\\(");

		String condition = split[1].replace(")", "");
		String op2 = getOpCondition(condition);
		// check if the condition has ZERO or not
		String op1;
		whileNum1 = line;
		if (condition.contains("0"))
			op1 = "if";

		else
			op1 = "if_icmp";
		// load the variables and numbers that will be compared
		int var1 = findVariables(condition.split("\\" + temp)[0].replaceAll("\\s", "")); // x
		char first = variableDeclaration.get(var1); // int or float

		int var2 = -1;
		if (!condition.split("\\" + temp)[1].equals(" 0 "))// if it is zero dont load it as the default is comparing //
															// zero
			var2 = numOrVariable(condition.split("\\" + temp)[1], first);

		if (var1 != -1) {
			if (isWhile == 0) {
				if (var1 <= 3) {
					tempBoo = tempBoo + line + ":	" + first + "load_" + var1 + "\n";
					if (!isBoolean)
						writeByteCode(line + ":	" + first + "load_" + var1);
					line++;
				} else {

					tempBoo = tempBoo + line + ":	" + first + "load " + var1 + "\n";
					if (!isBoolean)
						writeByteCode(line + ":	" + first + "load " + var1);
					line += 2;
				}
			} else {
				if (var1 <= 3) {
					tempWhile = tempWhile + "\n" + line + ":	" + first + "load_" + var1;
					line++;
				} else {
					tempWhile = tempWhile + "\n" + line + ":	" + first + "load " + var1;
					line += 2;

				}
			}
		}
		String newLine = "\n";

		if (var2 != -1) {
			if (isWhile == 0) {
				if (var2 <= 3) {
					tempBoo = tempBoo + line + ":	" + first + "load_" + var2 + "\n";
					if (!isBoolean)
						writeByteCode(newLine + line + ":	" + first + "load_" + var2);
					line++;
				} else {
					tempBoo = tempBoo + line + ":	" + first + "load " + var2 + "\n";
					if (!isBoolean)
						writeByteCode(newLine + line + ":	" + first + "load " + var2);
					line += 2;
				}
			} else {
				if (var2 <= 3) {
					tempWhile = tempWhile + newLine + line + ":	" + first + "load_" + var2;
					line++;
				} else {
					tempWhile = tempWhile + newLine + line + ":	" + first + "load " + var2;
					line += 2;
				}
			}

		}

		if (isWhile == 1) {
			if (!split[0].replaceAll("\\s", "").equals("if"))
				tempWhile = tempWhile + "\n" + line + ":" + "\t" + op1 + op2 + " " + whileNum2;
			else {
				dontWrite = 1;
				tempWhile = tempWhile + "\n" + line + ":\t" + op1 + op2 + " ~";
			}
			line += 3;
			return;
		}
		dontWrite = 1;
		if(isLast || !orflag)
			writeTemp = line + ":	" + op1 + op2 + " ~";
		else
			writeTemp = line + ":	" + op1 + op2 + " #";
		tempBoo = tempBoo + writeTemp;
		line += 3;
		// we dont write in the output file till we reach '}'
	}

	private String getOpCondition(String condition) {
		// check the condition and return the ending of the (IF)
		if (condition.contains("!=")) {
			temp = "!=";
			return "eq";
		}
		if (condition.contains("==")) {
			temp = "==";
			return "ne";
		}
		if (condition.contains("<=")) {
			temp = "<=";
			return "ge";
		}
		if (condition.contains(">=")) {
			temp = ">=";
			return "le";
		}
		if (condition.contains("<")) {
			temp = "<";
			return "gt";
		}
		if (condition.contains(">")) {
			temp = ">";
			return "lt";
		}
		return "";
	}

	private String arrangeString(String string) {
		String split[] = string.split("=");
		String op = "\\" + Character.toString(split[0].charAt(split[0].length() - 1));
		split[0] = split[0].replaceFirst(op, "") + "= " + split[0] + split[1];
		return split[0];
	}

	private void handleInc(String program) throws IOException {
		int num = 1;
		String splitIt = "\\++";
		if (program.contains("--")) {
			num = num * -1;
			splitIt = "--";
		}
		String[] split = program.split(splitIt);

		char first = variableDeclaration.get(findVariables(split[0].replaceAll("\\s", "")));
		int temp = findVariables(split[0].replaceAll("\\s", ""));
		String write = line + ":\t" + first + "inc	" + temp + "," + num;

		line += 3;
		if (dontWrite == 0 && isWhile == 0)
			writeByteCode(write);
		else if (isWhile == 0) {
			writeTemp = writeTemp + "\n" + write;
			tempBoo = tempBoo + "\n" + write;
		} else
			tempWhile = tempWhile + "\n" + write;
	}

	private void handleConstants(String program, int newVar) throws IOException {
		// generate byte code for int , float with/without operations on them
		// they are similar but have some differences in the mnemonic
		// int/float id = num ;
		// int/float id;
		// int/float id = num operation num ;
		// id = id op id
		// id = number op id -----
		// id = number op number ------
		/** id = id **/

		char first = program.charAt(0);
		if (first == 'b') {
			first = 'i';
		}

		if (!program.contains("=")) {
			variableDeclaration.add(first);
			// if the variable doesn't have a declaration ie: int x ; then default is to put
			// x=0
			String write = line + ":" + "	" + first + "const_0" + "\n";
			line++;

			if (numOfVariables <= 3) {
				write = write + +line + ":\t" + first + "store_" + numOfVariables;
				line++;
			} else {
				write = write + +line + ":\t" + first + "store " + numOfVariables;
				line += 2;
			}

			if (dontWrite == 0 && isWhile == 0)
				writeByteCode(write);
			else if (isWhile == 0) {
				writeTemp = writeTemp + "\n" + write;
				tempBoo = tempBoo + "\n" + write;
			} else
				tempWhile = tempWhile + "\n" + write;
			variable.add(program.split(" ")[1].replace(";", ""));
			numOfVariables++;

			return;
		}

		System.out.println(program);
		String[] split = program.split("=");
		String check = split[1].replace("\\s", "").replace(";", "");
		System.out.println("-" + check + "-");
		if (check.replaceAll(" ", "").equals("true")) {
			check = "1";
		} else if (check.replaceAll(" ", "").equals("false")) {
			check = "0";
		}

		int temp = numOfVariables;
		if (newVar == 1)
			variableDeclaration.add(first);
		else {
			// System.out.println(split[0].replaceAll("\\s", ""));
			first = variableDeclaration.get(findVariables(split[0].replaceAll("\\s", "")));
			numOfVariables = findVariables(split[0].replaceAll("\\s", ""));
		}

		try {
			Float tryIt = Float.parseFloat(check);
			handleNum(check, first, tryIt, 1);
			numOfVariables = temp;
			if (newVar == 1) {
				variable.add(split[0].split(" ")[1]);
				numOfVariables++;
			}
		} catch (NumberFormatException nfe) {

			handleOp(split[1].replace(";", ""), first);
			numOfVariables = temp;
			if (newVar == 1) {
				variable.add(split[0].split(" ")[1]);
				numOfVariables++;
			}
		}

	}

	private void handleOp(String operation, char first) throws IOException {
		String op = "";
		String[] split;
		int temp = line;
		if (operation.contains("+")) {
			op = first + "add";
			split = operation.split("\\+");
		} else if (operation.contains("-")) {
			op = first + "sub";
			split = operation.split("-");
		} else if (operation.contains("*")) {
			op = first + "mul";
			split = operation.split("\\*");
		} else if (operation.contains("%")) {
			op = first + "rem";
			split = operation.split("\\%");
		} else {
			op = first + "div";
			split = operation.split("/");
		}

		String num1 = split[0].replace("\\s", "");
		int num_one = numOrVariable(split[0], first);
		
		

		String write = "";

		if (num_one != -1) {
			if (num_one <= 3) {
				write = line + ":	" + first + "load_" + num_one;
				line++;
			} else {
				write = line + ":	" + first + "load " + num_one;
				line += 2;
			}

		}
		int length = split.length;
		int num_two = -1;
		if(length >1) {
			String num2 = split[1].replace("\\s", "");
			num_two = numOrVariable(split[1], first);
		if (num_two != -1) {
			if (num_two <= 3) {
				write = write + "\n" + line + ":	" + first + "load_" + num_two;
				line++;
			} else {
				write = write + "\n" + line + ":	" + first + "load " + num_two;
				line += 2;
			}
		}
		}
		
		String newline = "\n";
		if (num_one == -1 && num_two == -1)
			newline = "";
		if(length>1) {
		write = write + newline + line + ":	" + op;
		line++;
		}

		if (numOfVariables <= 3) {
			write = write + "\n" + line + ":\t" + first + "store_" + numOfVariables;
			line++;
		} else {
			write = write + "\n" + line + ":\t" + first + "store " + numOfVariables;
			line += 2;
		}

		if (dontWrite == 0 && isWhile == 0)
			writeByteCode(write);
		else if (isWhile == 0) {
			writeTemp = writeTemp + "\n" + write;
			tempBoo = tempBoo + "\n" + write;
		} else
			tempWhile = tempWhile + "\n" + write;

	}

	private int numOrVariable(String split, char first) throws IOException {
		String num1 = split.replace(" ", "");

		int num_one = -1;

		try {
			Float tryIt = Float.parseFloat(num1);
			handleNum(num1, first, tryIt, 0);
			// firstt = Integer.parseInt(num1);
			num_one = -1;

		} catch (NumberFormatException nfe) {
			num_one = findVariables(num1);

		}
		return num_one;
	}

	private int findVariables(String num) {
		return variable.indexOf(num);
	}

	private void handleNum(String check, char first, float num, int storeIt) throws IOException {

		String write;
		int temp = line;
		if (check.contains("-")) {
			num = num * -1;
			if (num == 1) {
				write = line + ":" + "\t" + first + "const_" + check.replace("-", "m");
				line++;
				if (storeIt == 1) {

					if (numOfVariables <= 3) {
						write = write + "\n" + line + ":\t" + first + "store_" + numOfVariables;
						line++;
					} else {
						write = write + "\n" + line + ":\t" + first + "store " + numOfVariables;
						line += 2;
					}
				}

				if (dontWrite == 0 && isWhile == 0) {
					tempBoo = tempBoo + "\n" + write + "\n";
					if (!isBoolean)
						writeByteCode(write);
				} else if (isWhile == 0) {
					writeTemp = writeTemp + "\n" + write;
					tempBoo = tempBoo + "\n" + write + "\n";
				} else {

					tempWhile = tempWhile + "\n" + write;
				}
				return;
			}
		}
		if (num > 5) {

			if (first == 'i') {
				char length = 'b';
				if (num > 127)
					length = 's';
				write = line + ":	" + length + "ipush\t" + check;
				booline = line;
				if (length == 's')
					line += 3;
				else
					line += 2;
				if (storeIt == 1) {

					if (numOfVariables <= 3) {
						write = write + "\n" + line + ":\t" + "istore_" + numOfVariables;
						line++;
					} else {
						write = write + "\n" + line + ":\t" + "istore " + numOfVariables;
						line += 2;
					}
				}

				if (dontWrite == 0 && isWhile == 0) {
					tempBoo = tempBoo + "\n" + write + "\n";
					if (!isBoolean)
						writeByteCode(write);
				} else if (isWhile == 0) {
					writeTemp = writeTemp + "\n" + write;
					tempBoo = tempBoo + "\n" + write + "\n";
				} else
					tempWhile = tempWhile + "\n" + write;
				return;
			} else {

				write = line + ":" + "\t" + "ldc	" + check;
				booline = line;
				line += 2;
				if (storeIt == 1) {

					if (numOfVariables <= 3) {
						write = write + "\n" + line + ":\t" + "fstore_" + numOfVariables;
						line++;
					} else {
						write = write + "\n" + line + ":\t" + "fstore " + numOfVariables;
						line += 2;
					}
				}

				if (dontWrite == 0 && isWhile == 0) {
					tempBoo = tempBoo + "\n" + write + "\n";
					if (!isBoolean)
						writeByteCode(write);
				} else if (isWhile == 0) {
					writeTemp = writeTemp + "\n" + write;
					tempBoo = tempBoo + "\n" + write;
				} else
					tempWhile = tempWhile + "\n" + write;
				return;
			}
		}

		if (num >= 0) {

			// in the range of 0-5
			write = line + ":" + "\t" + first + "const_" + check;
			booline = line;
			line++;
			if (storeIt == 1) {
				if (numOfVariables <= 3) {
					write = write + "\n" + line + ":\t" + first + "store_" + numOfVariables;
					line++;
				} else {
					write = write + "\n" + line + ":\t" + first + "store " + numOfVariables;
					line += 2;
				}
			}

			if (dontWrite == 0 && isWhile == 0) {
				tempBoo = tempBoo + "\n" + write + "\n";
				if (!isBoolean)
					writeByteCode(write);
			} else if (isWhile == 0) {
				writeTemp = writeTemp + "\n" + write;
				tempBoo = tempBoo + "\n" + write + "\n";
			} else
				tempWhile = tempWhile + "\n" + write;
			return;
		}

	}

	private void writeByteCode(String write) throws IOException {

		fileWriter.write(write + "\n");
	}
}