
public class Function implements Expression {

    public Variable v;
    public Expression e;

    public Function(Variable v, Expression e) {
        this.v = v;
        this.e = e;
    }

    public String toString() {
        return String.format("(λ%s.%s)", v, e);
    }

}
