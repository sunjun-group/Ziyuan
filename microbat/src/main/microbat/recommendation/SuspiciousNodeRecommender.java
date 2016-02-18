package microbat.recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import microbat.model.AttributionVar;
import microbat.model.trace.LoopSequence;
import microbat.model.trace.PathInstance;
import microbat.model.trace.PotentialCorrectPattern;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.trace.TraceNodeOrderComparator;
import microbat.model.trace.TraceNodeReverseOrderComparator;
import microbat.model.value.VarValue;
import microbat.model.variable.Variable;
import microbat.util.Settings;

public class SuspiciousNodeRecommender {
	
	public class LoopRange{
		/**
		 * all the skipped trace node by loop inference.
		 */
		List<TraceNode> skipPoints = new ArrayList<>();
		TraceNode startNode;
		TraceNode endNode;
		
		TraceNode binaryLandmark;

		public TraceNode binarySearch() {
			Collections.sort(skipPoints, new TraceNodeOrderComparator());
			
			int startIndex = skipPoints.indexOf(startNode);
			int endIndex = skipPoints.indexOf(endNode);
			if(endIndex == -1){
				endIndex = skipPoints.size()-1;
			}
			
			int index = (startIndex+endIndex)/2;
			
			return skipPoints.get(index);
		}

		public TraceNode findCorrespondingStartNode(TraceNode endNode2) {
			for(int i=0; i<skipPoints.size(); i++){
				TraceNode node = skipPoints.get(i);
				if(node.getOrder() == endNode2.getOrder()){
					if(i>0){
						TraceNode startNode = skipPoints.get(i-1);
						return startNode;						
					}
					else{
						System.err.println("In findCorrespondingStartNode(), the input endNode2 is the start of skipPoints");
					}
				}
			}
			
			return null;
		}
		
		public TraceNode getBinaryLandMark(){
			return binaryLandmark;
		}

		public void clearSkipPoints() {
			binaryLandmark = null;
			skipPoints.clear();
		}

		TraceNode backupStartNode;
		TraceNode backupEndNode;
		
		public void backupStartAndEndNode() {
			backupStartNode = startNode;
			backupEndNode = endNode;
		}
	}
	
	private int state = DebugState.JUMP;
	
	private TraceNode lastNode;
	
	private TraceNode lastRecommendNode;
	
	private LoopRange range = new LoopRange();
	
	private List<TraceNode> visitedUnclearNodeList = new ArrayList<>();
	
	public TraceNode recommendNode(Trace trace, TraceNode currentNode, String feedback){
		if(feedback.equals(UserFeedback.UNCLEAR)){
			state = DebugState.UNCLEAR;
			visitedUnclearNodeList.add(currentNode);
			Collections.sort(visitedUnclearNodeList, new TraceNodeOrderComparator());
			TraceNode node = findMoreClearNode(trace, currentNode);
			return node;
		}
		else if((state==DebugState.UNCLEAR || state==DebugState.PARTIAL_CLEAR) && feedback.equals(UserFeedback.CORRECT)){
			state = DebugState.PARTIAL_CLEAR;
			
			Iterator<TraceNode> iter = visitedUnclearNodeList.iterator();
			while(iter.hasNext()){
				TraceNode visitedUnclearNode = iter.next();
				if(currentNode.getOrder() >= visitedUnclearNode.getOrder()){
					iter.remove();
				}
			}
			TraceNode earliestVisitedNode = null;
			if(!visitedUnclearNodeList.isEmpty()){
				earliestVisitedNode = visitedUnclearNodeList.get(0);
			}
			
			TraceNode node = findMoreDetailedNodeInBetween(trace, currentNode, earliestVisitedNode);
			return node;
		}
		else if((state==DebugState.UNCLEAR || state==DebugState.PARTIAL_CLEAR) && feedback.equals(UserFeedback.INCORRECT)){
			visitedUnclearNodeList.clear();
			TraceNode node = handleJumpBehavior(trace, currentNode);
			return node;
		}
		else{
			TraceNode node = recommendSuspiciousNode(trace, currentNode);
			return node;
		}
	}
	
