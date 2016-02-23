package microbat.evaluation.handler;

import microbat.evaluation.GenerateRootCauseException;
import microbat.evaluation.SimulatedMicroBat;
import microbat.evaluation.junit.TestCaseAnalyzer;
import mutation.mutator.Mutator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;

public class EvaluationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Job job = new Job("Do evaluation") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				
				TestCaseAnalyzer parser = new TestCaseAnalyzer();
//				parser.setUp();
				
				try {
					parser.runEvaluation();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
				//archievedSimulation();
				
				return Status.OK_STATUS;
			}
		};
		
		job.schedule();
		
		return null;
	}

	
	private void archievedSimulation(){
		SimulatedMicroBat simulator = new SimulatedMicroBat();
		try {
			simulator.startSimulation();
		} catch (GenerateRootCauseException e) {
			e.printStackTrace();
		}
	}
}
