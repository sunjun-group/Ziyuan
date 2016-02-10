package microbat.recommendation;

import java.util.ArrayList;
import java.util.List;

import microbat.model.AttributionVar;
import microbat.model.trace.PathInstance;
import microbat.model.trace.PotentialCorrectPattern;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.model.variable.Variable;
import microbat.util.Settings;

public class SuspiciousNodeRecommender {
	
	public TraceNode recommendSuspiciousNode(Trace trace, TraceNode currentNode, TraceNode lastestNode){
		if(lastestNode != null){
			PathInstance path = new PathInstance(currentNode, lastestNode);
			if(path.isPotentialCorrect()){
				Settings.potentialCorrectPatterns.addPathForPattern(path);			
			}
		}
		
		System.currentTimeMillis();
		
		//TODO
		boolean isFromSkipping = false;
		boolean isOverSkipping = false;
		if(isFromSkipping){
			if(isOverSkipping){
				
			}
		}
		else{
			TraceNode suspiciousNode = findNodeBySuspiciousnessDistribution(trace, currentNode);
			
			PathInstance path = new PathInstance(suspiciousNode, currentNode);
			while(Settings.potentialCorrectPatterns.containsPattern(path)){
				
				Settings.potentialCorrectPatterns.addPathForPattern(path);
				PotentialCorrectPattern pattern = Settings.potentialCorrectPatterns.getPattern(path);
				
				
				TraceNode oldSusiciousNode = suspiciousNode;
				suspiciousNode = findNextSuspiciousNodeByPattern(pattern, oldSusiciousNode);
				
				if(suspiciousNode == null){
					suspiciousNode = oldSusiciousNode;
					break;
				}
				else{
					path = new PathInstance(suspiciousNode, oldSusiciousNode);					
				}
			}
			
			return suspiciousNode;
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
		
		String causingVarID = findCausingVarOfLabelPath(labelPath);
		String simpleCausingVarID = Variable.truncateSimpleID(causingVarID);
		
		for(TraceNode dominator: oldSusiciousNode.getDominator().keySet()){
			for(VarValue writtenVar: dominator.getWrittenVariables()){
				String writtenVarID = writtenVar.getVarID();
				String simpleID = Variable.truncateSimpleID(writtenVarID);
				
				if(simpleCausingVarID.equals(simpleID)){
					return dominator;
				}
			}
		}
		
		
		return null;
	}
	
	/**
	 * Find the variable which causes the jump of label path of the pattern.
	 */
	private String findCausingVarOfLabelPath(PathInstance labelPath){
		String varID = null;
		TraceNode producer = labelPath.getStartNode();
		for(VarValue readVar: labelPath.getEndNode().getReadVariables()){
			for(VarValue writtenVar: producer.getWrittenVariables()){
				if(writtenVar.getVarID().equals(readVar.getVarID())){
					varID = readVar.getVarID();
					break;
				}
			}
		}
		
		return varID;
	}

	private TraceNode findNodeBySuspiciousnessDistribution(Trace trace, TraceNode currentNode){
		List<AttributionVar> readVars = constructAttributionRelation(currentNode);
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
	
	private TraceNode checkPattern(TraceNode suspiciousNode, TraceNode lastestNode) {
		// TODO Auto-generated method stub
		return null;
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
						
						readVars.add(readVar);
					}
				}						
			}
		}
		
		Settings.interestedVariables.updateAttributionTrees();
		
		return readVars;
	}
	
}
