package javacocoWrapper;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class JavaCoCo {
	
	/**
	 * Creates a new example instance printing to the given stream.
	 * 
	 * @param out
	 *            stream for outputs
	 */
	public JavaCoCo(final PrintStream out) {
		this.out = out;
	}
	
	
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
	
	public void run(List<String> testingClassNames, Class<?> junitClass)
			throws Exception {

		List<Request> requests = extractTestCasesAsRequests(junitClass);

		for (Request request : requests) {
			// For instrumentation and runtime we need a IRuntime instance
			// to collect execution data:
			final IRuntime runtime = new LoggerRuntime();

			// The Instrumenter creates a modified version of our test target class
			// that contains additional probes for execution data recording:
			final Instrumenter instr = new Instrumenter(runtime);
			ArrayList<byte[]> instrumenteds = new ArrayList<byte[]>();
						
			for(String testingClassName: testingClassNames){
				instrumenteds.add(instr.instrument(getTargetClass(testingClassName), testingClassName));
			}
			

			// Now we're ready to run our instrumented class and need to startup the
			// runtime first:
			final RuntimeData data = new RuntimeData();
			runtime.startup(data);

			// In this tutorial we use a special class loader to directly load the
			// instrumented class definition from a byte[] instances.
			final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
			
			for(int i = 0; i < testingClassNames.size(); i++){
				String testingClassName = testingClassNames.get(i);
				memoryClassLoader.addDefinition(testingClassName, instrumenteds.get(i));
			}
			

			final Class<?> targetClass = memoryClassLoader.loadClass(RequestExecution.class.getName());

			// Here we execute our test target class through its Runnable interface:
			final Runnable targetInstance = (Runnable) targetClass.newInstance();
			
//			for (Method method : targetClass.getMethods()) {
//				if(method.getName().equals(anObject))
//				System.out.println(method.getName());
//			}
			
			Method setRequest = targetClass.getMethod("setRequest", Request.class);
			setRequest.invoke(targetInstance, request);
			
			targetInstance.run();

			// At the end of test execution we collect execution data and shutdown
			// the runtime:
			final ExecutionDataStore executionData = new ExecutionDataStore();
			final SessionInfoStore sessionInfos = new SessionInfoStore();
			data.collect(executionData, sessionInfos, false);
			runtime.shutdown();

			// Together with the original class definition we can calculate coverage
			// information:
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
			for(String testingClassName: testingClassNames){
				analyzer.analyzeClass(getTargetClass(testingClassName), testingClassName);
			}
			
			// Let's dump some metrics and line coverage information:
			for (final IClassCoverage cc : coverageBuilder.getClasses()) {
				out.printf("Coverage of class %s%n", cc.getName());

				printCounter("instructions", cc.getInstructionCounter());
				printCounter("branches", cc.getBranchCounter());
				printCounter("lines", cc.getLineCounter());
				printCounter("methods", cc.getMethodCounter());
				printCounter("complexity", cc.getComplexityCounter());

				for (int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
					out.printf("Line %s: %s%n", Integer.valueOf(i), getColor(cc
							.getLine(i).getStatus()));
				}
			}
		}

	}
	
	private InputStream getTargetClass(final String name) {
		final String resource = '/' + name.replace('.', '/') + ".class";
		return getClass().getResourceAsStream(resource);
	}
	
	private final PrintStream out;
	
	private void printCounter(final String unit, final ICounter counter) {
		final Integer missed = Integer.valueOf(counter.getMissedCount());
		final Integer total = Integer.valueOf(counter.getTotalCount());
		out.printf("%s of %s %s missed%n", missed, total, unit);
	}

	private String getColor(final int status) {
		switch (status) {
		case ICounter.NOT_COVERED:
			return "red";
		case ICounter.PARTLY_COVERED:
			return "yellow";
		case ICounter.FULLY_COVERED:
			return "green";
		}
		return "";
	}
	

}
