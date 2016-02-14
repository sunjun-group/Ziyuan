package microbat.evaluation;

import java.util.ArrayList;
import java.util.List;

import microbat.model.Fault;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.util.Settings;

public class SimulatedUser {

	public void feedback(TraceNode suspiciousNode, Fault rootCause, int checkTime) {
		
		List<VarValue> relevantVariables = findReachingReadVariablesFromSNToRootCause(suspiciousNode, rootCause);
		for(VarValue var: relevantVariables){
			Settings.interestedVariables.add(var.getVarID(), checkTime);
		}
		
	}

	private List<VarValue> findReachingReadVariablesFromSNToRootCause(
			TraceNode suspiciousNode, Fault rootCause) {
		List<VarValue> vars = new ArrayList<>();
		
		search(suspiciousNode, rootCause, vars);
		
		return vars;
	}

	
	
}
