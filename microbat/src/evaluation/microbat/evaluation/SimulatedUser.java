package microbat.evaluation;

import java.util.List;

import microbat.evaluation.model.PairList;
import microbat.evaluation.model.TraceNodePair;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.recommendation.UserFeedback;
import microbat.util.Settings;

public class SimulatedUser {

	public String feedback(TraceNode suspiciousNode, PairList pairList, int checkTime) {
		
		String feedback;
		
		TraceNodePair pair = pairList.findByMutatedNode(suspiciousNode);
		boolean isWrongPath = pair==null;
		if(isWrongPath){
			feedback = UserFeedback.WRONG_PATH;
		}
		else{
			
			List<String> wrongVarIDs = pair.findWrongVarIDs();
			
			if(!wrongVarIDs.isEmpty()){
				for(String wrongVarID: wrongVarIDs){
					Settings.interestedVariables.add(wrongVarID, checkTime);
				}			
				feedback = UserFeedback.INCORRECT;
			}
			else{
				for(VarValue writtenVar: suspiciousNode.getWrittenVariables()){
					Settings.interestedVariables.remove(writtenVar.getVarID());
				}
				for(VarValue readVar: suspiciousNode.getReadVariables()){
					Settings.interestedVariables.remove(readVar.getVarID());
				}
				
				feedback = UserFeedback.CORRECT;
				
			}
			
		}
		
		return feedback;
		
	}

//	private String findReachingReadVariablesFromSuspiciousNodeToRootCause(
//			TraceNode suspiciousNode, Fault rootCause) {
//		
//		Map<TraceNode, List<String>> dominatorMap = suspiciousNode.getDataDominator();
//		
//		List<String> workingIDs = new ArrayList<>();
//		for(TraceNode dominator: dominatorMap.keySet()){
//			
//			
//			if(dominator.equals(rootCause.getBuggyNode())){
//				List<String> varIDs = dominatorMap.get(dominator);
//				workingIDs.add(varIDs.get(0));
//			}
//			else{
//				String varID = findReachingReadVariablesFromSuspiciousNodeToRootCause(dominator, rootCause);
//				if(varID != null){
//					List<String> varIDs = dominatorMap.get(dominator);
//					workingIDs.add(varIDs.get(0));
//				}
//			}
//		}
//		
//		String varID = getFittestVar(workingIDs);
//		
//		return varID;
//	}
//
//	private String getFittestVar(List<String> workingIDs) {
//		if(workingIDs.isEmpty()){
//			return null;			
//		}
//		else if(workingIDs.size() == 1){
//			return workingIDs.get(0);
//		}
//		else{
//			String varID = workingIDs.get(0);
//			
//			for(String workingID: workingIDs){
//				if(!workingID.contains("vir")){
//					varID = workingID;
//					return varID;
//				}
//			}
//			
//			return varID;
//		}
//	}

	
	
}
