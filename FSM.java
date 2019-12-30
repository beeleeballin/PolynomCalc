import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Map;

/**
 * A string-to-token parser class that employs the methodology of a finite state machine
 * The class takes no parameters upon instantiation,
 * but its primary method, parsePoly(), takes in a string and a memory to 
 * convert the string into a list of Tokens, and to
 * access the stored objects from the Polynomial class.
 */

public class FSM 
{
    /** Helps store new variable before polynomial calculated */
    char temp = '!';
    
    /** Coefficient accumulators that help instantiate singleton polynomials */
    double coe = 0;
    /** Exponent accumulators that help instantiate singleton polynomials */
    int expo = 0;
    /** Decimal accumulators that help instantiate singleton polynomials */
    int power = 1;
    /** Toggle that help record polynomials that equal 0s */
    boolean zero = false;
    /** Help instantiate negative polynomials */
    Polynomial neg = new Polynomial(new Monomial (-1,0));
    
    /** Constructor */
    public FSM(){
        
    }
    
    /**
     * The FINITE in our finite state machine to parse the expression
     * Has 11 states + 1 error state to parse my string
     */
    enum polyState{
        //
        NEW(false),EQUAL(true),SIGN(false),START(false),DEC(true), SMALL(false), 
        VAR(true), CAR(true), EXP(false), XNEXT(true), ANEXT(true), ERR(false);
        
        private final boolean isAccept;
        polyState(boolean isAccept){this.isAccept = isAccept;}
        public boolean isAccept() {return isAccept;}
    }
    
    /**
     * Helps make sense of the coefficient strings so the polynomials could be instatiated
     * Realized I could use parseDouble() in the Java Double Class later on... but oh well this works too
     * @param o The coefficient value recorded up to this point
     * @param n The new number that should be accounted for
     * @return The updated coefficient value
     */
    public double dten(double o, int n){
        return (10 * o + n);
    }
    /**
     * Helps make sense of the coefficient strings so the polynomials could be instatiated
     * @param o The exponent value recorded up to this point
     * @param n The new number that should be accounted for
     * @return The updated exponent value
     */
    public double tenth(double o, int n, int p){
        double res = n;
        for(int i = 0; i < p; i++){
            res /= 10;
        }
        return (o + res);
    }
    /**
     * Helps make sense of the exponent strings so the polynomials could be instatiated
     * @param o The coefficient value recorded up to this point
     * @param n The new number that should be accounted for
     * @return The updated coefficient value
     */
    public int iten(int o, int n){
        return (10 * o + n);
    }
    
    /**
     * Resets the accumulators needed for polynomial instantiation
     */
    public void resetPolyInput(){
        coe = 0;
        expo = 0;
        power = 1;
        zero = false;
    }
    
    /**
     * Get the private field temp which stores the variable that needs a new associated Polynomial 
     * @return The stored variable
     */
    public char getTemp(){
        return temp;
    }
    
