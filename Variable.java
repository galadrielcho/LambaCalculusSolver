
public class Variable implements Expression {

	private String name;
	private boolean free;

	public Variable(String name, boolean free) {
		this.name = name;
		this.free = free;
	}

	public String toString() {
		return name;
	}

	public Boolean isFree() {
		return free;
	}

	public Boolean nameEquals(Variable v) {
		return v.name.equals(this.name);
	}

}
