package microbat.model;

public class InterestedVariable {
	private String className;
	private int lineNumber;
	
	/**
	 * this field corresponds to ExecValue.varId
	 */
	private String variableID;
	
	public InterestedVariable(String className, int lineNumber,
			String variableID) {
		super();
		this.className = className;
		this.lineNumber = lineNumber;
		this.variableID = variableID;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result + lineNumber;
		result = prime * result
				+ ((variableID == null) ? 0 : variableID.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "InterestedVariable [className=" + className + ", lineNumber="
				+ lineNumber + ", variableID=" + variableID + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InterestedVariable other = (InterestedVariable) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		if (variableID == null) {
			if (other.variableID != null)
				return false;
		} else if (!variableID.equals(other.variableID))
			return false;
		return true;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getVariableID() {
		return variableID;
	}

	public void setVariableID(String variableID) {
		this.variableID = variableID;
	}
	
	
}
