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
		
		if(traceNode1.getOrder() == 41 && traceNode2.getOrder() == 38){
			System.currentTimeMillis();
		}
		
		if(traceNode1.hasSameLocation(traceNode2)){
			double commonReadVarWithSameValue = findCommonVarWithSameValue(traceNode1.getReadVariables(), 
					traceNode2.getReadVariables());
			double commonWrittenVarWithSameValue = findCommonVarWithSameValue(traceNode1.getWrittenVariables(),
					traceNode2.getWrittenVariables());
			
			int totalVars = traceNode1.getReadVariables().size() + traceNode1.getWrittenVariables().size() +
					traceNode2.getWrittenVariables().size() + traceNode2.getReadVariables().size();
			
			double score; 
			if(totalVars == 0){
				score = 1;
			}
			else{
				score = (2*(double)commonReadVarWithSameValue+2*commonWrittenVarWithSameValue)/totalVars;
			}
			
			/**
			 * give a value for same location similarity
			 */
			return 0.05 + 0.95*score;
		}
		
		return 0;
	}

	private double findCommonVarWithSameValue(List<VarValue> variables1, List<VarValue> variables2) {
		double common = 0;
		for(VarValue var1: variables1){
			for(VarValue var2: variables2){
				double commonness = findCommonness(var1, var2);
				if(commonness > 0){
					common += commonness;
					break;
				}
//				if(isCommon(var1, var2)){
//					common++;
//					break;
//				}
			}
		}
		return common;
	}

//	private boolean hasSameReadVars(TraceNode node1, TraceNode node2){
//		List<VarValue> var1s = node1.getReadVariables();
//		List<VarValue> var2s = node2.getReadVariables();
//		
//		boolean isExactlyTheSame = isExactlyTheSame(var1s, node1, var2s, node2);
//		return isExactlyTheSame;
//	}
	
//	@SuppressWarnings("unchecked")
//	private boolean isExactlyTheSame(List<VarValue> var1s, TraceNode node1, List<VarValue> var2s, TraceNode node2) {
//		ArrayList<VarValue> clonedVar1s = (ArrayList<VarValue>) ((ArrayList<VarValue>)var1s).clone();
//		ArrayList<VarValue> clonedVar2s = (ArrayList<VarValue>) ((ArrayList<VarValue>)var2s).clone();
//		
//		Iterator<VarValue> iter1 = clonedVar1s.iterator();
//		while(iter1.hasNext()){
//			VarValue var1 = iter1.next();
//			
//			Iterator<VarValue> iter2 = clonedVar2s.iterator();
//			while(iter2.hasNext()){
//				VarValue var2 = iter2.next();
//				
//				if(isCommon(var1, var2)){
//					iter1.remove();
//					iter2.remove();
//					break;
//				}
//			}
//		}
//		
//		return clonedVar1s.isEmpty() && clonedVar2s.isEmpty();
//	}

	private double findCommonness(VarValue var1, VarValue var2) {
		double commonness = 0;
		
		if(var1 instanceof VirtualValue && var2 instanceof VirtualValue){
			if(var1.getStringValue().equals(var2.getStringValue())){
				commonness = 1;
			}
		}
		else{
			boolean flag1 = var1.getVarName().equals(var2.getVarName());
			if(flag1){
				commonness += 0.5;
				if(!(var1 instanceof ReferenceValue) && !(var2 instanceof ReferenceValue)){
					if(var1.getStringValue().equals(var2.getStringValue())){
						commonness += 0.5;
					}
				}
				else if((var1 instanceof ReferenceValue) && (var2 instanceof ReferenceValue)){
					commonness += 0.5;
				}
			}
		}
		
		return commonness;
	}

}
