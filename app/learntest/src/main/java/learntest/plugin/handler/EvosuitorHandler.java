package learntest.plugin.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import evosuite.EvosuitParams;
import evosuite.MathEvosuiteTest;
import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.plugin.utils.IStatusUtils;

public class EvosuitorHandler extends AbstractLearntestHandler {
	private static Logger log = LoggerFactory.getLogger(EvosuitorHandler.class);
	
	@Override
	protected IStatus execute(IProgressMonitor monitor) throws CoreException {
		return Status.OK_STATUS;
	}
	
	@Override
	protected String getJobName() {
		return "Run single evosuite for a single method";
	}
	
	
	public RunTimeInfo generateTest(LearnTestParams params) throws CoreException{
		try {
			TargetMethod method = params.getTargetMethod();
			String target = method.getMethodFullName() + "." + method.getLineNum();
			List<String> methods= new LinkedList<>();
			methods.add(target);
			EvosuitParams p = new EvosuitParams();
			MathEvosuiteTest evosuitor = new MathEvosuiteTest();
			evosuitor.runMathProject(methods);
			return null;
		} catch (Exception e) {
			log.debug("Error when generating test: {}", e.getMessage());
			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
		} 
	}
	
}
