import java.util.Deque;
import java.util.ArrayDeque;

/**
 * A class that reorganizes a list of Tokens and allows it to be evaluated more efficiently by an evaluator class.
 * It takes no parameters, but convert() takes in a Token list and organizes it into a new one.
 */
public class PostfixConverter {
    
    /** Constuctor */
    public PostfixConverter(){
        
    }
    
    /**
     * Organizes the input expression with an infix order into a postfix expression
     * @param infix A Token list in infix order
     * @return A Token list in postfix order
     */
    public Deque<Token> convert(Deque<Token> infix){
        
        //create list to store expression in postfix order
        Deque<Token> post = new ArrayDeque<Token>();
        
        //create a LIFO queue to organize operators and parentheses
        Deque<Token> stack = new ArrayDeque<Token>();
        
        //go through the entire infix expression
        while(infix.peekFirst() != null){
            //transfer the operands directly onto the postfix queue because that is how the expression works
            if (infix.peekFirst() instanceof Polynomial) {
                post.addLast(infix.removeFirst());
            //transfer the left parenthesis onto the LIFO queue and wait for a right parenthesis
            }else if (infix.peekFirst() instanceof OpParen) {
                stack.addLast(infix.removeFirst());
            //the right parenthesis does not get transfered, but initiates a unloading effect on the LIFO queue
            }else if (infix.peekFirst() instanceof CloseParen){ 
                //toggles 'true' when a left parenthesis is found
                boolean through = false;
                
                //keep popping off items in the LIFO queue until a left parenthesis is found
                while(!stack.isEmpty()){
                    //transfer operators onto the postfix queue
                    if(stack.peekLast() instanceof Operator){
                        try{
                            post.addLast(stack.removeLast());
                        //likely not helpful
                        }catch(java.util.NoSuchElementException E){ 
                            
                            System.out.println("1 or more missing left parenthesis");
                            //end evaluation due to faulty token list
                            return null;
                        }
                    //remove left parenthesis from the LIFO queue
                    }else{ 
                        stack.removeLast();
                        through = true;
                        break;
                    }
                }
                
                //if a left parenthesis was found and removed from the LIFO queue, then the right parenthesis could also be removed
                if(through){
                    infix.removeFirst();
                }else{
                    System.out.println("1 or more missing left parenthesis");
                    return null;
                }
            //transfer the operator onto the LIFO or postfix queue depending on the its relationship with other operators
            }else{ 
                //keep checking if the LIFO queue has operators
                while(!stack.isEmpty()){
                    if(stack.peekLast() instanceof Operator){
                        //compare the priority of the input and the LIFO operators to see which one goes in postfix queue first
                        try{
                            Operator top = (Operator)infix.peekFirst();
                            Operator sec = (Operator)stack.peekLast();
                            if (top.getPriority() <= sec.getPriority()){
                                post.addLast(stack.removeLast());
                            }else break;
                        //do I need this?
                        }catch(java.lang.ClassCastException E){ 
                            System.out.println("The next item is not an operator on stack");
                            
                            return null;
                        }
                    }else break;
                }
                
                //transfer the operator onto the LIFO queue if it has a lower priority
                stack.addLast(infix.removeFirst());
            }
        }
        
        //transfer the operators in the LIFO onto the postfix expression once the infix queue is emptied
        while (!stack.isEmpty()){
            //should not find more left parenthesese
            if (stack.peekLast() instanceof OpParen) {
                System.out.println("1 or more extra left parenthesis");
                return null;
            }else post.addLast(stack.removeLast());
        }
        
        //debugging tool to check the order of the postfix queue
        //int size = post.size();
        //System.out.println("postfix length is "+size);
        // for(int m = 0; m < size; m++){
            // System.out.println("m is: "+m+" and it is a "+post.removeFirst());
        // }
        
        return post;
    }
    
    /** Static main to test this class */
    public static void main(String[] args){
        PolyCalc poly = new PolyCalc();
        FSM f = new FSM();
        Deque<Token> infix = f.parsePoly("-1.1x^1*(-.1x)+x+1.1*(1x^1/1)", poly.storage);
        
        PostfixConverter post = new PostfixConverter();
        if(post.convert(infix) == null) System.out.println("faulty: not convertable");
        else System.out.println("conversion was smooth");
    }
}
