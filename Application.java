
public class Application implements Expression {
    public Expression right;
    public Expression left;

    public Application(Expression r, Expression l) {
        this.right = r;
        this.left = l;
    }

    public String toString() {
        return "(" + this.left + " " + this.right + ")";
    }
}