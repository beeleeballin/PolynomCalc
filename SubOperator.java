/**
 * A subclass of Operator that refers to subtracting polynomials
 */
public class SubOperator extends Operator
{
    /** The operation method of subtracting 2 polynomials */
    public Polynomial operate(Polynomial p1, Polynomial p2)
    {
        return p1.subtraction(p2);
    }
    /** The priority of subtracting */
    public int getPriority()
    {
        return -1;
    }
}