    /**
     * Parses an string expression into workable Tokens
     * @param input The string that needs to be parsed into Tokens
     * @param memory The memory of all the stored Polynomials
     * @return The input string in Token form
     */
    public Deque<Token> parsePoly(String input, Map<Character,Polynomial> memory){
        
        //state begin at NEW
        polyState curState = polyState.NEW;
        
        //help iterate through the expression
        int i = 0;
        
        //help store new variables
        char c = '!';
        
        //help determine if the variable previous to an operator has a stored polynomial value
        Polynomial lastUsed = null;
        
        //list of Tokens to return after parsing
        Deque<Token> all = new ArrayDeque<Token>();
        
        while(curState != polyState.ERR && i < input.length()){
            
            //the charater under examination in this iteration
            c = input.charAt(i++);
            
            //toggle 'true' when a variable has had a stored polynoial value
            boolean used = false;
            if(memory.get(c) != null){
                used = true;
            }
            
            //traverse the states depending 
            switch(curState){
                //many characters could start off the expression 
                //may not end on this state
                case NEW:
                    //a potential new polynomial 
                    this.resetPolyInput();
                    //'x' here means this operand is at least an 1x^1
                    //check whether it is followed by a '^' at which point we can expect an exponent value >= 1
                    if(c == 'x') {
                        if(coe == 0) coe = 1;
                        if(expo == 0) expo = 1;
                        zero = true;
                        
                        curState = polyState.CAR;
                    //'.' here means this operand has a coefficient > 0 but < 1
                    //check whether it is followed by more numbers which makes it an operand with decimaled coefficient
                    }else if(c == '.') {
                        curState = polyState.SMALL;
                    //a number here means this operand has a coefficient value beginning with this number
                    //which means it should eventually become an operand even if it is a 0
                    //check whether it is followed by more numbers which increases its coefficient value
                    }else if((c >= '0')&&(c <= '9')) {
                        zero = true;
                        coe = dten(coe, (c - '0'));
                        
                        curState = polyState.DEC;
                    //'-' here means the operand is negative
                    //so mutliply it by (-1)
                    //check whether it is followed by an operand
                    }else if(c == '-') {
                        all.addLast(neg);
                        all.addLast(new MulOperator());
                        
                        curState = polyState.START;
                    //a letter here suggests a new or old variable
                    //check whether the expression is assigning a value to a variable
                    //or referencing the value of a stored term
                    }else if(((c >= 'a')&&(c <= 'z'))||((c >= 'A')&&(c <= 'Z'))) { 
                        
                        //record the variable to later store as key in the memory
                        if(temp == '!') temp = c;
                        else System.out.println("error. new variable not saved");
                        
                        //store the associated polynomial if it has one
                        //in case this turns out to be not an assignment expression
                        if(used) lastUsed = memory.get(c);
                        
                        
                        curState = polyState.EQUAL;
                    //a left parenthesis means next up should be an operand or more left parenthesis
                    //store the left parenthesis as well
                    //check whether it could be a negative operand
                    }else if(c == '(') {
                        all.addLast(new OpParen());
                        
                        curState = polyState.SIGN;
                    }else curState = polyState.ERR;
                    break;
                //follows a variable declared at the head of an expression 
                //may end on this state
                case EQUAL:
                    //'=' here means this is an assignment expression
                    //forget about its original associated polynomial if there was one
                    //evaluate the expression and store it in the variable
                    //check whether the following operand is a negative one
                    if(c == '=') {
                        lastUsed = null;
                        
                        curState = polyState.SIGN;
                    //an operation sign here means this is an evaluation expression
                    //forget about assigning a value to any variable
                    //if the variable surely has an associated polynomial 
                    //append that onto the list along with the operation sign
                    //and check whether it is followed by an operand
                    }else if(((c == '+')||(c == '-')||(c == '*')||(c == '/'))&&(lastUsed != null)) {
                        temp = '!';
                        all.addLast(lastUsed);
                        lastUsed = null;
                        
                        if(c == '+') all.addLast(new AddOperator());
                        else if(c == '-') all.addLast(new SubOperator());
                        else if(c == '*') all.addLast(new MulOperator());
                        else all.addLast(new DivOperator());
                        
                        curState = polyState.START;
                    }else curState = polyState.ERR;
                    break;
                //follows a left parenthesis or an '='
                //may not end on this state
                case SIGN:
                    //'x' here means this operand is at least an 1x^1
                    //check whether it is followed by a '^' at which point we can expect an exponent value >= 1
                    if(c == 'x') {
                        if(coe == 0) coe = 1;
                        if(expo == 0) expo = 1;
                        zero = true;
                        
                        curState = polyState.CAR;
                    //'.' here means this operand has a coefficient > 0 but < 1
                    //check whether it is followed by more numbers which makes it an operand with decimaled coefficient
                    }else if(c == '.') {
                        curState = polyState.SMALL;
                    //a number here means this operand has a coefficient value beginning with this number
                    //which means it should eventually become an operand even if it is a 0
                    //check whether it is followed by more numbers which increases its coefficient value
                    }else if((c >= '0')&&(c <= '9')) {
                        zero = true;
                        coe = dten(coe, (c - '0'));
                        
                        curState = polyState.DEC;
                    //'-' here means the operand is negative
                    //so mutliply it by (-1)
                    //check whether it is followed by an operand
                    }else if(c == '-') {
                        all.addLast(neg);
                        all.addLast(new MulOperator());
                        
                        curState = polyState.START;
                    //if this character has an associated polynomial
                    //store that polynomial in the list
                    //check whether it is followed by an operation
                    }else if(used) {
                        all.addLast(memory.get(c));
                        
                        curState = polyState.ANEXT;
                    //a left parenthesis means next up should be an operand or more left parenthesis
                    //store the left parenthesis
                    //check whether it could be a negative operand
                    }else if(c == '(') {
                        all.addLast(new OpParen());
                        
                        curState = polyState.SIGN;
                    }else curState = polyState.ERR;
                    break;
                //follows a '-' or any operation 
                //may not end on this state
                case START:
                    //'x' here means this operand is at least an 1x^1
                    //check whether it is followed by a '^' at which point we can expect an exponent value >= 1
                    if(c == 'x') {
                        if(coe == 0) coe = 1;
                        if(expo == 0) expo = 1;
                        zero = true;
                        
                        curState = polyState.CAR;
                    //'.' here means this operand has a coefficient > 0 but < 1
                    //check whether it is followed by more numbers which makes it an operand with decimaled coefficient
                    }else if(c == '.') {
                        curState = polyState.SMALL;
                    //a number here means this operand has a coefficient value beginning with this number
                    //which means it should eventually become an operand even if it is a 0
                    //check whether it is followed by more numbers which increases its coefficient value
                    }else if((c >= '0')&&(c <= '9')) {
                        curState = polyState.DEC;
                        zero = true;
                        
                        coe = dten(coe, (c - '0'));
                    //if this character has an associated polynomial
                    //store that polynomial in the list
                    //check whether it is followed by an operation
                    }else if(used) {
                        all.addLast(memory.get(c));
                        
                        curState = polyState.ANEXT;
                    //a left parenthesis means next up should be an operand or more left parenthesis
                    //store the left parenthesis
                    //check whether it could be a negative operand
                    }else if(c == '(') {
                        all.addLast(new OpParen());
                        
                        curState = polyState.SIGN;
                    }else curState = polyState.ERR;
                    break;
                //follows a number that should be a coefficient
                //may not end on this state
                case DEC:
                    //'x' here means this operand is at least an (coe)x^1
                    //check whether it is followed by a '^' at which point we can expect an exponent value >= 1
                    if(c == 'x') {
                        if(expo == 0) expo = 1;
                        
                        curState = polyState.CAR;
                    //'.' here means this operand has a coefficient with a decimal point
                    //check whether it is followed by more numbers which makes it an operand with decimaled coefficient
                    }else if(c == '.') {
                        curState = polyState.SMALL;
                    //a number here means this operand has coefficient value with this number in it
                    //check whether it is followed by more numbers which increases its coefficient value
                    }else if((c >= '0')&&(c <= '9')) {
                        coe = dten(coe, (c - '0'));
                        curState = polyState.DEC;
                    //if this character has an associated polynomial
                    //store the coefficient value recorded thus far as an operand 
                    //throw in a multiplication operation
                    //then store that polynomial in the list
                    //lastly check whether it is followed by an operation
                    }else if(used) { 
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        all.addLast(new MulOperator());
                        all.addLast(memory.get(c));
                        
                        curState = polyState.ANEXT;
                    //an operation sign here means this is the end of an operand
                    //store the coefficient value recorded thus far as an operand 
                    //throw in the operation
                    //and check whether it is followed by an operand
                    }else if((c == '+')||(c == '-')||(c == '*')||(c == '/')) {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        if(c == '+') all.addLast(new AddOperator());
                        else if(c == '-') all.addLast(new SubOperator());
                        else if(c == '*') all.addLast(new MulOperator());
                        else all.addLast(new DivOperator());
                        
                        curState = polyState.START;
                    //a left parenthesis means that this coefficient should be 
                    //multiplied by another operand
                    //which should be next up or it is another left parenthesis
                    //store the coefficient
                    //throw in a multiplication operation
                    //store the left parenthesis
                    //and check whether the next could be a negative operand
                    }else if(c == '(') {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        all.addLast(new MulOperator());
                        
                        all.addLast(new OpParen());
                        
                        curState = polyState.SIGN;
                    //a right parenthesis here means the end of an operand
                    //store the coefficient value recorded thus far as an operand
                    //store the right parenthesis as well
                    //check whether it is followed by an operation
                    }else if(c == ')') { 
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        all.addLast(new CloseParen());
                        
                        curState = polyState.ANEXT;
                    }else curState = polyState.ERR;
                    break;
                //follows a '.'
                //may not end on this state
                case SMALL:
                    //a number here means this operand has coefficient value with this number in it
                    //check whether it is followed by more numbers which increases its coefficient value
                    if((c >= '0')&&(c <= '9')) {
                        coe = tenth(coe,(c - '0'),power++);
                        
                        curState = polyState.VAR;
                    }else curState = polyState.ERR;
                    break;
                //follows a number after the decimal point
                //may end on this state
                case VAR:
                    //'x' here means this operand is at least an (coe)x^1
                    //check whether it is followed by a '^' at which point we can expect an exponent value >= 1
                    if(c == 'x') {
                        if(expo == 0) expo = 1;
                        
                        curState = polyState.CAR;
                    //a number here means this operand has coefficient value with this number in it
                    //check whether it is followed by more numbers which increases its coefficient value
                    }else if((c >= '0')&&(c <= '9')) {
                        coe = tenth(coe,(c - '0'),power++);
                        
                        curState = polyState.VAR;
                    //if this character has an associated polynomial
                    //store the coefficient value recorded thus far as an operand 
                    //throw in a multiplication operation
                    //then store that polynomial in the list
                    //lastly check whether it is followed by an operation    
                    }else if(used) {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        all.addLast(new MulOperator());
                        all.addLast(memory.get(c));
                        
                        curState = polyState.ANEXT;
                    //an operation sign here means this is the end of an operand
                    //store the coefficient value recorded thus far as an operand 
                    //throw in the operation
                    //and check whether it is followed by an operand
                    }else if((c == '+')||(c == '-')||(c == '*')||(c == '/')) {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        if(c == '+') all.addLast(new AddOperator());
                        else if(c == '-') all.addLast(new SubOperator());
                        else if(c == '*') all.addLast(new MulOperator());
                        else all.addLast(new DivOperator());
                        
                        curState = polyState.START;
                    //a left parenthesis means that this coefficient should be 
                    //multiplied by another operand
                    //which should be next up or it is another left parenthesis
                    //store the coefficient
                    //throw in a multiplication operation
                    //store the left parenthesis
                    //and check whether the next could be a negative operand
                    }else if(c == '(') {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        all.addLast(new MulOperator());
                        
                        all.addLast(new OpParen());
                        
                        curState = polyState.SIGN;
                    //a right parenthesis here means the end of an operand
                    //store the coefficient value recorded thus far as an operand
                    //store the right parenthesis as well
                    //check whether it is followed by an operation
                    }else if(c == ')') {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        all.addLast(new CloseParen());
                        
                        curState = polyState.ANEXT;
                    }else curState = polyState.ERR;
                    break;
                //follows an 'x' leads it into inputting exponent value or another operand
                //may end on this state
                case CAR:
                    //'^' here means the exponent value will be altered
                    //check whether it is followed by a number
                    if(c == '^') {
                        expo = 0;
                        
                        curState = polyState.EXP;
                    //an operation sign here means this is the end of an operand
                    //store the coefficient and exponent values recorded thus far as an operand 
                    //throw in the operation
                    //and check whether it is followed by an operand
                    }else if((c == '+')||(c == '-')||(c == '*')||(c == '/')) {
                        
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        if(c == '+') all.addLast(new AddOperator());
                        else if(c == '-') all.addLast(new SubOperator());
                        else if(c == '*') all.addLast(new MulOperator());
                        else all.addLast(new DivOperator());
                        
                        curState = polyState.START;
                    //a left parenthesis means that this operand should be 
                    //multiplied by another operand
                    //which should be next up or it is another left parenthesis
                    //store the operand
                    //throw in a multiplication operation
                    //store the left parenthesis
                    //and check whether the next could be a negative operand
                    }else if(c == '(') {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        all.addLast(new MulOperator());
                        
                        all.addLast(new OpParen());
                        
                        curState = polyState.SIGN;
                    //a right parenthesis here means the end of an operand
                    //store the coefficient and exponent value recorded thus far as an operand
                    //store the right parenthesis as well
                    //check whether it is followed by an operation
                    }else if(c == ')') {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        all.addLast(new CloseParen());
                        
                        curState = polyState.ANEXT;
                    }else curState = polyState.ERR;
                    break;
                //follows '^'
                //may not end on this state
                case EXP: //can't take parenthesis values??
                    //a number here means the exponent value will be altered
                    //check whether it is followed by another number or operation
                    if((c >= '0')&&(c <= '9')) {
                        expo = iten(expo, (c - '0'));
                        
                        curState = polyState.XNEXT;
                    }else curState = polyState.ERR;
                    break;
                //follows a exponent number
                //may end on this state
                case XNEXT:
                    //a number here means the exponent value will be altered
                    //check whether it is followed by another number or operation
                    if((c >= '0')&&(c <= '9')) {
                        curState = polyState.XNEXT;
                        
                        expo = iten(expo, (c - '0'));
                    //an operation sign here means this is the end of an operand
                    //store the coefficient and exponent values recorded thus far as an operand 
                    //throw in the operation
                    //and check whether it is followed by an operand
                    }else if((c == '+')||(c == '-')||(c == '*')||(c == '/')) {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        if(c == '+') all.addLast(new AddOperator());
                        else if(c == '-') all.addLast(new SubOperator());
                        else if(c == '*') all.addLast(new MulOperator());
                        else all.addLast(new DivOperator());
                        
                        curState = polyState.START;
                    //a left parenthesis means that this operand should be 
                    //multiplied by another operand
                    //which should be next up or it is another left parenthesis
                    //store the operand
                    //throw in a multiplication operation
                    //store the left parenthesis
                    //and check whether the next could be a negative operand
                    }else if(c == '(') {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        all.addLast(new MulOperator());
                        
                        all.addLast(new OpParen());
                        
                        curState = polyState.SIGN;
                    //a right parenthesis here means the end of an operand
                    //store the coefficient and exponent value recorded thus far as an operand
                    //store the right parenthesis as well
                    //check whether it is followed by an operation
                    }else if(c == ')') {
                        all.addLast(new Polynomial(new Monomial(coe, expo)));
                        this.resetPolyInput();
                        
                        all.addLast(new CloseParen());
                        
                        curState = polyState.ANEXT;
                    }else curState = polyState.ERR;
                    break;
                //follows an operand, including monomial, right parentheses and variables
                //may end on this state
                case ANEXT:
                    //'x' here means this operand is at least an 1x^1
                    //check whether it is followed by a '^' at which point we can expect an exponent value >= 1
                    if(c == 'x') {
                        if(coe == 0) coe = 1;
                        if(expo == 0) expo = 1;
                        zero = true;
                        
                        curState = polyState.CAR;
                    //'.' here means this operand has a coefficient > 0 but < 1
                    //check whether it is followed by more numbers which makes it an operand with decimaled coefficient
                    }else if(c == '.') {
                        curState = polyState.SMALL;
                    //a number here means this operand is going to be multiplied to
                    //another operand
                    //toggle the switch to ensure this number can be instantiated
                    //throw in a multiplier operation
                    //check whether it is followed by more numbers which increases its coefficient value
                    }else if((c >= '0')&&(c <= '9')) {
                        all.addLast(new MulOperator());
                        zero = true;
                        coe = dten(coe, (c - '0'));
                        
                        curState = polyState.DEC;
                    //if this character has an associated polynomial
                    //store that polynomial in the list
                    //check whether it is followed by an operation
                    }else if(used) {
                        all.addLast(memory.get(c));
                        
                        curState = polyState.ANEXT;
                    //an operation sign here means the operand is finalized so just store the operation
                    //check whether it is followed by an operand
                    }else if((c == '+')||(c == '-')||(c == '*')||(c == '/')) {
                        
                        if(c == '+') all.addLast(new AddOperator());
                        else if(c == '-') all.addLast(new SubOperator());
                        else if(c == '*') all.addLast(new MulOperator());
                        else all.addLast(new DivOperator());
                        
                        curState = polyState.START;
                    //a left parenthesis means that this operand should be 
                    //multiplied by another operand
                    //which should be next up or it is another left parenthesis
                    //store the coefficient
                    //throw in a multiplication operation
                    //store the left parenthesis
                    //and check whether the next could be a negative operand
                    }else if(c == '(') {
                        
                        all.addLast(new MulOperator());
                        
                        all.addLast(new OpParen());
                        
                        curState = polyState.SIGN;
                    //a right parenthesis here means the end of another sub-expression
                    //store the right parenthesis
                    //check whether it is followed by an operation
                    }else if(c == ')') {
                        all.addLast(new CloseParen());
                        
                        curState = polyState.ANEXT;
                    }else curState = polyState.ERR;
                    break;
            }
        }
        //by the end of reading the expression
        //a term in creation could seem like it has a value of zero, but it is still a Token
        if(zero){  
            //System.out.println("loose end tie up: "+coe+expo+neg+power);  
            all.addLast(new Polynomial(new Monomial(coe, expo)));
            this.resetPolyInput();
        }
        if((i==1)&&(lastUsed != null)){
            all.addLast(lastUsed);
        }
        
        //debugging tool, but should be commented out if a meaningful return is desired!
        //int size = all.size();
        //System.out.println("infix length is "+size);
        //for(int m = 0; m < size; m++){
        //    System.out.println("m is: "+m+" and it is a "+all.removeFirst());
        //}
        //System.out.println("FSM ends");
        
        //is there any token to pass on and did we end in an accepting state?
        if(all.size() == 0){
            return null;
        }else if (curState.isAccept()){
            return all;
        }else return null;
    }
    
    /** Static main to test this class */
    public static void main(String[] args){
        PolyCalc p = new PolyCalc();
        FSM t = new FSM();
        String test1 = "-1.1x^1*(-.1x)+x+1.1*(1x^1/1)";
        String test2 = "((-.1x^11)*(-1x)+x/11.0*1x^1/1)"; // has remainder
        String test3 = "(-1x)-(-.1x^11+x/1x^1+11)";
        String test4 = "-1x";
        t.parsePoly(test1, p.storage);
        if(t.parsePoly(test2, p.storage) == null) System.out.println("faulty: not parsable");
        else System.out.println("parsing was great");
    }
}
