package learntest.plugin.handler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class RunAllJDartHandler extends AbstractLearntestHandler {

	
	
	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		Job job = new Job("Run JDart for All Methods") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		job.schedule();
		
		return null;
	}

	@Override
	protected String getJobName() {
		return "Run JDart for All Methods";
	}

}