	private TraceNode findMoreDetailedNodeInBetween(Trace trace, TraceNode currentNode, TraceNode earliestVisitedUnclearNode) {
		if(earliestVisitedUnclearNode == null){
			TraceNode node = handleJumpBehavior(trace, currentNode);
			return node;
		}
		else{
			TraceNode earliestNodeWithWrongVar = trace.getEarliestNodeWithWrongVar();
			
			if(earliestNodeWithWrongVar != null){
				List<TraceNode> dominators = earliestNodeWithWrongVar.findAllDominators();
				Collections.sort(dominators, new TraceNodeOrderComparator());
				
				Iterator<TraceNode> iter = dominators.iterator();
				while(iter.hasNext()){
					TraceNode dominator = iter.next();
					boolean shouldRemove = dominator.getOrder() < currentNode.getOrder() || 
							dominator.getOrder() > earliestVisitedUnclearNode.getOrder();
					if(shouldRemove){
						iter.remove();
					}
				}
				
				if(!dominators.isEmpty()){
					int index = dominators.size()/2;
					TraceNode moreDetailedNodeInBetween = dominators.get(index);
					return moreDetailedNodeInBetween;
				}
				else{
					System.err.println("In findMoreDetailedNodeInBetween(), cannot find a dominator between current node " + 
							currentNode.getOrder() + ", and unclear node " + earliestVisitedUnclearNode.getOrder());
				}
			}
			else{
				System.err.println("Cannot find earliestNodeWithWrongVar in findMoreDetailedNodeInBetween()");
			}
			
			return null;
		}
	}

	private TraceNode findMoreClearNode(Trace trace, TraceNode currentNode) {
		TraceNode earliestNodeWithWrongVar = trace.getEarliestNodeWithWrongVar();
		
		if(earliestNodeWithWrongVar != null){
			List<TraceNode> dominators = earliestNodeWithWrongVar.findAllDominators();
			Collections.sort(dominators, new TraceNodeReverseOrderComparator());
			
			LoopSequence loopSequence = trace.findLoopRangeOf(currentNode);
			for(TraceNode dominator: dominators){
				if(dominator.getInvocationLevel() < currentNode.getInvocationLevel()){
					if(currentNode.getInvocationParent() != null){
						if(dominator.getOrder() <= currentNode.getInvocationParent().getOrder()){
							return dominator;							
						}
					}
					
				}
				
				if(loopSequence != null){
					if(loopSequence.containsRangeOf(dominator)){
						continue;
					}
					else{
						return dominator;
					}
				}
			}
			
			if(!dominators.isEmpty()){
				return dominators.get(0);
			}
		}
		
		return null;
	}

	private TraceNode recommendSuspiciousNode(Trace trace, TraceNode currentNode){
		
		if(lastNode != null){
			PathInstance path = new PathInstance(currentNode, lastNode);
			if(path.isPotentialCorrect()){
				Settings.potentialCorrectPatterns.addPathForPattern(path);			
			}
		}
		
		if(lastRecommendNode!= null && !currentNode.equals(lastRecommendNode)){
			state = DebugState.JUMP;
		}
		
		TraceNode suspiciousNode = null;
		if(state == DebugState.JUMP){
			suspiciousNode = handleJumpBehavior(trace, currentNode);
		}
		else if(state == DebugState.SKIP){
			suspiciousNode = handleSkipBehavior(trace, currentNode);
		}
		else if(state == DebugState.BINARY_SEARCH){
			suspiciousNode = handleBinarySearchBehavior(trace, currentNode);
		}
		
		lastRecommendNode = suspiciousNode;
		
		return suspiciousNode;
	}

