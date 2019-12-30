import java.util.Scanner;

/**
 * A class that instantiates, compares, and operates upon monomials. It needs to be instantiated with 
 * a coefficient and a exponent.
 */
public class Monomial implements Comparable<Monomial>
{
    /** Real number coefficients */
    private double c; 
    /** Natural number exponents */
    private int e; 
    
    /** 
     * Constructor
     * @param x Real number coefficients
     * @param y Natural number exponents
     */
    public Monomial(double x, int y)
    {
        c = x;
        e = y;
    }
    
    /** 
     * Accesses the coefficient of the monomial
     * @return the coefficient
     */
    public double getC(){
        return this.c;
    }
    /** 
     * Accesses the exponent of the monomial
     * @return the exponent
     */
    public int getE(){
        return this.e;
    }
    
    /** 
     * Formats the monomials into printable objects 
     * @return monomial String
     */
    public String toString(){
        String s = "";
        if((c == 0)||(e == 0)) return s + c;
        if(e == 1) return c + "x";
        else return c + "x^" + e;
    }
    
    /** 
     * Adds 2 monomials 
     * @param x The addend to the augend
     * @return sum
     */
    public Monomial add(Monomial x){ 
        //attempts to congregate 'this' with 'x', but could be unsuccessful
        if(this.e == x.e){
            //give a new monomial if the terms could be grouped
            Monomial z = new Monomial((this.c + x.c), x.e);
            return z; 
        }else{
            //null if no calculation was done
            return null; 
        }
    }
    /** 
     * Subtracts 2 monomials (I don't need this...)
     * @param x The subtrahend to the minuend
     * @return difference
     */
    public Monomial substract(Monomial x){ 
        //attempts to congregate 'this' with 'x'
        if(this.e == x.e){
            Monomial z = new Monomial((this.c - x.c), x.e);
            return z; 
        }else return null; 
    }
    /** 
     * Multiplies 2 monomials 
     * @param x The multiplier to the multiplicand
     * @return product
     */
    public Monomial multiply(Monomial x){ 
        //multiplies 'this' by x, assigns product to a new monomial
        Monomial z = new Monomial((this.c * x.c), (this.e + x.e));
        return z; 
    }
    /** 
     * Divides 2 monomials 
     * @param x The divisor to the dividend
     * @return quotient
     */
    public Monomial divide(Monomial x){ 
        //attempts to divide 'this' by 'x'. could be unsuccessful
        if(((this.c % x.c) == 0)&&(this.e >= x.e)){
            Monomial z = new Monomial((this.c / x.c), (this.e - x.e));
            return z;
        }else return null;
    }
    
    /** 
     * Compares 2 monomials and return an int for sorting purpose (Collections.sort())
     * @param m The monomial to compare
     * @return -1, 0, or 1
     */
    public int compareTo(Monomial m){
        // 1 if this < e, 0 if this == e, -1 if this > e
        return (this.e > m.e ? -1 : 
                (this.e == m.e ? 0 : 1));
    }
    
    /** Static main to test this class */
    public static void main(String[] args){
        while(true){
            Scanner s = new Scanner(System.in);
            
            //create monomials
            System.out.println("first monomial please");
            double a1 = s.nextDouble();
            int a2 = s.nextInt();
            Monomial a = new Monomial(a1, a2);
            System.out.println("second monomial please");
            double b1 = s.nextDouble();
            int b2 = s.nextInt();
            Monomial b = new Monomial(b1, b2);
            
            //print monomials to verify constructor. toString() is implied
            System.out.println(a);
            System.out.println(b);
            
            //print adjusted monomials to verify operative methods
            System.out.print("this is their sum: ");
            if(a.add(b)!=null) System.out.println(a.add(b));
            else System.out.println("not operated");
            System.out.print("this is their difference: ");
            if(a.substract(b)!=null)System.out.println(a.substract(b));
            else System.out.println("not operated");
            System.out.print("this is their product: ");
            if(a.multiply(b)!=null)System.out.println(a.multiply(b));
            else System.out.println("not operated");
            System.out.print("this is their quotient: ");
            if(a.divide(b)!=null)System.out.println(a.divide(b));
            else System.out.println("not operated");
            
            //implement an opportunity to repeat the program. exit if reponds "no", but run again otherwise
            System.out.println("would you like to continue?");
            String q = s.next(); //why can't I used s.nextLine()?
            if(q.equals("no")) break;
        }
        System.out.println("Cool right? bye");
    }
}
