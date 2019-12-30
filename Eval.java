import java.util.Deque;
import java.util.ArrayDeque;

/**
 * A class that evaluates a list of Tokens. It takes no parameters, but sol() takes in 
 * a Token list and returns a Polynomial.
 */
public class Eval
{
    /** Constuctor */
    public Eval(){
        
    }
    
    /**
     * Evaluates the expression in postfix order
     * @param postfix A Token list in postfix order
     * @return A result Polynomial
     */
    public Polynomial sol(Deque<Token> postfix){
        //create a LIFO queue to operate the operands
        Deque<Token> stack = new ArrayDeque<Token>();
        
        //define 0 in the form of a polynomial 
        Polynomial zero = new Polynomial(new Monomial(0,0));
        
        //go through the entire postfix expression
        while(postfix.peekFirst() != null){
            //transfer the operands onto the LIFO queue until an operator is presented
            if (postfix.peekFirst() instanceof Polynomial){
                stack.addLast(postfix.removeFirst());
            //operate on the top 2 operands when presented an operator
            }else if (postfix.peekFirst() instanceof Operator){
                try{
                    //would I need to worry about an empty LIFO queue?
                    Polynomial p2 = (Polynomial) stack.removeLast();
                    Polynomial p1 = (Polynomial) stack.removeLast();
                    //calculate using add, sub, mul, or div operators with methods of the same name due to polymorphism
                    Operator calc = (Operator) postfix.removeFirst();
                    Polynomial result = calc.operate(p1, p2);
                    
                    //store the evaluated value back on the LIFO queue until all operators are read
                    stack.addLast(result);
                }catch(NullPointerException E){
                    //System.out.println("EVAL caught exception");
                    return null;
                }
            //should be impossible to get something else in here
            }else {
                System.out.println("how the heck did you get something other than operands and operators in here?");
                return null;
            }
        }
        
        //the result should be a polynomial 
        return (Polynomial) stack.removeLast();
    }
    
    /** Static main to test this class */
    public static void main(String[] args){
        PolyCalc poly = new PolyCalc();
        FSM f = new FSM();
        Deque<Token> infix = f.parsePoly("-1.1x^1*(-.1x)+x+1.1*(1x^1/1)",poly.storage);
        PostfixConverter post = new PostfixConverter();
        Deque<Token> postfix = post.convert(infix);
        Eval e = new Eval();
        Polynomial test = e.sol(postfix);
        
        if(test == null) System.out.println("faulty: not evaluable");
        else System.out.println(test);
    }
}
