package learntest.plugin.handler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.LearnTestParams;
import learntest.core.commons.LearntestConstants;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.export.io.excel.MultiTrial;
import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.TrialExcelHandler;
import learntest.plugin.export.io.excel.TrialExcelReader;
import learntest.plugin.handler.filter.classfilter.ClassNameFilter;
import learntest.plugin.handler.filter.classfilter.TargetClassFilter;
import learntest.plugin.handler.filter.classfilter.TestableClassFilter;
import learntest.plugin.handler.filter.methodfilter.MethodNameFilter;
import learntest.plugin.handler.filter.methodfilter.NestedBlockChecker;
import learntest.plugin.handler.filter.methodfilter.TargetMethodFilter;
import learntest.plugin.handler.filter.methodfilter.TestableMethodFilter;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.SavRtException;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.SingleTimer;
import sav.settings.SAVTimer;

public class EvaluationHandler extends AbstractLearntestHandler {
	private static Logger log = LoggerFactory.getLogger(EvaluationHandler.class);
	private static final int EVALUATIONS_PER_METHOD = 5;
	private List<TargetMethodFilter> methodFilters;
	private List<TargetClassFilter> classFilters;
	private static Map<String, Trial> oldTrials;
	static {
		/* todo : skip old trial start*/
		try {
			TrialExcelReader reader = new TrialExcelReader(new File("E:/hairui/eclipse-java-mars-clean/eclipse/apache-common-math-2.2_0.xlsx"));
			oldTrials = reader.readDataSheet();
		} catch (Exception e) {
			// ignore
		}
		/* todo : skip old trial end */
	}
	
