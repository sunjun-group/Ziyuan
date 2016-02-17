package microbat.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import microbat.model.AttributionVar;
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
				AttributionVar var = Settings.interestedVariables.add(readVarID, checkTime);
				var.setReadTraceNode(suspiciousNode);
				
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
		
		Map<TraceNode, List<String>> dominatorMap = suspiciousNode.getDataDominator();
		
		List<String> workingIDs = new ArrayList<>();
		for(TraceNode dominator: dominatorMap.keySet()){
			
			
			if(dominator.equals(rootCause.getBuggyNode())){
				List<String> varIDs = dominatorMap.get(dominator);
				workingIDs.add(varIDs.get(0));
			}
			else{
				String varID = findReachingReadVariablesFromSuspiciousNodeToRootCause(dominator, rootCause);
				if(varID != null){
					List<String> varIDs = dominatorMap.get(dominator);
					workingIDs.add(varIDs.get(0));
				}
			}
		}
		
		String varID = getFittestVar(workingIDs);
		
		return varID;
	}

	private String getFittestVar(List<String> workingIDs) {
		if(workingIDs.isEmpty()){
			return null;			
		}
		else if(workingIDs.size() == 1){
			return workingIDs.get(0);
		}
		else{
			String varID = workingIDs.get(0);
			
			for(String workingID: workingIDs){
				if(!workingID.contains("vir")){
					varID = workingID;
					return varID;
				}
			}
			
			return varID;
		}
	}

	
	
}
