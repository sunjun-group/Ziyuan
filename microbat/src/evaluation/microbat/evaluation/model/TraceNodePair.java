package microbat.evaluation.model;

import java.util.ArrayList;
import java.util.List;

import microbat.algorithm.graphdiff.HierarchyGraphDiffer;
import microbat.model.trace.TraceNode;
import microbat.model.value.PrimitiveValue;
import microbat.model.value.ReferenceValue;
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

	/**
	 * 
	 * @return
	 */
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
	
	public String findSingleWrongWrittenVarID(){
		List<String> wrongVarIDs = new ArrayList<>();
		
		for(VarValue mutatedWrittenVar: mutatedNode.getWrittenVariables()){
			VarValue originalWrittenVar = findCorrespondingVarWithDifferentValue(mutatedWrittenVar, 
					originalNode.getWrittenVariables(), mutatedNode.getWrittenVariables());
			if(originalWrittenVar != null){
				wrongVarIDs.add(mutatedWrittenVar.getVarID());				
			}
		}
		
		if(!wrongVarIDs.isEmpty()){
			return wrongVarIDs.get(0);
		}
		
		return null;
	}
	
	/**
	 * Each time, the simulated user may select only one read variable. To this end, the priority of wrong variable
	 * is as follows:
	 * +++ primitive variable
	 * ++  reference variable
	 * +   virtual variable
	 * @return
	 */
	public String findSingleWrongReadVarID(){
		List<String> primitiveVars = new ArrayList<>();
		List<String> referenceVars = new ArrayList<>();
		List<String> virtualVars = new ArrayList<>();
		
		for(VarValue mutatedReadVar: mutatedNode.getReadVariables()){
			VarValue originalReadVar = findCorrespondingVarWithDifferentValue(mutatedReadVar, 
					originalNode.getReadVariables(), mutatedNode.getReadVariables());
			if(originalReadVar != null){
				if(mutatedReadVar instanceof PrimitiveValue){
					primitiveVars.add(mutatedReadVar.getVarID());
				}
				else if(mutatedReadVar instanceof ReferenceValue){
					referenceVars.add(mutatedReadVar.getVarID());
				}
				else if(mutatedReadVar instanceof VirtualValue){
					virtualVars.add(mutatedReadVar.getVarID());
				}
			}
		}
		
		if(!primitiveVars.isEmpty()){
			return primitiveVars.get(0);
		}
		else if(!referenceVars.isEmpty()){
			return referenceVars.get(0);
		}
		else if(!virtualVars.isEmpty()){
			return virtualVars.get(0);
		}
		else{
			return null;
		}
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
		else if(mutatedVar instanceof ReferenceValue){
			for(VarValue originalVar: originalList){
				if(originalVar instanceof ReferenceValue){
					if(mutatedVar.getVarName().equals(originalVar.getVarName())){
						ReferenceValue refVar1 = (ReferenceValue)mutatedVar;
						setChildren(refVar1, mutatedNode);
						ReferenceValue refVar2 = (ReferenceValue)originalVar;
						setChildren(refVar2, originalNode);
						
						if(refVar1.getChildren() != null && refVar2.getChildren() != null){
							HierarchyGraphDiffer differ = new HierarchyGraphDiffer();
							differ.diff(refVar1, refVar2);
							if(!differ.getDiffs().isEmpty()){
								return originalVar;						
							}								
						}
						else if(refVar1.getChildren() == null && refVar2.getChildren() == null){
							return originalVar;
						}
						
						
//						HierarchyGraphDiffer differ = new HierarchyGraphDiffer();
//						differ.diff(mutatedVar, originalVar);
//						if(!differ.getDiffs().isEmpty()){
//							return originalVar;							
//						}
					}
				}
			}
		}
		
		return null;
	}
	
	private void setChildren(ReferenceValue refVar, TraceNode node){
		if(refVar.getChildren()==null){
			if(node.getProgramState() != null){
				
				String varID = refVar.getVarID();
				varID = varID.substring(0, varID.indexOf(":"));
				
				VarValue vv = node.getProgramState().findVarValue(varID);
				if(vv != null){
					refVar.setChildren(vv.getChildren());
				}				
			}
		}
	}
}
