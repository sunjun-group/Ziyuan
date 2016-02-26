package microbat.recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

public class StepRecommender {
	
	public class LoopRange{
		/**
		 * all the skipped trace node by loop inference.
		 */
		ArrayList<TraceNode> skipPoints = new ArrayList<>();
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
		
		@SuppressWarnings("unchecked")
		public LoopRange clone(){
			LoopRange loopRange = new LoopRange();
			loopRange.startNode = this.startNode;
			loopRange.endNode = this.endNode;
			loopRange.binaryLandmark = this.binaryLandmark;
			loopRange.skipPoints = (ArrayList<TraceNode>) this.skipPoints.clone();
			
			return loopRange;
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

		public boolean checkedSkipPointsContains(TraceNode suspiciousNode) {
			for(TraceNode skipPoint: skipPoints){
				if(skipPoint.hasChecked() && skipPoint.equals(suspiciousNode)){
					return true;
				}
			}
			return false;
		}
	}
	
	public class InspectingRange{
		TraceNode startNode;
		TraceNode endNode;

		public InspectingRange(TraceNode startNode, TraceNode endNode) {
			super();
			this.startNode = startNode;
			this.endNode = endNode;
		}
		
		public InspectingRange clone(){
			InspectingRange inspectingRange = new InspectingRange(startNode, endNode);
			return inspectingRange;
		}
	}
	
	private int state = DebugState.JUMP;
	
	/**
	 * Fields for clear state.
	 */
	private int latestClearState = -1;
	private TraceNode lastNode;
	private TraceNode lastRecommendNode;
	private LoopRange loopRange = new LoopRange();
	private InspectingRange inspectingRange;
	
	private List<TraceNode> visitedUnclearNodeList = new ArrayList<>();
	
	public TraceNode recommendNode(Trace trace, TraceNode currentNode, String feedback){
		if(feedback.equals(UserFeedback.UNCLEAR)){
			
			if(state==DebugState.JUMP || state==DebugState.SKIP || state==DebugState.BINARY_SEARCH || state==DebugState.DETAIL_INSPECT){
				latestClearState = state;
			}
			
			state = DebugState.UNCLEAR;
			visitedUnclearNodeList.add(currentNode);
			Collections.sort(visitedUnclearNodeList, new TraceNodeOrderComparator());
			TraceNode node = findMoreClearNode(trace, currentNode);
			return node;
		}
		else if(feedback.equals(UserFeedback.WRONG_PATH)){
			state = DebugState.JUMP;
			TraceNode node = currentNode.getControlDominator();
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
			
			if(earliestVisitedNode == null){
				state = latestClearState;
				TraceNode node = recommendSuspiciousNode(trace, currentNode, feedback);
				return node;
			}
			else{
				TraceNode node = findMoreDetailedNodeInBetween(trace, currentNode, earliestVisitedNode);
				if(node.equals(currentNode)){
					return earliestVisitedNode;
				}
				else{
					return node;
				}
			}
		}
		else if((state==DebugState.UNCLEAR || state==DebugState.PARTIAL_CLEAR) && feedback.equals(UserFeedback.INCORRECT)){
			visitedUnclearNodeList.clear();
			state = latestClearState;
			TraceNode node = recommendSuspiciousNode(trace, currentNode, feedback);
			return node;
		}
		else{
			TraceNode node = recommendSuspiciousNode(trace, currentNode, feedback);
			return node;
		}
	}
	
