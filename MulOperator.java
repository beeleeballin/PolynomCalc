/**
 * A subclass of Operator that refers to multiplying polynomials
 */
public class MulOperator extends Operator
{
    /** The operation method of multiplying 2 polynomials */
    public Polynomial operate(Polynomial p1, Polynomial p2)
    {
        return p1.multiplication(p2);
    }
    /** The priority of multiplying */
    public int getPriority()
    {
        return 1;
    }
}