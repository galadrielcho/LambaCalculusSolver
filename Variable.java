
public class Variable implements Expression {

	private String name;
	private String type;

	public Variable(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String toString() {
		return name;
	}

	public String type() {
		return this.type;
	}

	public Boolean nameEquals(Variable v) {
		return v.name.equals(this.name);
	}

}
