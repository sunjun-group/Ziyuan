package microbat.model;

import microbat.model.value.VarValue;
import microbat.util.MicroBatUtil;

public class InterestedVariable {
	/**
	 * The location means that the root of this variable is reachable (and defined) at 
	 * <code>lineNumber</code> of <code>className</code>
	 */
	private String visitingClassName;
	private int lineNumber;
	
	
	private VarValue variable;
	
	public InterestedVariable(String className, int lineNumber,
			VarValue variable) {
		super();
		this.visitingClassName = className;
		this.lineNumber = lineNumber;
		this.variable = variable;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((visitingClassName == null) ? 0 : visitingClassName.hashCode());
		result = prime * result + lineNumber;
		result = prime * result
				+ ((variable.getVarName() == null) ? 0 : variable.getVarName().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "InterestedVariable [className=" + visitingClassName + ", lineNumber="
				+ lineNumber + ", variable=" + variable.getVarName() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof InterestedVariable){
			InterestedVariable that = (InterestedVariable)obj;
			boolean isTheSame = MicroBatUtil.isTheSameVariable(this, that);
			
//			if(this.variable.getVarName().equals("flag") && that.getVariable().getVarName().equals("flag")){
//				System.currentTimeMillis();
//			}
			
			return isTheSame;
		}
		
		return false;
	}


	public String getVisitingClassName() {
		return visitingClassName;
	}

	public void setVisitingClassName(String className) {
		this.visitingClassName = className;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public VarValue getVariable() {
		return variable;
	}

	public void setVariable(VarValue variable) {
		this.variable = variable;
	}
	
	
}