	private TraceNode handleBinarySearchBehavior(Trace trace, TraceNode currentNode) {
		TraceNode suspiciousNode = null;
		
		boolean isOverSkipping = currentNode.isAllReadWrittenVarCorrect();
		if(isOverSkipping){
			state = DebugState.BINARY_SEARCH;
			
			this.range.startNode = currentNode;
//			this.range.update();
			
			suspiciousNode = this.range.binarySearch();
			
			this.range.binaryLandmark = suspiciousNode;
			this.lastNode = currentNode;
		}
		else{
			TraceNode endNode = currentNode;
			TraceNode startNode = this.range.findCorrespondingStartNode(endNode);
			if(startNode != null){
				PathInstance fakePath = new PathInstance(startNode, endNode);
				
				PotentialCorrectPattern pattern = Settings.potentialCorrectPatterns.getPattern(fakePath);
				if(pattern != null){
					PathInstance labelPath = pattern.getLabelInstance();
					Variable causingVariable = labelPath.findCausingVar();
					
					if(isCompatible(causingVariable, currentNode)){
						state = DebugState.BINARY_SEARCH;
						if(currentNode.isAllReadWrittenVarCorrect()){
							this.range.startNode = currentNode;
						}
						else{
							this.range.endNode = currentNode;
						}
						
//						this.range.update();
						suspiciousNode = this.range.binarySearch();
						
						this.lastNode = currentNode;
					}
					else{
						state = DebugState.JUMP;
						suspiciousNode = handleJumpBehavior(trace, currentNode);
					}
				}
				else{
					System.err.println("error in binary search for " + fakePath.getLineTrace() + ", cannot find corresponding pattern");
				}
			}
			else{
				System.err.println("cannot find the start node in binary_search state");
			}
			
		}
		
		
		return suspiciousNode;
	}

	private boolean isCompatible(Variable causingVariable, TraceNode currentNode) {
		List<VarValue> markedReadVars = currentNode.findMarkedReadVariable();
		for(VarValue readVar: markedReadVars){
			Variable readVariable = readVar.getVariable();
			if(isEquivalentVariable(causingVariable, readVariable)){
				return true;
			}
		}
		
		return false;
	}

	private TraceNode handleSkipBehavior(Trace trace, TraceNode currentNode) {
		TraceNode suspiciousNode;
		boolean isOverSkipping = currentNode.isAllReadWrittenVarCorrect();
		if(isOverSkipping){
			state = DebugState.BINARY_SEARCH;
			
			this.range.startNode = currentNode;
			this.range.backupStartAndEndNode();
			
			suspiciousNode = this.range.binarySearch();
			
			this.range.binaryLandmark = suspiciousNode;
			this.lastNode = currentNode;
		}
		else{
			state = DebugState.JUMP;
			
			this.range.clearSkipPoints();
			
			suspiciousNode = handleJumpBehavior(trace, currentNode);
		}
		return suspiciousNode;
	}

	private TraceNode handleJumpBehavior(Trace trace, TraceNode currentNode) {
		TraceNode oldSusiciousNode = currentNode;
		
		List<AttributionVar> readVars = constructAttributionRelation(currentNode, trace.getCheckTime());
		AttributionVar focusVar = Settings.interestedVariables.findFocusVar(readVars);
				
		TraceNode suspiciousNode = null;
		if(focusVar != null){
//			long t1 = System.currentTimeMillis();
			trace.distributeSuspiciousness(Settings.interestedVariables);
//			long t2 = System.currentTimeMillis();
//			System.out.println("time for distributeSuspiciousness: " + (t2-t1));
			suspiciousNode = trace.findMostSupiciousNode(focusVar); 
		}
		TraceNode readTraceNode = focusVar.getReadTraceNode();
		PathInstance path = new PathInstance(suspiciousNode, readTraceNode);
		
		boolean isPathInPattern = Settings.potentialCorrectPatterns.containsPattern(path)? true : false;
		if(isPathInPattern){
			state = DebugState.SKIP;
			
			this.range.endNode = path.getEndNode();
			this.range.skipPoints.clear();
			//this.range.skipPoints.add(suspiciousNode);
			
			while(Settings.potentialCorrectPatterns.containsPattern(path)){
				
				Settings.potentialCorrectPatterns.addPathForPattern(path);
				this.range.skipPoints.add(suspiciousNode);
				
				PotentialCorrectPattern pattern = Settings.potentialCorrectPatterns.getPattern(path);
				oldSusiciousNode = suspiciousNode;
				
				suspiciousNode = findNextSuspiciousNodeByPattern(pattern, oldSusiciousNode);
				
				if(suspiciousNode == null){
					break;
				}
				else{
					path = new PathInstance(suspiciousNode, oldSusiciousNode);					
				}
			}
			
			this.lastNode = currentNode;
			return oldSusiciousNode;
		}
		else{
			state = DebugState.JUMP;
			
			this.lastNode = currentNode;
			return suspiciousNode;				
		}
	}
	
