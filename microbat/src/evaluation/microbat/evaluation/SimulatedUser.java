package microbat.evaluation;

import java.util.List;
import java.util.Map;

import microbat.model.Fault;
import microbat.model.trace.TraceNode;
import microbat.util.Settings;

public class SimulatedUser {

	public void feedback(TraceNode suspiciousNode, Fault rootCause, int checkTime) {
		
		if(rootCause.getBuggyNode().getOrder() > suspiciousNode.getOrder()){
			
		}
		else{
			String readVarID = findReachingReadVariablesFromSuspiciousNodeToRootCause(suspiciousNode, rootCause);
			
			if(readVarID != null){
				Settings.interestedVariables.add(readVarID, checkTime);		
				
				if(!suspiciousNode.getWrittenVariables().isEmpty()){
					String writtenVarID = suspiciousNode.getWrittenVariables().get(0).getVarID();
					Settings.interestedVariables.add(writtenVarID, checkTime);
				}
			}
			else{
				System.err.println("Cannot find the relevant causing var");
			}
			
		}
		
		
	}

	private String findReachingReadVariablesFromSuspiciousNodeToRootCause(
			TraceNode suspiciousNode, Fault rootCause) {
		
		Map<TraceNode, List<String>> dominatorMap = suspiciousNode.getDominator();
		
		for(TraceNode dominator: dominatorMap.keySet()){
			if(dominator.equals(rootCause.getBuggyNode())){
				List<String> varIDs = dominatorMap.get(dominator);
				return varIDs.get(0);
			}
			else{
				String varID = findReachingReadVariablesFromSuspiciousNodeToRootCause(dominator, rootCause);
				if(varID != null){
					List<String> varIDs = dominatorMap.get(dominator);
					return varIDs.get(0);
				}
			}
		}
		
		return null;
	}

	
	
}
