/**
 * A subclass of Operator that refers to adding polynomials
 */
public class AddOperator extends Operator
{
    /** The operation method of adding 2 polynomials */
    public Polynomial operate(Polynomial p1, Polynomial p2)
    {
        return p1.addition(p2);
    }
    /** The priority of adding */
    public int getPriority()
    {
        return -1;
    }
}
