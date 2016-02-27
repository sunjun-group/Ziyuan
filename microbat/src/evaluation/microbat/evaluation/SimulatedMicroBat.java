package microbat.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microbat.evaluation.accuracy.Accuracy;
import microbat.evaluation.model.PairList;
import microbat.evaluation.model.TraceNodePair;
import microbat.evaluation.model.Trial;
import microbat.evaluation.util.DiffUtil;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.trace.TraceNodeReverseOrderComparator;
import microbat.recommendation.StepRecommender;
import microbat.recommendation.UserFeedback;
import microbat.util.Settings;
import sav.strategies.dto.ClassLocation;

public class SimulatedMicroBat {
	List<TraceNode> falsePositive = new ArrayList<>();
	List<TraceNode> falseNegative = new ArrayList<>();
	
	private SimulatedUser user = new SimulatedUser();
	private StepRecommender recommender;
	
	public Trial detectMutatedBug(Trace mutatedTrace, Trace correctTrace, ClassLocation mutatedLocation) {
		PairList pairList = DiffUtil.generateMatchedTraceNodeList(mutatedTrace, correctTrace);
		
		TraceNode rootCause = findRootCause(mutatedLocation.getClassCanonicalName(), 
				mutatedLocation.getLineNo(), mutatedTrace, pairList);
		
//		Object dom = rootCause.findAllDominatees();
//		dominatees.add(rootCause);
		
//		List<TraceNode> dominatees = findAllDominatees(mutatedTrace, mutatedLocation);
		Map<Integer, TraceNode> allWrongNodeMap = findAllWrongNodes(pairList, mutatedTrace);
		
		List<TraceNode> wrongNodeList = new ArrayList<>(allWrongNodeMap.values());
		Collections.sort(wrongNodeList, new TraceNodeReverseOrderComparator());
		TraceNode observedFaultNode = wrongNodeList.get(0);
		
		Trial trial = startSimulation(observedFaultNode, rootCause, mutatedTrace, allWrongNodeMap, pairList);
		return trial;
		
//		Accuracy accuracy = computeAccuracy(dominatees, allWrongNodes);
//		
//		if(accuracy.getRecall() < 0.95){
//			System.out.println(mutatedLocation.getClassCanonicalName() + ":" + mutatedLocation.getLineNo() + " has problem");
//			TraceNodeSimilarityComparator sc = new TraceNodeSimilarityComparator();
//			TraceNode node = falseNegative.get(0);
//			TraceNodePair pair = pairList.findByMutatedNode(node);
//			
//			double d = sc.compute(pair.getMutatedNode(), pair.getOriginalNode());
//			
//			System.currentTimeMillis();
//		}
//		
//		System.out.println(accuracy);
	}
	
	private Trial startSimulation(TraceNode observedFaultNode, TraceNode rootCause, Trace mutatedTrace, 
			Map<Integer, TraceNode> allWrongNodeMap, PairList pairList) {
		Settings.interestedVariables.clear();
		Settings.localVariableScopes.clear();
		Settings.potentialCorrectPatterns.clear();
		recommender = new StepRecommender();
		
		List<TraceNode> jumpingSteps = new ArrayList<>();
		
		TraceNode suspiciousNode = observedFaultNode;
		jumpingSteps.add(suspiciousNode);
		
		String feedbackType = user.feedback(suspiciousNode, pairList, mutatedTrace.getCheckTime());
		int checkTime = 1;
		
		boolean isFail = false;
		boolean isBugFound = rootCause.getLineNumber()==suspiciousNode.getLineNumber();
		while(!isBugFound){
			suspiciousNode = findSuspicioiusNode(suspiciousNode, mutatedTrace, feedbackType);
			jumpingSteps.add(suspiciousNode);
			isBugFound = rootCause.getLineNumber()==suspiciousNode.getLineNumber();
			
			if(!isBugFound){
				if(suspiciousNode.getOrder()==143){
					System.currentTimeMillis();
				}
				
				feedbackType = user.feedback(suspiciousNode, pairList, mutatedTrace.getCheckTime());
				checkTime++;
				
				if(checkTime > mutatedTrace.size()){
					isFail = true;
					break;
				}
			}
		}
		
		List<String> jumpStringSteps = new ArrayList<>();
		System.out.println("bug found: " + !isFail);
		for(TraceNode node: jumpingSteps){
			String str = node.toString();
			System.out.println(str);		
			jumpStringSteps.add(str);
		}
		System.out.println("Root Cause:" + rootCause);
		System.currentTimeMillis();
		
		Trial trial = new Trial();
		trial.setBugFound(isBugFound);
		trial.setMutatedLineNumber(rootCause.getLineNumber());
		trial.setJumpSteps(jumpStringSteps);
		trial.setTotalSteps(mutatedTrace.size());
		
		return trial;
	}

	
	protected List<TraceNode> findAllDominatees(Trace mutationTrace, ClassLocation mutatedLocation){
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
	
	private Map<Integer, TraceNode> findAllWrongNodes(PairList pairList, Trace mutatedTrace){
		Map<Integer, TraceNode> actualWrongNodes = new HashMap<>();
		for(TraceNode mutatedTraceNode: mutatedTrace.getExectionList()){
			TraceNodePair foundPair = pairList.findByMutatedNode(mutatedTraceNode);
			if(foundPair != null){
				if(!foundPair.isExactSame()){
					TraceNode mutatedNode = foundPair.getMutatedNode();
					actualWrongNodes.put(mutatedNode.getOrder(), mutatedNode);
				}
			}
			else{
				actualWrongNodes.put(mutatedTraceNode.getOrder(), mutatedTraceNode);
			}
		}
		
		return actualWrongNodes;
	}
	
	public Accuracy computeAccuracy(List<TraceNode> dominatees, List<TraceNode> actualWrongNodes) {
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

	private TraceNode findSuspicioiusNode(TraceNode currentNode, Trace trace, String feedbackType) {
		setCurrentNodeCheck(trace, currentNode);
		
		
		if(!feedbackType.equals(UserFeedback.UNCLEAR)){
			setCurrentNodeCheck(trace, currentNode);					
		}
		
		TraceNode suspiciousNode = recommender.recommendNode(trace, currentNode, feedbackType);
		return suspiciousNode;
		
//		TraceNode suspiciousNode = null;
//		
//		ConflictRuleChecker conflictRuleChecker = new ConflictRuleChecker();
//		TraceNode conflictNode = conflictRuleChecker.checkConflicts(trace, currentNode.getOrder());
//		
//		if(conflictNode == null){
//			suspiciousNode = recommender.recommendNode(trace, currentNode, feedbackType);
//		}
//		else{
//			suspiciousNode = conflictNode;
//		}
//		
//		return suspiciousNode;
	}
	
	private void setCurrentNodeCheck(Trace trace, TraceNode currentNode) {
		int checkTime = trace.getCheckTime()+1;
		currentNode.setCheckTime(checkTime);
		trace.setCheckTime(checkTime);
	}


	
}
