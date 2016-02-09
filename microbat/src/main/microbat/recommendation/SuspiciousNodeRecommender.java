package microbat.recommendation;

import java.util.ArrayList;
import java.util.List;

import microbat.model.AttributionVar;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.util.Settings;

public class SuspiciousNodeRecommender {
	
	public TraceNode findSuspiciousNode(Trace trace, TraceNode currentNode, TraceNode lastestNode){
		List<AttributionVar> readVars = constructAttributionRelation(currentNode);
		AttributionVar focusVar = Settings.interestedVariables.findFocusVar(readVars);
		
		TraceNode suspiciousNode = null;
		
		if(focusVar != null){
//			long t1 = System.currentTimeMillis();
			trace.distributeSuspiciousness(Settings.interestedVariables);
//			long t2 = System.currentTimeMillis();
//			System.out.println("time for distributeSuspiciousness: " + (t2-t1));
			
			suspiciousNode = trace.findMostSupiciousNode(focusVar); 
			
			TraceNode patternGeneratedNode = checkPattern(suspiciousNode, lastestNode);
			if(patternGeneratedNode != null){
				suspiciousNode = patternGeneratedNode;
			}
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
