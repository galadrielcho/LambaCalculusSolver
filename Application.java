// Written by Galadriel Cho and Annie Li


public class Application implements Expression {
    public Expression right;
    public Expression left;

    public Application(Expression l, Expression r) {
        this.right = r;
        this.left = l;
    }

    public String toString() {
        return "(" + this.left + " " + this.right + ")";
    }
    
}