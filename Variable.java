
public class Variable implements Expression {

	private String name;

	public Variable(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Expression e) {
		return this.name.equals(e.toString());
	}

}
