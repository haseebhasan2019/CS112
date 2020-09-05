package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression 
{
	public static String delims = " \t*+-/()[]";
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    {
    	//Remove white space and Create tokens separated by delimiters
    	expr = expr.replaceAll("\\s", "");
    	StringTokenizer tokens = new StringTokenizer(expr, "+-*/()][", true);
    	//Fill array list with tokens
    	ArrayList<String> arr = new ArrayList<String>();
    	while(tokens.hasMoreTokens())
    		arr.add(tokens.nextToken());
    	//Traversing array list
    	for (int i = 0; i < arr.size(); i++)
    	{
    		if ((Character.isLetter((arr.get(i)).charAt(0))) && (i+1 == arr.size()))
    			vars.add(new Variable(arr.get(i)));
    		else if ((Character.isLetter((arr.get(i)).charAt(0))) && (arr.get(i+1).equals("[")))
    			arrays.add(new Array(arr.get(i)));
    		else if ((Character.isLetter((arr.get(i)).charAt(0))))
    			vars.add(new Variable(arr.get(i)));
    	}
    	//Got minus 5 because I didn't check for duplicates in the arraylists. 
    }
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    private static int getMatchingCloseBracket(ArrayList<String> arr, int indexOfOpenBracket) 
    {
    	String matchingCloseBracket; // ], )
    	if (arr.get(indexOfOpenBracket).equals("("))
    		matchingCloseBracket = ")";
    	else
    		matchingCloseBracket = "]";
    	for (int i = indexOfOpenBracket + 1; i < arr.size(); i++)
    		if (arr.get(i).equals(matchingCloseBracket))
    			return i;
    	return -1;
    }
    private static int getNextMinusSign(ArrayList<String> arr, int currentMinusSign) 
    {
    	for (int i = currentMinusSign + 1; i < arr.size(); i++)
    		if (arr.get(i).equals("-"))
    			return i;
    	return -1;
    }
    private static boolean isNumeric(String strNum) 
    {
        if (strNum == null) {
            return false;
        }
        try {
            Float.parseFloat(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    {
    	//Remove white space
    	expr = expr.replaceAll("\\s", "");
    	//Replacing variables with their values
		for (int j = 0; j < vars.size(); j++)
			if (expr.contains(vars.get(j).name))
				expr = expr.replace((vars.get(j)).name, String.valueOf((vars.get(j)).value));
    	//Create tokens separated by delimiters
    	StringTokenizer tokens = new StringTokenizer(expr, "+-*/()[]", true);
    	ArrayList<String> arr = new ArrayList<String>();
    	while(tokens.hasMoreTokens())
    		arr.add(tokens.nextToken());
    	//Changing negative numbers
		int index = arr.indexOf("-");
    	while (index != -1)
    	{
    		if (isNumeric(arr.get(index + 1)) && (!(index-1 != -1 && (arr.get(index-1).equals("]")))) && (!(index-1 != -1 && (arr.get(index-1).equals(")")))))
    		{
	    		arr.remove(index);
	    		arr.set(index, String.valueOf(Float.valueOf(arr.get(index)) * (-1)));
	    		if ((index - 1 != -1) && isNumeric(arr.get(index - 1)))
	    		{
	    				arr.add(index, "+");
	    				index++;
	    		}
    		}
    		index = getNextMinusSign(arr, index);
    	}
    	//Brackets
    	while (arr.contains("["))
    	{
    		int begin = arr.lastIndexOf("[");
   			int end = getMatchingCloseBracket(arr, begin);
   			String sub = "";
    		for (int i = begin + 1; i < end; i++)
    			sub += (arr.get(i)).toString();
    		arr.add(begin + 1, String.valueOf(evaluate(sub, vars, arrays)));
    		int count = 1;
    		while (count < (end - begin))
    		{
    			arr.remove(begin + 2);
    			count++;
    		}
   			//Replacing Arrays with their values
   			if  (end == begin + 2)
   			{
   				for (int j = 0; j < arrays.size(); j++)
   				{
   					if (arr.get(begin-1).equals(arrays.get(j).name))
   					{
   						arr.add(begin - 1, String.valueOf(arrays.get(j).values[(int)(Math.floor((double)(Float.valueOf(arr.get(begin+1)))))]));
   						arr.remove(begin); //Name
   						arr.remove(begin); //Opening bracket
   						arr.remove(begin); //Value
   						arr.remove(begin); //Closing bracket
   					}
   				}
   			}
    	}
    	//Parentheses
    	while (arr.contains("("))
    	{
    		int begin = arr.lastIndexOf("(");
			int end = getMatchingCloseBracket(arr, begin);
       		String sub = "";
    		for (int i = begin + 1; i < end; i++)
    			sub += (arr.get(i)).toString();
    		arr.add(begin, String.valueOf(evaluate(sub, vars, arrays)));
    		int count = 0;
    		while (count <= (end - begin))
    		{
    			arr.remove(begin + 1);
    			count++;
    		}
		}
    	//Recheck for negative numbers
    	index = arr.indexOf("-");
    	while (index != -1)
    	{
    		if (isNumeric(arr.get(index + 1)) && (!(index-1 != -1 && (arr.get(index-1).equals("]")))) && (!(index-1 != -1 && (arr.get(index-1).equals(")")))))
    		{
	    		arr.remove(index);
	    		arr.set(index, String.valueOf(Float.valueOf(arr.get(index)) * (-1)));
	    		if ((index - 1 != -1) && isNumeric(arr.get(index - 1)))
	    		{
	    				arr.add(index, "+");
	    				index++;
	    		}
    		}
    		index = getNextMinusSign(arr, index);
    	}
    	//Multiply and Divide
    	while (arr.contains("*") || arr.contains("/"))
    	{
    		int at = 0;
    		String prod = "";
    		int mult = arr.indexOf("*");
    		int divide = arr.indexOf("/");
    		if ((divide < mult && divide != -1) || mult == -1 )
    		{
    			at = divide;
	    		prod = String.valueOf(Float.valueOf(arr.get(at-1)) / Float.valueOf(arr.get(at+1)));
    		}
    		else 
    		{
    			at = mult;
	    		prod = String.valueOf(Float.valueOf(arr.get(at-1)) * Float.valueOf(arr.get(at+1)));
    		}
    		arr.add(at - 1, prod);
    		arr.remove(at); 
    		arr.remove(at); 
    		arr.remove(at);
    	}
    	//Add and Subtract
    	while (arr.contains("+") || arr.contains("-"))
    	{
    		int at = 0;
    		String prod = "";
    		int add = arr.indexOf("+");
    		int subtract = arr.indexOf("-");
    		if ((subtract < add && subtract != -1) || add == -1)
    		{
    			at = subtract;
	    		prod = String.valueOf(Float.valueOf(arr.get(at-1)) - Float.valueOf(arr.get(at+1)));
    		}	
    		else 
    		{
    			at = add;
	    		prod = String.valueOf(Float.valueOf(arr.get(at-1)) + Float.valueOf(arr.get(at+1)));
    		}
    		arr.add(at - 1, prod);
    		arr.remove(at); 
    		arr.remove(at); 
    		arr.remove(at);
    	}
    	return Float.valueOf(arr.get(0));
    }
}