package javacocoWrapper;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.Test;
import org.junit.runner.Request;

import faultLocalization.dto.CoverageReport;

public class JavaCoCo {

	private List<Request> extractTestCasesAsRequests(Class<?> junitClass) {
		ArrayList<Request> requests = new ArrayList<Request>();

		Method[] methods = junitClass.getMethods();
		for (Method method : methods) {
			Test test = method.getAnnotation(Test.class);

			if (test != null) {
				Request request = Request.method(junitClass, method.getName());
				requests.add(request);
			}

		}

		return requests;
	}
	
	public CoverageReport run(List<String> testingClassNames, Class<?> junitClass)
			throws Exception {
		CoverageReport report = new CoverageReport(testingClassNames);
		
		ArrayList<String> classNameForCoCo = new ArrayList<String>(testingClassNames);
		classNameForCoCo.add(junitClass.getName());
		
		// For instrumentation and runtime we need a IRuntime instance
		// to collect execution data:
		final IRuntime runtime = new LoggerRuntime();
		
		// The Instrumenter creates a modified version of our test target class
		// that contains additional probes for execution data recording:
		final Instrumenter instr = new Instrumenter(runtime);
		ArrayList<byte[]> instrumenteds = new ArrayList<byte[]>();
		
		for(String testingClassName: classNameForCoCo){
			instrumenteds.add(instr.instrument(getTargetClass(testingClassName), testingClassName));
		}
		
		
		// In this tutorial we use a special class loader to directly load the
		// instrumented class definition from a byte[] instances.
		final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
		
		for(int j = 0; j < classNameForCoCo.size(); j++){
			String testingClassName = classNameForCoCo.get(j);
			memoryClassLoader.addDefinition(testingClassName, instrumenteds.get(j));
		}
		
		// Now we're ready to run our instrumented class and need to startup the
		// runtime first:
		final RuntimeData data = new RuntimeData();
		runtime.startup(data);
		
		List<Request> testcases = extractTestCasesAsRequests(memoryClassLoader.loadClass(junitClass.getName()));
		for(int i = 0; i < testcases.size(); i++){
			Request testcase = testcases.get(i);
			
			data.reset();
			final Class<?> targetClass = memoryClassLoader.loadClass(RequestExecution.class.getName());

			// Here we execute our test target class through its Runnable interface:
			final Runnable targetInstance = (Runnable) targetClass.newInstance();
			
			Method setRequest = targetClass.getMethod("setRequest", Request.class);
			setRequest.invoke(targetInstance, testcase);
			
			targetInstance.run();
			
			Method getResult = targetClass.getMethod("getResult");
			boolean isPassed = (Boolean) getResult.invoke(targetInstance);

			
			// At the end of test execution we collect execution data and shutdown
			// the runtime:
			final ExecutionDataStore executionData = new ExecutionDataStore();
			final SessionInfoStore sessionInfos = new SessionInfoStore();
			data.collect(executionData, sessionInfos, false);

			// Together with the original class definition we can calculate coverage
			// information:
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
			for(String testingClassName: classNameForCoCo){
				analyzer.analyzeClass(getTargetClass(testingClassName), testingClassName);
			}
			
			// Let's dump some metrics and line coverage information:
			for (final IClassCoverage cc : coverageBuilder.getClasses()) {
				
				//do not display data for junit test file
				if (!cc.getName().endsWith(junitClass.getSimpleName())) {

					for (int j = cc.getFirstLine(); j <= cc.getLastLine(); j++) {
						ILine lineInfo = cc.getLine(j);
						if(lineInfo.getStatus() != ICounter.EMPTY){
							boolean isCovered = lineInfo.getStatus() != ICounter.NOT_COVERED;
							report.addInfo(i, cc.getName(), j, isPassed, isCovered);
						}
						
					}
				}
			}
		}
		runtime.shutdown();
		return report;
	}

	private InputStream getTargetClass(final String name) {
		final String resource = '/' + name.replace('.', '/') + ".class";
		return getClass().getResourceAsStream(resource);
	}

}
