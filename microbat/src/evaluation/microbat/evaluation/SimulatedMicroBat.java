package microbat.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microbat.evaluation.accuracy.Accuracy;
import microbat.evaluation.model.PairList;
import microbat.evaluation.model.TraceNodePair;
import microbat.evaluation.util.DiffUtil;
import microbat.evaluation.util.TraceNodeSimilarityComparator;
import microbat.model.Fault;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.recommendation.StepRecommender;
import microbat.recommendation.conflicts.ConflictRuleChecker;
import sav.strategies.dto.ClassLocation;

public class SimulatedMicroBat {
	List<TraceNode> falsePositive = new ArrayList<>();
	List<TraceNode> falseNegative = new ArrayList<>();
	
	private StepRecommender recommender = new StepRecommender();
	private SimulatedUser user = new SimulatedUser();
	
	public void detectMutatedBug(Trace mutatedTrace, Trace correctTrace, ClassLocation mutatedLocation) {
		PairList pairList = DiffUtil.generateMatchedTraceNodeList(mutatedTrace, correctTrace);
		
//		TraceNode rootCause = findRootCause(mutatedLocation.getClassCanonicalName(), 
//				mutatedLocation.getLineNo(), mutatedTrace, pairList);
//		
//		List<TraceNode> dominatees = rootCause.findAllDominatees();
//		dominatees.add(rootCause);
		
		List<TraceNode> dominatees = findAllDominatees(mutatedTrace, mutatedLocation);
		List<TraceNode> allWrongNodes = findAllWrongNodes(pairList, mutatedTrace);
		
		Accuracy accuracy = computeAccuracy(dominatees, allWrongNodes);
		
		if(accuracy.getRecall() < 0.95){
			System.currentTimeMillis();
			TraceNodeSimilarityComparator sc = new TraceNodeSimilarityComparator();
			TraceNode node = falseNegative.get(0);
			TraceNodePair pair = pairList.findByMutatedNode(node);
			
			double d = sc.compute(pair.getMutatedNode(), pair.getOriginalNode());
			
			System.currentTimeMillis();
		}
		
		System.out.println(accuracy);
	}
	
	private List<TraceNode> findAllDominatees(Trace mutationTrace, ClassLocation mutatedLocation){
		Map<Integer, TraceNode> allDominatees = new HashMap<>();
		
		for(TraceNode mutatedNode: mutationTrace.getExectionList()){
			if(mutatedNode.getClassName().equals(mutatedLocation.getClassCanonicalName()) 
					&& mutatedNode.getLineNumber() == mutatedLocation.getLineNo()){
				
				if(allDominatees.get(mutatedNode.getOrder()) == null){
					Map<Integer, TraceNode> dominatees = mutatedNode.findAllDominatees();
					allDominatees.putAll(dominatees);
					allDominatees.put(mutatedNode.getOrder(), mutatedNode);
				}
				
			}
		}
		
		return new ArrayList<>(allDominatees.values());
	}
	
	private List<TraceNode> findAllWrongNodes(PairList pairList, Trace mutatedTrace){
		List<TraceNode> actualWrongNodes = new ArrayList<>();
		for(TraceNode mutatedTraceNode: mutatedTrace.getExectionList()){
			TraceNodePair foundPair = pairList.findByMutatedNode(mutatedTraceNode);
			if(foundPair != null){
				if(!foundPair.isExactSame()){
					actualWrongNodes.add(foundPair.getMutatedNode());
				}
			}
			else{
				actualWrongNodes.add(mutatedTraceNode);
			}
		}
		
		return actualWrongNodes;
	}
	
	private Accuracy computeAccuracy(List<TraceNode> dominatees, List<TraceNode> actualWrongNodes) {
		double modelInfluencedSize = dominatees.size();
		
		List<TraceNode> commonNodes = findCommonNodes(dominatees, actualWrongNodes);
		
		double precision = (double)commonNodes.size()/modelInfluencedSize;
		double recall = (double)commonNodes.size()/actualWrongNodes.size();
		
		Accuracy accuracy = new Accuracy(precision, recall);
		
		return accuracy;
	}

	private List<TraceNode> findCommonNodes(List<TraceNode> dominatees,
			List<TraceNode> actualWrongNodes) {
		List<TraceNode> commonNodes = new ArrayList<>();
		
		falsePositive = new ArrayList<>();
		falseNegative = new ArrayList<>();
		
		for(TraceNode domiantee: dominatees){
			if(actualWrongNodes.contains(domiantee)){
				commonNodes.add(domiantee);
			}
			else{
				falsePositive.add(domiantee);
			}
		}
		
		for(TraceNode acturalWrongNode: actualWrongNodes){
			if(!commonNodes.contains(acturalWrongNode)){
				falseNegative.add(acturalWrongNode);
			}
		}
		
		return commonNodes;
	}

	private TraceNode findRootCause(String className, int lineNo, Trace mutatedTrace, PairList pairList) {
		for(TraceNode node: mutatedTrace.getExectionList()){
			if(node.getClassName().equals(className) && node.getLineNumber()==lineNo){
				TraceNodePair pair = pairList.findByMutatedNode(node);
				
				if(pair == null){
					System.currentTimeMillis();
				}
				
				return pair.getMutatedNode();
			}
		}
		
		return null;
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
		Map<Integer, TraceNode> dominatorMap = shownFaultNode.findAllDominators();
		List<TraceNode> dominators = new ArrayList<>(dominatorMap.values());
		
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
