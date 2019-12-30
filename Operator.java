/**
 * An abstract subclass of Token that refers to operators
 */
public abstract class Operator extends Token
{
    /** The operation method of this class */
    public abstract Polynomial operate(Polynomial p1, Polynomial p2);

    /** The priority of this class */
    public abstract int getPriority();

}