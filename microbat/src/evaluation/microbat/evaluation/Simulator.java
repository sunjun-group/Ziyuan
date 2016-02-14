package microbat.evaluation;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import microbat.Activator;
import microbat.model.Fault;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.model.variable.Variable;
import microbat.recommendation.SuspiciousNodeRecommender;
import microbat.recommendation.conflicts.ConflictRuleChecker;
import microbat.util.Settings;
import microbat.views.DebugFeedbackView;

public class Simulator {
	
	private SuspiciousNodeRecommender recommender = new SuspiciousNodeRecommender();
	private SimulatedUser user = new SimulatedUser();
	
	public void startSimulation(){
		
		Trace trace = new TraceModelConstructor().constructTraceModel();
		
		Fault shownFault = generateShownFault(trace);
		
		TraceNode shownFaultNode = shownFault.getBuggyNode();
		
		List<TraceNode> dominators = shownFaultNode.findAllDominators();
		
		Fault rootCause = generateRootCause(dominators);
		
		TraceNode suspiciousNode = shownFaultNode;
		Feedback feedback = user.feedback(suspiciousNode, rootCause, dominators);
		
		boolean isBugFound = rootCause.getBuggyNode().getOrder()==suspiciousNode.getOrder();
		while(!isBugFound){
			
			suspiciousNode = findSuspicioiusNode(suspiciousNode, feedback, trace);
			
			isBugFound = rootCause.getBuggyNode().getOrder()==suspiciousNode.getOrder();
			
			if(!isBugFound){
				feedback = user.feedback(suspiciousNode, rootCause, dominators);				
			}
			
		}
	}

	private TraceNode findSuspicioiusNode(TraceNode currentNode, Feedback feedback, Trace trace) {
		setCurrentNodeCheck(trace, currentNode);
		
		TraceNode suspiciousNode = null;
		
		ConflictRuleChecker conflictRuleChecker = new ConflictRuleChecker();
		TraceNode conflictNode = conflictRuleChecker.checkConflicts(trace, currentNode.getOrder());
		
		if(conflictNode == null){
			suspiciousNode = recommender.recommendSuspiciousNode(trace, currentNode);
		}
		else{
			suspiciousNode = conflictNode;
		}
		
		return suspiciousNode;
	}
	
	private void setCurrentNodeCheck(Trace trace, TraceNode currentNode) {
		int checkTime = trace.getCheckTime()+1;
		currentNode.setCheckTime(checkTime);
		trace.setCheckTime(checkTime);
	}

	private Fault generateRootCause(List<TraceNode> dominators) throws GenerateRootCauseException{
		
		int count = 0;
		
		while(true){
			int index = (int) (dominators.size()*Math.random());
			TraceNode node = dominators.get(index);
			
			if(!node.getWrittenVariables().isEmpty()){
				VarValue var = node.getWrittenVariables().get(0);
				Fault fault = new Fault(node, var);
				return fault;
			}
			
			count++;
			if(count > 50){
				String message = "encounter infinite loop when generating the root cause of a trace";
				GenerateRootCauseException ex = new GenerateRootCauseException(message);
				
				throw ex;
			}
		}
	}

	/**
	 * The general idea is that the last node writing variable is the step for output.
	 * @param trace
	 * @return
	 */
	private Fault generateShownFault(Trace trace) {
		for(int i=trace.size()-1; i>=0; i--){
			TraceNode node = trace.getExectionList().get(i);
			if(!node.getWrittenVariables().isEmpty()){
				VarValue var = node.getWrittenVariables().get(0);
				Fault fault = new Fault(node, var);
				return fault;
			}
		}
		
		return null;
	}

	
}
