package microbat.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import microbat.evaluation.model.PairList;
import microbat.evaluation.model.TraceNodePair;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.recommendation.UserFeedback;
import microbat.util.Settings;

public class SimulatedUser {

	private HashMap<TraceNode, Integer> labeledUnclearNodeVisitedTimes = new HashMap<>();
	
	public String feedback(TraceNode suspiciousNode, PairList pairList, int checkTime, boolean isFirstTime, boolean enableUnclear) {
		
		String feedback;
		
		boolean isClear = isClear(suspiciousNode, labeledUnclearNodeVisitedTimes, isFirstTime, enableUnclear);
		if(!isClear){
			feedback = UserFeedback.UNCLEAR;
		}
		else{
			TraceNodePair pair = pairList.findByMutatedNode(suspiciousNode);
			boolean isWrongPath = (pair==null);
			if(isWrongPath){
				feedback = UserFeedback.WRONG_PATH;
			}
			else{
//			System.currentTimeMillis();
//			List<String> wrongVarIDs = pair.findWrongVarIDs();
				List<String> wrongVarIDs = new ArrayList<>();
				String wrongReadVarID = pair.findSingleWrongReadVarID();
				String wrongWrittenVarID = pair.findSingleWrongWrittenVarID();
				
				if(wrongReadVarID != null){
					wrongVarIDs.add(wrongReadVarID);
				}
				if(wrongWrittenVarID != null){
					wrongVarIDs.add(wrongWrittenVarID);
				}
				
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
			
		}
		
		return feedback;
		
	}

	
	
	private boolean isClear(TraceNode suspiciousNode, HashMap<TraceNode, Integer> labeledUnclearNodeVisitedTimes, 
			boolean isFirstTime, boolean enableUnclear) {
		
		if(!enableUnclear){
			return true;
		}
		
		Integer times = labeledUnclearNodeVisitedTimes.get(suspiciousNode);
		if(times == null){
			times = 1;
		}
		else{
			times++;
		}
		labeledUnclearNodeVisitedTimes.put(suspiciousNode, times);
		
		
		if(isFirstTime){
			return true;
		}
		
		int layerNum = suspiciousNode.getInvocationLevel();
		
		double unclearPossibility = (1-1/(Math.pow(Math.E, layerNum-1)))/times;
		
		double dice = Math.random();
		
		if(dice<unclearPossibility){
			return false;
		}
		else{
			return true;			
		}
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
