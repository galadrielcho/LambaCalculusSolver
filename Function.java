
public class Function implements Expression {

    public Variable parameter;
    public Expression expression;

    public Function(Variable v, Expression e) {
        this.parameter = v;
        this.expression = e;
    }

    public String toString() {
        return String.format("(λ%s.%s)", parameter, expression);
    }

}
