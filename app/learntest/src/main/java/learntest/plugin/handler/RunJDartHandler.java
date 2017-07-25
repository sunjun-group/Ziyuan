package learntest.plugin.handler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import learntest.core.JDartLearntest;
import learntest.core.LearnTestParams;

public class RunJDartHandler extends AbstractLearntestHandler {

	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		try {
			JDartLearntest jdart = new JDartLearntest(getAppClasspath());
			LearnTestParams params = initLearntestParamsFromPreference();
			jdart.jdart(params);
//			jdart.run(initLearntestParams());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}
	
	@Override
	protected String getJobName() {
		return "Run JDart";
	}
}
