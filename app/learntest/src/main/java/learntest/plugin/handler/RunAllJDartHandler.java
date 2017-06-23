package learntest.plugin.handler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class RunAllJDartHandler extends AbstractLearntestHandler {

	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}

	@Override
	protected String getJobName() {
		return "Run JDart for All Methods";
	}

}
