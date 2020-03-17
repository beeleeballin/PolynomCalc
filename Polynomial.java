import java.util.ArrayList;
import java.util.Collections; //Collection.sort

/**
 * A subclass of Token that instantiates, organizes and operates upon polynomials. It does not have to be instantiated with 
 * any parameter, but a monomial would make it a singleton polynomial object.
 */
public class Polynomial extends Token
{
    
    /** Creates a list of monomials that makes a polynomial */
    ArrayList<Monomial> poly;
    
    /** Defines 0 in the form of a monomial */
    Monomial zero = new Monomial(0,0);
    
    /** Defines -1 in the form of a monomial */
    Monomial neg = new Monomial (-1, 0);
    
    /** Constructor that creates an empty polynomial object */
    public Polynomial(){ 
        poly = new ArrayList<Monomial>();
    }
    /** 
     * Constructor that creates a polynomial object with one monomial 
     * @param m The first monomial in the polynomial object
     */
    public Polynomial(Monomial m){
        poly = new ArrayList<Monomial>();
        poly.add(m);
    }
    
    /** Sorts the monomials in a polynomial into descending order */
    public ArrayList<Monomial> sortPoly(){
        Collections.sort(poly);
        return poly;
    }
    
    /** Removes all extra 0s in a polynomial */
    public Polynomial cleanUp(){ 
        for(int i = 0; i < this.poly.size(); i++){
            if(zero.getC() + this.poly.get(i).getC() == 0){
                this.poly.remove(this.poly.get(i));
                i--;
            }
        }
        return this;
    }
    
    /** Verifies whether a polynomial is 0 */
    public boolean isZero(){
        //remove all zero terms in the input
        if(this.cleanUp().poly.size() == 0) return true;
        else return false;
    }
    
    /** Creates a polynomial duplicate */
    public Polynomial clone(){
        Polynomial clone = new Polynomial();
        for(int i = this.poly.size()-1; i >= 0; i--){
            clone.poly.add(0,this.poly.get(i));
        }
        return clone;
    }
    
    /** 
     * Adds 2 polynomials 
     * @param a The addend to the augend
     * @return sum
     */
    public Polynomial addition(Polynomial a){
        //create a new polynomial designed to be the sum of 'this' and 'a'
        Polynomial addedPoly = new Polynomial();
        
        //operate on the cloned 'this' and 'm' to avoid messing with the original polynomials
        Polynomial x = this.clone(); 
        x.sortPoly();
        Polynomial y = a.clone();
        y.sortPoly();
        
        //scan through all monomials in 'this' to see which one could be combined with a monomial in 'a'
        for(int i = 0; i < x.poly.size(); i++){ 
            //scan through all monomials in until it finds the one that could be combined with THE monomial in this
            for(int j = 0; j < y.poly.size(); j++){ 
                //add() returns null if no operation happens
                if(x.poly.get(i).add(y.poly.get(j)) != null){ 
                    //append the summed monomial onto addedPoly
                    addedPoly.poly.add(0,x.poly.get(i).add(y.poly.get(j))); 
                    //remove the used monomials from 'this' and 'a'
                    x.poly.remove(x.poly.get(i)); 
                    y.poly.remove(y.poly.get(j));
                    
                    //the new monomials at idx == 1 in both 'this' and 'a' need to be visisted
                    i--; 
                    j--;
                    
                    //there should not be another monomial in 'a' that could be combined with a monomial in 'this'
                    //hence move on to save time
                    break; 
                }
            }
        }
        
        //paste on the rest of the monomials in 'this' and 'a'
        while(x.poly.size() != 0){ 
            addedPoly.poly.add(0,x.poly.remove(0)); 
        }
        while(y.poly.size() != 0){ 
            addedPoly.poly.add(0,y.poly.remove(0)); 
        }
        
        //remove 0s (when negative values are added there could be 0s)
        addedPoly.cleanUp();
        
        //sort into order
        addedPoly.sortPoly();
        
        return addedPoly;
    }
    
    /** 
     * Subtracts 2 polynomials (same as adding the additive inverse of the subtrahend)
     * @param s The subtrahend to the minuend
     * @return difference
     */
    public Polynomial subtraction(Polynomial s){
        //create a new Polynomial designed to be the additive inverse of 'this'
        Polynomial sprime = new Polynomial();
        for(int i = s.poly.size()-1; i >= 0 ; i--){
            sprime.poly.add(0,s.poly.get(i).multiply(neg));
        }
        
        //add the original minuend to the additive inverse of the subtrahend
        return this.addition(sprime);
    }
    
