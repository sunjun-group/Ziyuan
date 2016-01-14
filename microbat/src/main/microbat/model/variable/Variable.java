package microbat.model.variable;

public abstract class Variable {
	protected String type;
	protected String variableName;

	public Variable(String name, String type){
		this.variableName = name;
		this.type = type;
	}
	
	public String getName() {
		return variableName;
	}

	public void setName(String variableName) {
		this.variableName = variableName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public abstract String getSimpleName();
}
