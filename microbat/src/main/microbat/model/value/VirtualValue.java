package microbat.model.value;

import microbat.model.variable.Variable;

public class VirtualValue extends VarValue {

	public VirtualValue(boolean isRoot, Variable variable) {
		this.isRoot = isRoot;
		this.variable = variable;
	}
	
	@Override
	public boolean isTheSameWith(GraphNode node) {
		return false;
	}

}
