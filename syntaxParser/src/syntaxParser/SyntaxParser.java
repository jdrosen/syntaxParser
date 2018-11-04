package syntaxParser;
import java.io.*;
import java.util.Stack;

public class SyntaxParser {
	
	public enum State {
		TOP,			//The initial state.
		ROUND,			//Parsing the contents of round brackets. Must close with a round.
		CURLY,			//Parsing the contents of curly brackets. Must close with curly.
		QUOTE,			//Parsing a quote. Only escaped quotes are processed inside here.
		ESCAPEQUOTE		//Parsing an escape quote. Double quote must be next.
	}	
	
	private Stack<State> myStack;
	
	private String s;
	
	
	/* Constructor initializes the stack and state and stores the string */
	
	SyntaxParser (String s) {
		
		myStack = new Stack<>();
		myStack.push(State.TOP)	;
		this.s = s;
	}
	
	/* Primary method parseIt() parses the string and returns a true if the
	 * string is valid, false otherwise. It will also print out the specific 
	 * error to System.out.
	 */
	
	public Boolean parseIt() {
		
		/* loop through each character in the string */
		
		for(int i=0; i < s.length(); i++) {
			
			/* Based on the current state, different things are OK. */
			
			//System.out.println(s.charAt(i));
			
			switch(myStack.peek()) {
		
				/* At the top level or within either of the bracket types, we can start
				 * a new open bracket or quote. However the closing bracket must match
				 * the opening one.
				 */
			
				case TOP:
				case ROUND:
				case CURLY:
				{
				
					//System.out.println("Entering TOP/ROUND/CURLY");
					/* In all three cases, a new curly, round or quote can start */
					
					if(s.charAt(i)=='(') myStack.push(State.ROUND);
					if(s.charAt(i)=='{') myStack.push(State.CURLY);
					if(s.charAt(i)=='"') myStack.push(State.QUOTE);
					
					/* But the closing bracket must match the opening one */
					
					if(s.charAt(i)==')') {
						if(myStack.peek() == State.ROUND) {
							myStack.pop();
						} else {
							System.out.println("Error: close round bracket without matching open");
							return(false);
						}
					} else if(s.charAt(i)=='}') {
						if(myStack.peek() == State.CURLY) {
							myStack.pop();
						} else {
							System.out.println("Error: close curly bracket without matching open");
							return(false);
						}
					} 
					break;
					
					
				}	
				
				case QUOTE: {
					
					/* While in a quote, the only options are an end quote, or
					 * an escaped quote - so a " or \. Everything else is ignored as a 
					 * string content. 
					 */
					
					if(s.charAt(i) == '"') myStack.pop();
					if(s.charAt(i) == '\\') myStack.push(State.ESCAPEQUOTE);						
				
					break;
				}
				case ESCAPEQUOTE: {
				
					/* while in an escape quote, the ONLY permitted character is a quote.
					 * Everything else is an error.
					 */
					
					if(s.charAt(i) == '"') {
						myStack.pop();
					} else {
						System.out.println("Error: a backslash must be followed by double quote");
						return(false);
						
					}
				}
				
				break;
			}	
			
		}
		
		/* Finally, if we've closed all the opens, we're good. 
		 * Otherwise its an error.
		 */
		if(myStack.peek() == State.TOP) {
			return(true);
		} else {
			System.out.println("Error: open bracket or string is unclosed");
			return(false);
		}
		
	}
	
	public static void main(String[] args) {
		
		/* Basic parser project. Reads a file whose name is passed on the command line,
		 * and parses it to look for (, ), {, }, strings and string escapes. 
		 */
		
		String line = null;
		
		if(args.length != 1) {
			System.out.println("Syntax: java SyntaxParser <filename>");
		} 
		
		try {
            
            FileReader fileReader = new FileReader(args[0]);
            BufferedReader bufferedReader =  new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                SyntaxParser myParser = new SyntaxParser(line);
                Boolean result = myParser.parseIt();
                if(result == true) System.out.println("Result is valid!");
            }   

            // Always close files.
            bufferedReader.close();  
		}
		catch(FileNotFoundException ex) {
                System.out.println("File not found: " + args[0]);
		}	
        catch(IOException ex) {
                System.out.println("Error on read: " + args[0]); 
                   
		} 
	}

}