	private int curMethodIdx = 0;
	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		SingleTimer timer = SingleTimer.start("Evaluation all methods");
		curMethodIdx = 0;
		final List<IPackageFragmentRoot> roots = IProjectUtils.findTargetSourcePkgRoots(LearnTestUtil.getJavaProject());
		TrialExcelHandler excelHandler = null;
		try {
			excelHandler = new TrialExcelHandler(LearnTestConfig.getINSTANCE().getProjectName());
			initFilters(excelHandler.readOldTrials());
		} catch (Exception e1) {
			handleException(e1);
			return Status.CANCEL_STATUS;
		}
		SAVTimer.enableExecutionTimeout = true;
		SAVTimer.exeuctionTimeout = 300000;
		RunTimeCananicalInfo overalInfo = new RunTimeCananicalInfo(0, 0, 0);
		try {
			for(IPackageFragmentRoot root: roots){
				for (IJavaElement element : root.getChildren()) {
					if (element instanceof IPackageFragment) {
						RunTimeCananicalInfo info = runEvaluation((IPackageFragment) element, excelHandler);
						overalInfo.add(info);
					}
				}
			}
			log.info(overalInfo.toString());
		} catch (JavaModelException e) {
			handleException(e);
		}
		timer.logResults(log);
		return Status.OK_STATUS;
	}

	private void initFilters(Collection<Trial> oldTrials) {
		methodFilters = Arrays.asList(new TestableMethodFilter(), new NestedBlockChecker(),
				new MethodNameFilter(LearntestConstants.EXCLUSIVE_METHOD_FILE_NAME));
		classFilters = Arrays.asList(new TestableClassFilter(), new ClassNameFilter(getExcludedClasses()));
	}

	private List<String> getExcludedClasses() {
		/* TODO - temporary hard code */
		return Arrays.asList("org.apache.tools.ant.Main");
	}

	private RunTimeCananicalInfo runEvaluation(IPackageFragment pkg, TrialExcelHandler excelHandler)
			throws JavaModelException {
		RunTimeCananicalInfo info = new RunTimeCananicalInfo();
		for (IJavaElement javaElement : pkg.getChildren()) {
			if (javaElement instanceof IPackageFragment) {
				runEvaluation((IPackageFragment) javaElement, excelHandler);
			} else if (javaElement instanceof ICompilationUnit) {
				ICompilationUnit icu = (ICompilationUnit) javaElement;
				CompilationUnit cu = LearnTestUtil.convertICompilationUnitToASTNode(icu);
				boolean valid = true;
				for (TargetClassFilter classFilter : classFilters) {
					if (!classFilter.isValid(cu)) {
						valid = false;
						continue;
					}
				}
				if (!valid) {
					continue;
				}
				TestableMethodCollector collector = new TestableMethodCollector(cu, methodFilters);
				cu.accept(collector);
				List<TargetMethod> validMethods = collector.getValidMethods();
				updateRuntimeInfo(info, cu, collector.getTotalMethodNum(), validMethods.size());
				evaluateForMethodList(excelHandler, validMethods);
			}
		}
		return info;
	}

	private void updateRuntimeInfo(RunTimeCananicalInfo info, CompilationUnit cu, int totalMethods, int validMethods) {
		int length0 = cu.getLineNumber(cu.getStartPosition() + cu.getLength() - 1);
		info.addTotalLen(length0);
		info.validNum += validMethods;
		info.totalNum += totalMethods;
	}

	protected void evaluateForMethodList(TrialExcelHandler excelHandler, List<TargetMethod> targetMethods) {
		if (targetMethods.isEmpty()) {
			return;
		}
		
		for (TargetMethod targetMethod : targetMethods) {
			log.info("-----------------------------------------------------------------------------------------------");
			log.info("Method {}", ++curMethodIdx);

			/* todo : skip old trial start*/
//			String fullName = targetMethod.getMethodFullName();
//			int line = targetMethod.getLineNum();
//			if (oldTrials.containsKey(fullName+"_"+line)) {
//				continue;
//			}
			
			/* todo : skip old trial end */
			try{
			    PrintWriter writer = new PrintWriter("latest_working_method.txt", "UTF-8");
			    writer.println("working method: " + targetMethod.getMethodFullName());
			    writer.close();
			} catch (IOException e) {
			}

			
			MultiTrial multiTrial = new MultiTrial();
			for (int i = 0; i < EVALUATIONS_PER_METHOD; i++) {
				try {
					LearnTestParams params = initLearntestParams(targetMethod);
					Trial trial = evaluateLearntestForSingleMethod(params);
					if (trial != null) {
						multiTrial.addTrial(trial);
					}
				} catch (Exception e) {
					handleException(e);
				}
			}
			if (!multiTrial.isEmpty()) {
				try {
					excelHandler.export(multiTrial);
					logSuccessfulMethod(targetMethod);
				} catch (Exception e) {
					handleException(e);
				}
			}
		}
	}

	private static void logSuccessfulMethod(TargetMethod targetMethod) {
		try {
			FileUtils.appendFile(LearntestConstants.EXCLUSIVE_METHOD_FILE_NAME, 
					IMethodUtils.getMethodId(targetMethod.getMethodFullName(), targetMethod.getLineNum()) + "\n");
		} catch(SavRtException e) {
			// ignore
		}
	}

	private LearnTestParams initLearntestParams(TargetMethod targetMethod) {
		LearnTestParams params = new LearnTestParams(targetMethod);
		setSystemConfig(params);
		return params;
	}
	
	class RunTimeCananicalInfo {
		int validNum;
		int totalNum;
		int totalLen;
		
		public RunTimeCananicalInfo() {
			
		}
		
		public RunTimeCananicalInfo(int validNum, int totalNum, int totalLen) {
			this.validNum = validNum;
			this.totalNum = totalNum;
			this.totalLen = totalLen;
		}
		public void add(RunTimeCananicalInfo info) {
			totalNum += info.totalNum;
			validNum += info.validNum;
			totalLen += info.totalLen;
		}
		
		public void addTotalLen(int totalLen) {
			this.totalLen += totalLen;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("total valid methods: ").append(validNum).append("\n")
				.append("total methods: ").append(totalNum).append("\n")
				.append("total LOC: ").append(totalLen);
			return sb.toString();
		}
	}

	@Override
	protected String getJobName() {
		return "Do evaluation";
	}

}
