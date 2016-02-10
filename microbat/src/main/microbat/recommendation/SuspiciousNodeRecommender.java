package microbat.recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import microbat.model.AttributionVar;
import microbat.model.trace.PathInstance;
import microbat.model.trace.PotentialCorrectPattern;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.trace.TraceNodeOrderComparator;
import microbat.model.value.VarValue;
import microbat.model.variable.Variable;
import microbat.util.Settings;

public class SuspiciousNodeRecommender {
	
	class LoopRange{
		/**
		 * all the skipped trace node by loop inference.
		 */
		List<TraceNode> skipPoints = new ArrayList<>();
		TraceNode startNode;
		TraceNode endNode;
		
		TraceNode binaryLandmark;

		public void update() {
			Collections.sort(skipPoints, new TraceNodeOrderComparator());
			Iterator<TraceNode> iter = skipPoints.iterator();
			while(iter.hasNext()){
				TraceNode skipNode = iter.next();
				if(skipNode.getOrder() < startNode.getOrder()
						|| skipNode.getOrder() > endNode.getOrder()){
					iter.remove();
				}
			}
		}

		public TraceNode binarySearch() {
			Collections.sort(skipPoints, new TraceNodeOrderComparator());
			int index = skipPoints.size()/2;
			return skipPoints.get(index);
		}
	}
	
	private boolean isFormSkipping = false;
	private boolean isOverSkipping = false;

	private TraceNode lastNode;
	private LoopRange range = new LoopRange();
	
