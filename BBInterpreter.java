import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class BBInterpreter
{
	static HashMap<String,Integer> varMap = new HashMap<String,Integer>();
	static BufferedReader reader;
	static String[] savedLines = new String[999];
	static int lineNumber = 0;
	
	//Read text input by the user from the command line
	public static String readInput() throws IOException
	{
		BufferedReader cmdReader = new BufferedReader(new InputStreamReader(System.in));
		String input = cmdReader.readLine();
		cmdReader.close();
		return input;
	}
	
	//Reads a line from the BareBones file and calls an appropriate function
	public static boolean interpretLine(String line) throws IOException
	{
		String[] lineContents;
		
		line = line.trim(); //Gets rid of whitespace from the start of the line
		//Checking for correct syntax
		int tPosition = line.indexOf(";");
		if(tPosition >= 0)
		{
			line = line.substring(0, tPosition); 
		}
		else
		{
			System.out.println("Error: ; missing from line");
			System.exit(0);
		}
		
		lineContents = line.split(" ", 3);
		
		switch(lineContents[0])
		{
			case "clear":
				clear(lineContents[1]);
			break;
			case "incr":
				increment(lineContents[1]);
			break;
			case "decr":
				decrement(lineContents[1]);
			break;
			case "while":
				/*
				if(lineContents[2] != "not 0 do")
				{
					System.out.println("Error: invalid syntax");
					System.exit(0);
				}
				*/
				whileLoop(lineContents[1]);
			break;
			case "end":
				return true; //The whileLoop function will loop when this function returns true
			default:
				System.out.println("Error: invalid syntax");
				System.exit(0);
		}
		
		//Prints the values of all variables created so far after a line is interpreted
		for(HashMap.Entry i : varMap.entrySet())
		{
			System.out.print(i.getKey() + " = " + i.getValue() + "   ");
		}
		System.out.print("\n");
		return false;
	}
	
	public static void clear(String var)
	{
		varMap.put(var, 0);
	}
	
	public static void increment(String var)
	{
		varMap.put(var, varMap.get(var) + 1);
	}
	
	public static void decrement(String var)
	{
		varMap.put(var, varMap.get(var) - 1);
	}
	
	public static void whileLoop(String var) throws IOException
	{
		int markedLine = lineNumber; //Saves the line in the code where the loop starts
		boolean endReached;
		
		while(varMap.get(var) != 0)
		{
			lineNumber = markedLine;
			endReached = false;
			
			while(!endReached)
			{
				lineNumber ++;
				endReached = interpretLine(savedLines[lineNumber]);
			}
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		String newLine;
		int numberOfLines = 0;
		boolean exception;
		
		System.out.println("Enter filepath of the text file to read:");
		reader = new BufferedReader(new FileReader(readInput()));
		
		//Saves all the lines in the file to a list; makes it easier to interpret nested while loops but not efficient for big files
		while(true)
		{
			newLine = reader.readLine();
			if(newLine == null)
				break;
			savedLines[numberOfLines] = newLine;
			numberOfLines ++;
		}
		reader.close();
		
		while(lineNumber < numberOfLines)
		{
			exception = interpretLine(savedLines[lineNumber]);
			if(exception)
			{
				System.out.println("Error: \"end\" called illegally");
				System.exit(0);
			}
			
			lineNumber ++;
		}
	}
}