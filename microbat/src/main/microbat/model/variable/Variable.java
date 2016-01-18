package microbat.model.variable;

public abstract class Variable {
	protected String type;
	protected String variableName;
	
	/**
	 * The id of an object (non-primitive type) is its object id. 
	 * For primitive type:
	 * if it is a field, its id is: its parent's object id + field name;
	 * if it is an array element, its id is: its parent's object id + index;
	 * if it is a local variable, its id is: its scope (i.e., class[startLine, endLine]) + variable name.
	 * if it is a virtual variable, its id is: "virtual var" + the order of the relevant return-trace-node. 
	 */
	protected String varID;

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
	
	public String getVarID() {
		return varID;
	}

	public void setVarID(String varID) {
		this.varID = varID;
	}

	public abstract String getSimpleName();
}
