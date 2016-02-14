package microbat.model;

import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;

public class Fault {
	private TraceNode buggyNode;
	private VarValue causingVariable;
	
	public Fault(TraceNode buggyNode, VarValue causingVariable) {
		super();
		this.buggyNode = buggyNode;
		this.causingVariable = causingVariable;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Fault){
			Fault thatFault = (Fault)obj;
			if(buggyNode.equals(thatFault.buggyNode) && 
					causingVariable.getVarID().equals(thatFault.causingVariable.getVarID())){
				return true;
			}
		}
		
		return false;
	}

	public TraceNode getBuggyNode() {
		return buggyNode;
	}

	public void setBuggyNode(TraceNode buggyNode) {
		this.buggyNode = buggyNode;
	}

	public VarValue getCausingVariable() {
		return causingVariable;
	}

	public void setCausingVariable(VarValue causingVariable) {
		this.causingVariable = causingVariable;
	}
	
	
}