	public TraceNode recommendSuspiciousNode(Trace trace, TraceNode currentNode){
		if(lastNode != null){
			PathInstance path = new PathInstance(currentNode, lastNode);
			if(path.isPotentialCorrect()){
				Settings.potentialCorrectPatterns.addPathForPattern(path);			
			}
		}
		
		
		/**
		 * after we find the over-skipping, we perform binary search to identify where the bug lies. 
		 */
		if(this.range.binaryLandmark != null && currentNode.getOrder() == this.range.binaryLandmark.getOrder()){
			if(currentNode.isAllReadWrittenVarCorrect()){
				this.range.startNode = currentNode;
			}
			else{
				this.range.endNode = currentNode;
			}
			
			this.range.update();
			TraceNode suspiciousNode = this.range.binarySearch();
			
			this.lastNode = currentNode;
			
			return suspiciousNode;
		}
		
		/**
		 * we find the over-skipping phenomenon, thus, the bug lies in the loop.
		 */
		if(isFormSkipping){
			isOverSkipping = currentNode.isAllReadWrittenVarCorrect();
			if(isOverSkipping){
				this.range.startNode = currentNode;
				TraceNode suspiciousNode = this.range.binarySearch();
				isFormSkipping = false;
				
				this.lastNode = currentNode;
				
				return suspiciousNode;
			}
		}
		
		/**
		 * jump or skip based on <code>currentNode</code>
		 */
		if(!isOverSkipping){
			TraceNode oldSusiciousNode = currentNode;
			
			System.currentTimeMillis();
			
			TraceNode suspiciousNode = findNodeBySuspiciousnessDistribution(trace, currentNode);
			PathInstance path = new PathInstance(suspiciousNode, currentNode);
			
			isFormSkipping = Settings.potentialCorrectPatterns.containsPattern(path)? true : false;
			if(isFormSkipping){
				this.range.endNode = path.getEndNode();
				this.range.skipPoints.clear();
				this.range.skipPoints.add(suspiciousNode);
				
				while(Settings.potentialCorrectPatterns.containsPattern(path)){
					
					Settings.potentialCorrectPatterns.addPathForPattern(path);
					PotentialCorrectPattern pattern = Settings.potentialCorrectPatterns.getPattern(path);
					
					
					oldSusiciousNode = suspiciousNode;
					suspiciousNode = findNextSuspiciousNodeByPattern(pattern, oldSusiciousNode);
					
					if(suspiciousNode == null){
//						suspiciousNode = oldSusiciousNode;
						suspiciousNode = findNextSuspiciousNodeByPattern(pattern, oldSusiciousNode);
						break;
					}
					else{
						this.range.skipPoints.add(suspiciousNode);
						path = new PathInstance(suspiciousNode, oldSusiciousNode);					
					}
				}
				
				this.lastNode = currentNode;
				return oldSusiciousNode;
			}
			else{
				this.lastNode = currentNode;
				return suspiciousNode;				
			}
		}
		
		
		return null;
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
	private TraceNode findNextSuspiciousNodeByPattern(PotentialCorrectPattern pattern, 
			TraceNode oldSusiciousNode){
		PathInstance labelPath = pattern.getLabelInstance();
		
		Variable causingVariable = findCausingVarOfLabelPath(labelPath);
		
		for(TraceNode dominator: oldSusiciousNode.getDominator().keySet()){
			for(VarValue writtenVar: dominator.getWrittenVariables()){
				Variable writtenVariable = writtenVar.getVariable();
				
				String causingVarID = causingVariable.getVarID();
				String causingSimpleVarID = Variable.truncateSimpleID(causingVarID);
				String writtenVarID = writtenVariable.getVarID();
				String writtenSimpleVarID = Variable.truncateSimpleID(writtenVarID);
				
				if(causingSimpleVarID.equals(writtenSimpleVarID) || 
						causingVariable.getName().equals(writtenVariable.getName())){
					return dominator;
				}
			}
		}
		
		
		return null;
	}
	
	/**
	 * Find the variable which causes the jump of label path of the pattern.
	 */
	private Variable findCausingVarOfLabelPath(PathInstance labelPath){
		Variable causingVariable = null;
		TraceNode producer = labelPath.getStartNode();
		for(VarValue readVar: labelPath.getEndNode().getReadVariables()){
			for(VarValue writtenVar: producer.getWrittenVariables()){
				if(writtenVar.getVarID().equals(readVar.getVarID())){
					causingVariable = readVar.getVariable();
					break;
				}
			}
		}
		
		return causingVariable;
	}

	private TraceNode findNodeBySuspiciousnessDistribution(Trace trace, TraceNode currentNode){
		List<AttributionVar> readVars = constructAttributionRelation(currentNode);
		
		//System.currentTimeMillis();
		
		AttributionVar focusVar = Settings.interestedVariables.findFocusVar(readVars);
		
		TraceNode suspiciousNode = null;
		
		if(focusVar != null){
//			long t1 = System.currentTimeMillis();
			trace.distributeSuspiciousness(Settings.interestedVariables);
//			long t2 = System.currentTimeMillis();
//			System.out.println("time for distributeSuspiciousness: " + (t2-t1));
			
			suspiciousNode = trace.findMostSupiciousNode(focusVar); 
		}
		
		return suspiciousNode;
	}
	
	private List<AttributionVar> constructAttributionRelation(TraceNode currentNode){
		List<AttributionVar> readVars = new ArrayList<>();
		for(VarValue writtenVarValue: currentNode.getWrittenVariables()){
			String writtenVarID = writtenVarValue.getVarID();
			if(Settings.interestedVariables.contains(writtenVarID)){
				for(VarValue readVarValue: currentNode.getReadVariables()){
					String readVarID = readVarValue.getVarID();
					if(Settings.interestedVariables.contains(readVarID)){
						
						AttributionVar writtenVar = Settings.interestedVariables.findOrCreateVar(writtenVarID);
						AttributionVar readVar = Settings.interestedVariables.findOrCreateVar(readVarID);
						
						readVar.addChild(writtenVar);
						writtenVar.addParent(readVar);
					}
				}						
			}
		}
		
		for(VarValue readVarValue: currentNode.getReadVariables()){
			String readVarID = readVarValue.getVarID();
			if(Settings.interestedVariables.contains(readVarID)){
				AttributionVar readVar = Settings.interestedVariables.findOrCreateVar(readVarID);
				readVars.add(readVar);
			}
		}						
		
		Settings.interestedVariables.updateAttributionTrees();
		
		return readVars;
	}
	
}
