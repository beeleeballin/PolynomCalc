import java.util.Map;
import java.util.Iterator; 
import java.util.Deque;

/**
 * A class takes in an arithmetic expression and a memory, prints Polynomials and stores them in variables
 */
public class Execute 
{
    /** The string to evaluate */
    private String input = "";
    /** The memory to access variables and polynomials */
    Map storage;
    
    /** 
     * Constuctor
     * @param i The arithmetic expression string seek to evaluate
     * @param memory The memory of all the stored Polynomials
     */
    public Execute(String i, Map<Character,Polynomial> memory){
        input = i;
        storage = memory;
    }
    
    /**
     * Parsess, organizes and evaluates the input expression
     */
    public void run(){
        //test if the expression could be appropriately parsed
        FSM parser = new FSM();
        Deque<Token> infix = parser.parsePoly(input, storage);
        if(infix == null) {
            System.out.print("Did you accidently keyed in an invalid arithmetic expression, ");
            System.out.println("such as an invalid operator or operand?");
            return;
        }
        
        //test if the parsed expression could be sorted into an evaluatable order
        PostfixConverter organizer = new PostfixConverter();
        Deque<Token> postfix = organizer.convert(infix);
        if(postfix == null) {
            System.out.println("Did you accidently keyed in a mismatching parenthesis?");
            return;
        }
        
        //test if the ordered expression could be operated properly and return a single polynomial
        Eval res = new Eval();
        Polynomial end = res.sol(postfix);
        if(end == null) {
            System.out.println("Cannot divide by 0!");
            System.out.print("Or this is a valid arithmetic expression,");
            System.out.println("but sorry our program does not allow remainders when dividing....");
            return;
        }
        
        //store or update the calculated variable into the memory for future references
        if(parser.getTemp() != '!') {
            storage.put(parser.getTemp(), end); 
            System.out.print("Variable "+parser.getTemp()+" is saved as: "+end);
        }else System.out.print("Your result is "+end);
        
        //EXTRA FEATURE! show all the stored variables that have an equivalent result as the result
        Iterator<Map.Entry<Character,Polynomial>> itr = storage.entrySet().iterator();
        int counter = 0;
        while(itr.hasNext()) 
        { 
             Map.Entry<Character,Polynomial> storedValue = itr.next(); 
             Polynomial diff = end.subtraction(storedValue.getValue());
             if((diff.isZero())&&(storedValue.getKey()!= parser.getTemp())){
                 if(counter == 0){
                     System.out.print(", which is equal to variables "+storedValue.getKey());
                     counter++;
                 }else System.out.print(", "+storedValue.getKey());
             }
        } 
        System.out.println("");
    }
    
    /** Static main to test this class */
    public static void main(String[] args){
        PolyCalc p = new PolyCalc();
        Execute e = new Execute("-1.1x^1*(-.1x)+x+1.1*(1x^1/1)", p.storage);
        e.run();
    }
}
