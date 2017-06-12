package jdart.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import jdart.core.JDartCore;
import jdart.core.JDartParams;
import learntest.core.commons.data.testtarget.TargetMethod;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.TestGenerator;
import learntest.core.gentest.TestGenerator.GentestResult;
import learntest.handler.AbstractLearntestHandler;
import learntest.main.LearnTestParams;
import sav.common.core.SavException;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

public class RunJDartHandler extends AbstractLearntestHandler {

	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		try {
			/* init params */
			LearnTestParams learntestParams = LearnTestParams.initFromLearnTestConfig();
			GentestParams gentestParams = initGentestParams(learntestParams);
			JDartParams jdartParams = initJDartParams(learntestParams);
			/* generate testcase and jdart entry */
			GentestResult testResult = generateTestcases(gentestParams);
			/* run jdart */
			jdartParams.setMainEntry(testResult.getMainClassName());
			JDartCore jdartCore = new JDartCore();
			jdartCore.run(jdartParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Status.OK_STATUS;
	}
	
	private GentestResult generateTestcases(GentestParams params) throws ClassNotFoundException, SavException {
		AppJavaClassPath appClasspath = getAppClasspath();
		TestGenerator testGenerator = new TestGenerator(appClasspath);
		JavaCompiler compiler = new JavaCompiler(new VMConfiguration(appClasspath));
		params.setGenerateMainClass(true);
		GentestResult result = testGenerator.genTest(params);
		compiler.compile(appClasspath.getTestTarget(), result.getAllFiles());
		return result;
	}

	private JDartParams initJDartParams(LearnTestParams learntestParams) throws CoreException {
		JDartParams params = new JDartParams();
		params.setAppProperties(PluginUtils.loadAbsolutePath("libs/jdart/jpf.properties"));
		params.setSiteProperties(PluginUtils.loadAbsolutePath("libs/jpf.properties"));
		TargetMethod targetMethod = learntestParams.getTargetMethod();
		params.setClassName(targetMethod.getClassName());
		params.setMethodName(targetMethod.getMethodName());
		params.setParamString(buildJDartParamStr(targetMethod));
		params.setClasspathStr(StringUtils.join(getAppClasspath().getClasspaths(), ";"));
		return params;
	}

	private String buildJDartParamStr(TargetMethod targetMethod) {
		int lastIdx = targetMethod.getParams().size() - 1;
		StringBuilder sb = new StringBuilder("(");
		for (int i = 0; i <= lastIdx; i++) {
			sb.append(targetMethod.getParams().get(i))
				.append(":")
				.append(targetMethod.getParamTypes().get(i));
			if (i < lastIdx) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	protected String getJobName() {
		return "Run JDart";
	}
}
