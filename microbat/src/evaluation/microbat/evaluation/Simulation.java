package microbat.evaluation;

import microbat.Activator;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.variable.Variable;

public class Simulation {
	
	class RootCause{
		TraceNode buggyNode;
		Variable causingVariable;
		
		public RootCause(TraceNode buggyNode, Variable causingVariable) {
			super();
			this.buggyNode = buggyNode;
			this.causingVariable = causingVariable;
		}
		
		
	}
	
	private SimulatedUser user = new SimulatedUser();
	
	public void startSimulation(){
		
		Trace trace = new TraceModelConstructor().constructTraceModel();
		
		RootCause rootCause = generateRootCauseTraceNode();
		
		TraceNode shownFaultNode = findShownFaultNode();
		Variable wrongOutputVar = findWrongOutputVariable(trace, rootCause);
		
		
		TraceNode suspiciousNode = shownFaultNode;
		boolean isBugFound = rootCause.buggyNode.getOrder()==suspiciousNode.getOrder();
		while(!isBugFound){
			
			suspiciousNode = user.feedback(trace, suspiciousNode);
			
			isBugFound = rootCause.buggyNode.getOrder()==suspiciousNode.getOrder();
		}
	}
}
