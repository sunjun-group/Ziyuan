package microbat.evaluation.handler;

import microbat.evaluation.GenerateRootCauseException;
import microbat.evaluation.Simulator;
import microbat.evaluation.junit.TestCaseParser;
import mutation.mutator.Mutator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class EvaluationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Job job = new Job("Do evaluation") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				
				TestCaseParser parser = new TestCaseParser();
//				parser.setUp();
				
				parser.runEvaluation();
				
				//archievedSimulation();
				
				
				
				
				return Status.OK_STATUS;
			}
		};
		
		job.schedule();
		
		return null;
	}

	
	private void archievedSimulation(){
		Simulator simulator = new Simulator();
		try {
			simulator.startSimulation();
		} catch (GenerateRootCauseException e) {
			e.printStackTrace();
		}
	}
}