	/**
	 * Find the variable causing the jump of label path of the <code>pattern</code>, noted as <code>var</code>, 
	 * then try finding the dominator of the <code>oldSusiciousNode</code> by following the same dominance chain 
	 * with regard to <code>var</code>. The dominator of <code>oldSusiciousNode</code> on <code>var</code> is the
	 * new suspicious node. 
	 * <br><br>
	 * If there is no such dominator, this method return null.
	 * 
	 * @param pattern
	 * @param oldSusiciousNode
	 * @return
	 */
	public TraceNode findNextSuspiciousNodeByPattern(PotentialCorrectPattern pattern, 
			TraceNode oldSusiciousNode){
		PathInstance labelPath = pattern.getLabelInstance();
		
		Variable causingVariable = labelPath.findCausingVar();
		
		for(TraceNode dominator: oldSusiciousNode.getDataDominator().keySet()){
			for(VarValue writtenVar: dominator.getWrittenVariables()){
				Variable writtenVariable = writtenVar.getVariable();
				
				if(isEquivalentVariable(causingVariable, writtenVariable)){
					return dominator;					
				}
			}
		}
		
		
		return null;
	}
	
	private boolean isEquivalentVariable(Variable var1, Variable var2){
		String varID1 = var1.getVarID();
		String simpleVarID1 = Variable.truncateSimpleID(varID1);
		String varID2 = var2.getVarID();
		String simpleVarID2 = Variable.truncateSimpleID(varID2);
		
		boolean isEquivalentVariable = simpleVarID1.equals(simpleVarID2) || var1.getName().equals(var2.getName());
		
		return isEquivalentVariable;
	}
	

	private List<AttributionVar> constructAttributionRelation(TraceNode currentNode, int checkTime){
		List<AttributionVar> readVars = new ArrayList<>();
		for(VarValue writtenVarValue: currentNode.getWrittenVariables()){
			String writtenVarID = writtenVarValue.getVarID();
			if(Settings.interestedVariables.contains(writtenVarID)){
				for(VarValue readVarValue: currentNode.getReadVariables()){
					String readVarID = readVarValue.getVarID();
					if(Settings.interestedVariables.contains(readVarID)){
						
						AttributionVar writtenVar = Settings.interestedVariables.findOrCreateVar(writtenVarID, checkTime);
						AttributionVar readVar = Settings.interestedVariables.findOrCreateVar(readVarID, checkTime);
						readVar.setReadTraceNode(currentNode);
						
						readVar.addChild(writtenVar);
						writtenVar.addParent(readVar);
					}
				}						
			}
		}
		
		Settings.interestedVariables.updateAttributionTrees();
		
		for(VarValue readVarValue: currentNode.getReadVariables()){
			String readVarID = readVarValue.getVarID();
			if(Settings.interestedVariables.contains(readVarID)){
				AttributionVar readVar = Settings.interestedVariables.findOrCreateVar(readVarID, checkTime);
				readVar.setReadTraceNode(currentNode);
				readVars.add(readVar);
			}
		}		
		
		return readVars;
	}
	
	public int getState(){
		return state;
	}
	
	public LoopRange getRange(){
		return this.range;
	}
}
