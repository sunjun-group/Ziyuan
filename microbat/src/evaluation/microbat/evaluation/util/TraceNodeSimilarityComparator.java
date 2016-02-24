package microbat.evaluation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import microbat.model.trace.TraceNode;
import microbat.model.value.ReferenceValue;
import microbat.model.value.VarValue;
import microbat.model.value.VirtualValue;

public class TraceNodeSimilarityComparator {

	public double compute(TraceNode traceNode1, TraceNode traceNode2) {
		
		if(traceNode1.hasSameLocation(traceNode2)){
			int commonReadVarWithSameValue = findCommonVarWithSameValue(traceNode1.getReadVariables(), traceNode1, 
					traceNode2.getReadVariables(), traceNode2);
			int commonWrittenVarWithSameValue = findCommonVarWithSameValue(traceNode1.getWrittenVariables(), traceNode1,
					traceNode2.getWrittenVariables(), traceNode2);
			
			int totalVars = traceNode1.getReadVariables().size() + traceNode1.getWrittenVariables().size() +
					traceNode2.getWrittenVariables().size() + traceNode2.getReadVariables().size();
			
			double score; 
			if(totalVars == 0){
				score = 1;
			}
			else{
				score = (2*(double)commonReadVarWithSameValue+2*commonWrittenVarWithSameValue)/totalVars;
			}
			return score;
		}
		
		return 0;
	}

	private int findCommonVarWithSameValue(List<VarValue> variables1, TraceNode node1, List<VarValue> variables2, TraceNode node2) {
		int common = 0;
		for(VarValue var1: variables1){
			for(VarValue var2: variables2){
				if(isCommon(var1, node1, var2, node2)){
					common++;
					break;
				}
			}
		}
		return common;
	}

	private boolean hasSameReadVars(TraceNode node1, TraceNode node2){
		List<VarValue> var1s = node1.getReadVariables();
		List<VarValue> var2s = node2.getReadVariables();
		
		boolean isExactlyTheSame = isExactlyTheSame(var1s, node1, var2s, node2);
		return isExactlyTheSame;
	}
	
	@SuppressWarnings("unchecked")
	private boolean isExactlyTheSame(List<VarValue> var1s, TraceNode node1, List<VarValue> var2s, TraceNode node2) {
		ArrayList<VarValue> clonedVar1s = (ArrayList<VarValue>) ((ArrayList<VarValue>)var1s).clone();
		ArrayList<VarValue> clonedVar2s = (ArrayList<VarValue>) ((ArrayList<VarValue>)var2s).clone();
		
		Iterator<VarValue> iter1 = clonedVar1s.iterator();
		while(iter1.hasNext()){
			VarValue var1 = iter1.next();
			
			Iterator<VarValue> iter2 = clonedVar2s.iterator();
			while(iter2.hasNext()){
				VarValue var2 = iter2.next();
				
				if(isCommon(var1, node1, var2, node2)){
					iter1.remove();
					iter2.remove();
					break;
				}
			}
		}
		
		return clonedVar1s.isEmpty() && clonedVar2s.isEmpty();
	}

	private boolean isCommon(VarValue var1, TraceNode node1, VarValue var2, TraceNode node2) {
		
		if(var1 instanceof VirtualValue && var2 instanceof VirtualValue){
			return var1.getStringValue().equals(var2.getStringValue());
		}
		else{
			boolean flag = isOrdinaryCommon(var1, var2);
			return flag;
		}
		
//		return false;
	}

	private TraceNode findDominatorReturn(TraceNode node, VarValue var) {
		for(TraceNode dataDominator: node.getDataDominator().keySet()){
			List<String> varIDs = node.getDataDominator().get(dataDominator);
			String varID = varIDs.get(0);
			
			if(var.getVarID().equals(varID)){
				return dataDominator;
			}
		}
		
		return null;
	}

	private boolean isOrdinaryCommon(VarValue var1, VarValue var2) {
		boolean flag1 = var1.getVarName().equals(var2.getVarName());
		boolean flag2 = true; 
		if(!(var1 instanceof ReferenceValue) && !(var2 instanceof ReferenceValue)){
			flag2 = var1.getStringValue().equals(var2.getStringValue());
		}
		
		return flag1 && flag2;
	}

}
