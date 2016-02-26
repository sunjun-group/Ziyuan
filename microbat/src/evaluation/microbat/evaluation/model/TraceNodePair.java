package microbat.evaluation.model;

import java.util.ArrayList;
import java.util.List;

import microbat.model.trace.TraceNode;
import microbat.model.value.PrimitiveValue;
import microbat.model.value.VarValue;
import microbat.model.value.VirtualValue;
import microbat.util.PrimitiveUtils;

public class TraceNodePair {

	private TraceNode originalNode;
	private TraceNode mutatedNode;
	
	private boolean isExactlySame;
	
	public TraceNodePair(TraceNode mutatedNode, TraceNode originalNode) {
		this.originalNode = originalNode;
		this.mutatedNode = mutatedNode;
	}

	public TraceNode getOriginalNode() {
		return originalNode;
	}

	public void setOriginalNode(TraceNode originalNode) {
		this.originalNode = originalNode;
	}

	public TraceNode getMutatedNode() {
		return mutatedNode;
	}

	public void setMutatedNode(TraceNode mutatedNode) {
		this.mutatedNode = mutatedNode;
	}

	public void setExactSame(boolean b) {
		this.isExactlySame = b;
	}

	public boolean isExactSame(){
		return this.isExactlySame;
	}

	@Override
	public String toString() {
		return "TraceNodePair [originalNode=" + originalNode + ", mutatedNode="
				+ mutatedNode + ", isExactlySame=" + isExactlySame + "]";
	}

	public List<String> findWrongVarIDs() {
		List<String> wrongVarIDs = new ArrayList<>();
		
		for(VarValue mutatedReadVar: mutatedNode.getReadVariables()){
			VarValue originalReadVar = findCorrespondingVarWithDifferentValue(mutatedReadVar, 
					originalNode.getReadVariables(), mutatedNode.getReadVariables());
			if(originalReadVar != null){
				wrongVarIDs.add(mutatedReadVar.getVarID());				
			}
		}
		
		for(VarValue mutatedWrittenVar: mutatedNode.getWrittenVariables()){
			VarValue originalWrittenVar = findCorrespondingVarWithDifferentValue(mutatedWrittenVar, 
					originalNode.getWrittenVariables(), mutatedNode.getWrittenVariables());
			if(originalWrittenVar != null){
				wrongVarIDs.add(mutatedWrittenVar.getVarID());				
			}
		}
		
		System.currentTimeMillis();
		
		return wrongVarIDs;
	}

	private VarValue findCorrespondingVarWithDifferentValue(VarValue mutatedVar, List<VarValue> originalList, List<VarValue> mutatedList) {
		if(mutatedVar instanceof VirtualValue && PrimitiveUtils.isPrimitiveTypeOrString(mutatedVar.getType())){
			for(VarValue originalVar: originalList){
				if(originalVar instanceof VirtualValue && mutatedVar.getType().equals(originalVar.getType())){
					/**
					 * currently, the mutation will not change the order of virtual variable.
					 */
					int mutatedIndex = mutatedList.indexOf(mutatedVar);
					int originalIndex = originalList.indexOf(originalVar);
					
					if(mutatedIndex==originalIndex && !mutatedVar.getStringValue().equals(originalVar.getStringValue())){
						return originalVar;
					}
				}
				
			}
		}
		else if(mutatedVar instanceof PrimitiveValue){
			for(VarValue originalVar:originalList){
				if(originalVar instanceof PrimitiveValue){
					if(mutatedVar.getVarName().equals(originalVar.getVarName()) 
							&& !mutatedVar.getStringValue().equals(originalVar.getStringValue())){
						return originalVar;
					}
				}
			}
		}
		
		return null;
	}
	
	
}
