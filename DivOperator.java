/**
 * A subclass of Operator that refers to dividing polynomials
 */
public class DivOperator extends Operator
{
    /** The operation method of dividing 2 polynomials */
    public Polynomial operate(Polynomial p1, Polynomial p2)
    {
        return p1.division(p2);
    }
    /** The priority of dividing */
    public int getPriority()
    {
        return 1;
    }
}