	private TraceNode findMoreDetailedNodeInBetween(Trace trace, TraceNode currentNode, TraceNode earliestVisitedUnclearNode) {
		TraceNode earliestNodeWithWrongVar = trace.getEarliestNodeWithWrongVar();
		
		if(earliestNodeWithWrongVar != null){
			Map<Integer, TraceNode> dominatorMap = earliestNodeWithWrongVar.findAllDominators();
			List<TraceNode> dominators = new ArrayList<>(dominatorMap.values());
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

	private TraceNode findMoreAbstractDominator(LoopSequence loopSequence, List<TraceNode> dominators, TraceNode currentNode){
		TraceNode moreAbstractDominator = null;
		for(TraceNode dominator: dominators){
			if(dominator.getInvocationLevel() < currentNode.getInvocationLevel()){
				if(currentNode.getInvocationParent() != null){
					if(dominator.getOrder() <= currentNode.getInvocationParent().getOrder()){
						moreAbstractDominator = dominator;		
						break;
					}
				}
				
			}
			
			if(loopSequence != null){
				if(loopSequence.containsRangeOf(dominator)){
					continue;
				}
				else{
					moreAbstractDominator = dominator;
					break;
				}
			}
		}
		
		return moreAbstractDominator;
	}
	
	private TraceNode findMoreClearNode(Trace trace, TraceNode currentNode) {
		TraceNode earliestNodeWithWrongVar = trace.getEarliestNodeWithWrongVar();
		
		if(earliestNodeWithWrongVar != null){
			Map<Integer, TraceNode> dominatorMap = earliestNodeWithWrongVar.findAllDominators();
			List<TraceNode> dominators = new ArrayList<>(dominatorMap.values());
			Collections.sort(dominators, new TraceNodeReverseOrderComparator());
			
			LoopSequence loopSequence = trace.findLoopRangeOf(currentNode);
			TraceNode moreAbstractDominator = findMoreAbstractDominator(loopSequence, dominators, currentNode);
			
			if(moreAbstractDominator != null){
				if(moreAbstractDominator.hasChecked()){
					int index = dominators.indexOf(moreAbstractDominator);
					for(int i=index; i>=0; i--){
						TraceNode dominator = dominators.get(i);
						if(!dominator.equals(moreAbstractDominator)){
							if(!dominator.hasChecked()){
								return dominator;
							}
						}
					}
				}
				else{
					return moreAbstractDominator;
				}
			}
			else if(!dominators.isEmpty()){
				return dominators.get(0);
			}
		}
		
		return null;
	}

	private TraceNode recommendSuspiciousNode(Trace trace, TraceNode currentNode, String userFeedBack){
		
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
			suspiciousNode = handleJumpBehavior(trace, currentNode, userFeedBack);
		}
		else if(state == DebugState.SKIP){
			suspiciousNode = handleSkipBehavior(trace, currentNode, userFeedBack);
		}
		else if(state == DebugState.BINARY_SEARCH){
			suspiciousNode = handleBinarySearchBehavior(trace, currentNode, userFeedBack);
		}
		else if(state == DebugState.DETAIL_INSPECT){
			suspiciousNode = handleDetailInspecting(trace, currentNode, userFeedBack);
		}
		
		lastRecommendNode = suspiciousNode;
		
		return suspiciousNode;
	}

	private TraceNode handleBinarySearchBehavior(Trace trace, TraceNode currentNode, String userFeedback) {
		TraceNode suspiciousNode = null;
		
		boolean isOverSkipping = currentNode.isAllReadWrittenVarCorrect(false);
		if(isOverSkipping){
			state = DebugState.BINARY_SEARCH;
			
			this.loopRange.startNode = currentNode;
//			this.range.update();
			
			suspiciousNode = this.loopRange.binarySearch();
			
			this.loopRange.binaryLandmark = suspiciousNode;
			this.lastNode = currentNode;
		}
		else{
			TraceNode endNode = currentNode;
			TraceNode startNode = this.loopRange.findCorrespondingStartNode(endNode);
			if(startNode != null){
				PathInstance fakePath = new PathInstance(startNode, endNode);
				
				PotentialCorrectPattern pattern = Settings.potentialCorrectPatterns.getPattern(fakePath);
				if(pattern != null){
					PathInstance labelPath = pattern.getLabelInstance();
					Variable causingVariable = labelPath.findCausingVar();
					
					if(isCompatible(causingVariable, currentNode)){
						state = DebugState.BINARY_SEARCH;
						if(currentNode.isAllReadWrittenVarCorrect(false)){
							this.loopRange.startNode = currentNode;
						}
						else{
							this.loopRange.endNode = currentNode;
						}
						
//						this.range.update();
						suspiciousNode = this.loopRange.binarySearch();
						
						this.lastNode = currentNode;
					}
					else{
						state = DebugState.JUMP;
						suspiciousNode = handleJumpBehavior(trace, currentNode, userFeedback);
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

	private TraceNode handleSkipBehavior(Trace trace, TraceNode currentNode, String userFeedback) {
		TraceNode suspiciousNode;
		boolean isOverSkipping = currentNode.isAllReadWrittenVarCorrect(false);
		if(isOverSkipping){
			state = DebugState.BINARY_SEARCH;
			
			this.loopRange.startNode = currentNode;
			this.loopRange.backupStartAndEndNode();
			
			suspiciousNode = this.loopRange.binarySearch();
			
			this.loopRange.binaryLandmark = suspiciousNode;
			this.lastNode = currentNode;
		}
		else{
			state = DebugState.JUMP;
			
			this.loopRange.clearSkipPoints();
			
			suspiciousNode = handleJumpBehavior(trace, currentNode, userFeedback);
		}
		return suspiciousNode;
	}

	private TraceNode handleJumpBehavior(Trace trace, TraceNode currentNode, String userFeedBack) {
		TraceNode oldSusiciousNode = currentNode;
		
		List<AttributionVar> readVars = constructAttributionRelation(currentNode, trace.getCheckTime());
		AttributionVar focusVar = Settings.interestedVariables.findFocusVar(trace, currentNode, readVars);
				
		if(focusVar != null){
//			long t1 = System.currentTimeMillis();
			trace.distributeSuspiciousness(Settings.interestedVariables);
//			long t2 = System.currentTimeMillis();
//			System.out.println("time for distributeSuspiciousness: " + (t2-t1));
			TraceNode suspiciousNode = trace.findMostSupiciousNode(focusVar);
			
			/**
			 * it means the suspiciousness of focusVar cannot be distributed to other trace node any more. 
			 */
			if(suspiciousNode.isWrittenVariablesContains(focusVar.getVarID()) && suspiciousNode.equals(this.lastNode)){
				//TODO it could be done in a more intelligent way.
				this.inspectingRange = new InspectingRange(currentNode, suspiciousNode);
				TraceNode recommendedNode = handleDetailInspecting(trace, currentNode, userFeedBack);
				return recommendedNode;
			}
			else{
				TraceNode readTraceNode = focusVar.getReadTraceNode();
				
				boolean isPathInPattern = false;
				PathInstance path = null;
				if(readTraceNode != null){
					path = new PathInstance(suspiciousNode, readTraceNode);
					isPathInPattern = Settings.potentialCorrectPatterns.containsPattern(path)? true : false;					
				}
				
				if(isPathInPattern){
					state = DebugState.SKIP;
					
					this.loopRange.endNode = path.getEndNode();
					this.loopRange.skipPoints.clear();
					//this.range.skipPoints.add(suspiciousNode);
					
					while(Settings.potentialCorrectPatterns.containsPattern(path) 
							&& !shouldStopOnCheckedNode(suspiciousNode, path)){
						
						Settings.potentialCorrectPatterns.addPathForPattern(path);
						this.loopRange.skipPoints.add(suspiciousNode);
						
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
		}
		
		System.err.println("fucosVar is null");
		return currentNode;
		
	}
	
	private boolean shouldStopOnCheckedNode(TraceNode suspiciousNode, PathInstance path) {
		if(!suspiciousNode.hasChecked()){
			return false;
		}
		else{
			if(suspiciousNode.isAllReadWrittenVarCorrect(false)){
				return true;
			}
			else{
				PotentialCorrectPattern pattern = Settings.potentialCorrectPatterns.getPattern(path);
				if(pattern != null){
					PathInstance labelPath = pattern.getLabelInstance();
					Variable causingVariable = labelPath.findCausingVar();
					
					if(isCompatible(causingVariable, suspiciousNode)){
						return false;
					}
				}
			}
		}
		
		return true;
	}

	private TraceNode handleDetailInspecting(Trace trace, TraceNode currentNode, String userFeedBack) {
		
		if(userFeedBack.equals(UserFeedback.CORRECT)){
			this.state = DebugState.DETAIL_INSPECT;
			
			TraceNode nextNode;
			if(currentNode.getOrder() > this.inspectingRange.endNode.getOrder()){
				nextNode = this.inspectingRange.startNode;
			}
			else{
				nextNode = trace.getExectionList().get(currentNode.getOrder());
				
			}
			
			return nextNode;
		}
		else{
			TraceNode node = handleJumpBehavior(trace, currentNode, userFeedBack);
			return node;
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
	
	@SuppressWarnings("unchecked")
	public StepRecommender clone(){
		StepRecommender recommender = new StepRecommender();
		recommender.lastNode = this.lastNode;
		recommender.lastRecommendNode = this.lastRecommendNode;
		recommender.latestClearState = this.latestClearState;
		recommender.loopRange = this.loopRange.clone();
		if(this.inspectingRange != null){
			recommender.inspectingRange = this.inspectingRange.clone();			
		}
		ArrayList<TraceNode> list = (ArrayList<TraceNode>)this.visitedUnclearNodeList;
		recommender.visitedUnclearNodeList = (ArrayList<TraceNode>) list.clone();
		
		return recommender;
	}
	
	public int getState(){
		return state;
	}
	
	public LoopRange getLoopRange(){
		return this.loopRange;
	}
}
