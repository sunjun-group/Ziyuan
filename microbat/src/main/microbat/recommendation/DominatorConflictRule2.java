package microbat.recommendation;

import java.util.ArrayList;
import java.util.List;

import microbat.model.trace.StepVariableRelationEntry;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;


public class DominatorConflictRule2 extends ConflictRule {

	/**
	 * The steps will always be marked as "correct" in the process of debugging, otherwise, the debugging process is finished.
	 * 
	 * Given the order of a trace node. If all the read variables of this node (step) is correct. Thus, all of the 
	 * dominator trace nodes of this node is read-variable-correct.
	 * <br><br>
	 * If the above is not the case, then a conflict happen.
	 * 
	 * This method will return the node causing the conflicts if there is a conflict indeed.
	 * 
	 * @param order
	 * @return
	 */
	@Override
	public TraceNode checkConflicts(Trace trace, int order) {
		TraceNode node = trace.getExectionList().get(order-1);
		assert node.getVarsCorrectness()==TraceNode.READVARS_CORRECT;

		if(node.getDominator().keySet().isEmpty()){
			return null;
		}
		
		List<TraceNode> producerList = new ArrayList<>();
		for(VarValue var: node.getReadVariables()){
			String varID = var.getVarID();
			StepVariableRelationEntry entry = trace.getStepVariableTable().get(varID);
			
			if(!entry.getProducers().isEmpty()){
				TraceNode producer = entry.getProducers().get(0);
				if(producer.getVarsCorrectness()==TraceNode.READVARS_INCORRECT){
					producerList.add(producer);
				}
			}
		}
		
		if(!producerList.isEmpty()){
			TraceNode jumpNode = findOldestConflictNode(producerList);
			return jumpNode;
		}
		else{
			return null;
		}
	}

}