    /** 
     * Multiplies 2 polynomials 
     * @param m The multiplier to the multiplicand
     * @return product
     */
    public Polynomial multiplication(Polynomial m){
        //operate on cloned 'this' and 'm' to protect the original polynomials
        Polynomial x = this.clone();
        x.sortPoly();
        Polynomial y = m.clone();
        y.sortPoly();
        
        //create an array of polynomials (ArrayList<Monomial>[]) to store the intermediary polynomials
        //(but maybe we don't need this much memory all the time?)
        Polynomial[] multPoly = new Polynomial[10]; 
        for (int j = 0; j < 10; j++) { 
            multPoly[j] = new Polynomial(); 
        }
        
        //use i to store the number of monomials in 'this'
        int i = 0;
        
        //multiply every term in 'm' with every term in 'this'
        for(i = 0; i < x.poly.size(); i++){ 
            for(int j = 0; j < y.poly.size(); j++){
                multPoly[i].poly.add(0,x.poly.get(i).multiply(y.poly.get(j)));
                /*
                 * System.out.println(x.poly.getClass()); //arraylist of monomials v
                 * System.out.println(x.getClass()); //polynomial v
                 * System.out.println(multPoly.getClass()); //[Lpolynomial??
                 * System.out.println(multPoly[i].getClass()); //polynomial v
                 * System.out.println(multPoly.length); //10 v
                 */
            }
        }
        
        //sum up all the intermediary products of in the process of multiplication
        for(int j = 1; j < i; j++){
            multPoly[0] = multPoly[0].addition(multPoly[j]);
        }
        
        //sort the polynomial in a descending order
        multPoly[0].sortPoly();
        
        return multPoly[0];
    }
    
    /** 
     * Divides 2 polynomials 
     * @param d The divisor to the dividend
     * @return quotient
     */
    public Polynomial division(Polynomial d){
        //create an new Polynomial to store the quotient
        Polynomial divPoly = new Polynomial();
        
        //clone 'this' and input 'd' so the manipulation stays within the function 
        Polynomial x = this.clone();
        x.sortPoly();
        Polynomial y = d.clone();
        y.sortPoly();
        
        //as long as there is dividend and the divisor is not 0, try to divide!
        while((!x.poly.isEmpty())&&(!y.poly.isEmpty())){ 
            
            //find the monomial that will be the constituent of the eventual quotient
            //but if the dividend can no longer be divided by the divisor, it entails a remainder, which we don't bother
            Monomial temp;
            if(x.poly.get(0).divide(y.poly.get(0)) == null) break;
            else temp = x.poly.get(0).divide(y.poly.get(0));
            
            //transform the monomial quotient value to a polynomial form for easy calculation
            Polynomial ptemp = new Polynomial (temp);
            
            //attach the monomial onto the quotient
            divPoly.poly.add(temp);
            
            //create a new dividend to be divided
            x = x.subtraction(y.multiplication(ptemp));
            x.cleanUp();
        }
        
        // return quotient when the dividend is reduced to 0 (division with no remainder)
        // if not, we return null because this is an incomplete division, or dividing by 0.0
        if(x.poly.isEmpty())return divPoly;
        else return null; 
    }
    
    /** Formats the polynomial into a presentable string */
    public String toString(){ 
        String polyString = "";
        for(int i = 0; i < this.poly.size(); i++){
            if(i == 0) polyString = this.poly.get(i).toString();
            else{
                if(this.poly.get(i).toString().charAt(0) == '-'){
                    polyString = polyString+" - "+this.poly.get(i).toString().substring(1);
                }else{
                    polyString = polyString+" + "+this.poly.get(i).toString();
                }
            }
        }
        if(polyString.equals("")) return "0.0";
        else return polyString;
    }
    
    // /** Static main to test this class  */
    // public static void main(String[] args){
        
        // System.out.println("the first polynomial, a, is: ");
        // //hard-coded polynomials
        // Polynomial a = new Polynomial();
        // Monomial a1 = new Monomial(-9, 5);
        // Monomial a2 = new Monomial(3, 2);
        // Monomial a3 = new Monomial(-1, 1);
        // Monomial[] ma = {a1, a2, a3};
        // for(int i = 0; i < ma.length; i++){
            // a.poly.add(ma[i]);
        // }
        // //to.String() automatically applies
        // System.out.println(a);
        // System.out.println("-");
        
        // System.out.println("the second polynomial, b, is: ");
        // Polynomial b = new Polynomial();
        // Monomial b1 = new Monomial(3, 1);
        // Monomial b2 = new Monomial(-2, 5);
        // Monomial b3 = new Monomial(5, 3);
        // Monomial[] mb = {b1, b2, b3};
        // for(int i = 0; i < mb.length; i++){
            // b.poly.add(mb[i]);
        // }
        // System.out.println(b);
        // System.out.println("--");
        
        // //perform polynomial addition
        // Polynomial c = a.addition(b);
        // if(c != null) System.out.println("the sum of a & b is: "+c);
        // else System.out.println("no results");
        // System.out.println("---");

        // //perform polynomial subtraction
        // Polynomial d = c.subtraction(a);
        // if(d != null) System.out.println("the difference of (a+b) & a is: "+d);
        // else System.out.println("no results");
        // System.out.println("which should equal to b: "+b);
        // System.out.println("----");
        
        // //perform polynomial multiplication
        // Polynomial e = a.multiplication(b);
        // if(e != null) System.out.println("the product of a & b is: "+e);
        // else System.out.println("no results");
        // System.out.println("-----");
        
        // //perform polynomial division
        // Polynomial f = e.division(a);
        // if(f != null) System.out.println("the quotient of a*b & a is: "+f);
        // else System.out.println("no results");
        // System.out.println("which should equal to b: "+b);
        // System.out.println("------");
    // }
}
