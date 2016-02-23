package microbat.evaluation;

import java.util.List;

import microbat.evaluation.model.PairList;
import microbat.evaluation.util.DiffUtil;
import microbat.model.Fault;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.recommendation.StepRecommender;
import microbat.recommendation.conflicts.ConflictRuleChecker;

public class SimulatedMicroBat {
	
	private StepRecommender recommender = new StepRecommender();
	private SimulatedUser user = new SimulatedUser();
	
	public void detectMutatedBug(Trace mutatedTrace, Trace correctTrace) {
		PairList pairList = DiffUtil.generateMatchedTraceNodeList(mutatedTrace, correctTrace);
		
		
	}
	
	public void startSimulation() throws GenerateRootCauseException{
		
		Trace trace = new TraceModelConstructor().constructTraceModel(null, null);
		
//		Settings.interestedVariables.clear();
//		Settings.localVariableScopes.clear();
//		Settings.potentialCorrectPatterns.clear();
//		recommender = new StepRecommender();
//		Trace trace = Activator.getDefault().getCurrentTrace();
//		for(TraceNode node: trace.getExectionList()){
//			node.setCheckTime(-1);
//		}
		
		Fault shownFault = generateShownFault(trace);
		
		TraceNode shownFaultNode = shownFault.getBuggyNode();
		List<TraceNode> dominators = shownFaultNode.findAllDominators();
		
		Fault rootCause = generateRootCause(dominators, trace);
		
		TraceNode suspiciousNode = shownFaultNode;
		user.feedback(suspiciousNode, rootCause, trace.getCheckTime());
		
		boolean isBugFound = rootCause.getBuggyNode().getOrder()==suspiciousNode.getOrder();
		while(!isBugFound){
			
			suspiciousNode = findSuspicioiusNode(suspiciousNode, trace);
			
			isBugFound = rootCause.getBuggyNode().getOrder()==suspiciousNode.getOrder();
			
			if(!isBugFound){
				user.feedback(suspiciousNode, rootCause, trace.getCheckTime());				
			}
			
		}
		
		System.out.println("Bug found!");
	}

	private TraceNode findSuspicioiusNode(TraceNode currentNode, Trace trace) {
		setCurrentNodeCheck(trace, currentNode);
		
		TraceNode suspiciousNode = null;
		
		ConflictRuleChecker conflictRuleChecker = new ConflictRuleChecker();
		TraceNode conflictNode = conflictRuleChecker.checkConflicts(trace, currentNode.getOrder());
		
		if(conflictNode == null){
			//suspiciousNode = recommender.recommendSuspiciousNode(trace, currentNode);
		}
		else{
			suspiciousNode = conflictNode;
		}
		
		return suspiciousNode;
	}
	
	private void setCurrentNodeCheck(Trace trace, TraceNode currentNode) {
		int checkTime = trace.getCheckTime()+1;
		currentNode.setCheckTime(checkTime);
		trace.setCheckTime(checkTime);
	}

	private Fault generateRootCause(List<TraceNode> dominators, Trace trace) throws GenerateRootCauseException{
		
		int count = 0;
		
		while(true){
//			int index = (int) (dominators.size()*Math.random());
//			TraceNode node = dominators.get(index);
			
//			Trace trace = Activator.getDefault().getCurrentTrace();
			TraceNode node = trace.getExectionList().get(152);
			
			if(!node.getWrittenVariables().isEmpty()){
				VarValue var = node.getWrittenVariables().get(0);
				Fault fault = new Fault(node, var);
				return fault;
			}
			
			count++;
			if(count > 50){
				String message = "encounter infinite loop when generating the root cause of a trace";
				GenerateRootCauseException ex = new GenerateRootCauseException(message);
				
				throw ex;
			}
		}
	}

	/**
	 * The general idea is that the last node writing variable is the step for output.
	 * @param trace
	 * @return
	 */
	private Fault generateShownFault(Trace trace) {
		for(int i=trace.size()-1; i>=0; i--){
			TraceNode node = trace.getExectionList().get(i);
			if(!node.getWrittenVariables().isEmpty()){
				VarValue var = node.getWrittenVariables().get(0);
				Fault fault = new Fault(node, var);
				return fault;
			}
		}
		
		return null;
	}

	

	
}
