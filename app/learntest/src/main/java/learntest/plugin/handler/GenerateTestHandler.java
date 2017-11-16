package learntest.plugin.handler;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.TestRunTimeInfo;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.machinelearning.FormulaInfo;
import learntest.plugin.LearnTestConfig;
import learntest.plugin.utils.IProjectUtils;
import learntest.plugin.utils.IStatusUtils;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.utils.TextFormatUtils;

public class GenerateTestHandler extends AbstractLearntestHandler {
	private static Logger log = LoggerFactory.getLogger(GenerateTestHandler.class);
	
	@Override
	protected IStatus execute(IProgressMonitor monitor) throws CoreException {
		generateTest();
		refreshProject();
		log.debug("Finish!");
		return Status.OK_STATUS;
	}
	
	@Override
	protected String getJobName() {
		return "Run single learntest for a single method";
	}
	
//	public RunTimeInfo generateTest() throws CoreException{
//		try {
//			LearnTestParams params = initLearntestParamsFromPreference();
//			RunTimeInfo runtimeInfo = runLearntest(params);
//			return runtimeInfo;
//		} catch (Exception e) {
//			log.debug("Error when generating test: {}", e.getMessage());
//			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
//		} 
//	}
	
	public RunTimeInfo generateTest() throws CoreException{
		try {
			System.currentTimeMillis();
			LearnTestParams l2tParam = initLearntestParamsFromPreference();
			
			LearnTestParams jdartParam = l2tParam.createNew();
			jdartParam.setApproach(LearnTestApproach.JDART);
			RunTimeInfo jdartInfo = runJdart(jdartParam);
			
			l2tParam.setApproach(LearnTestApproach.L2T);
			l2tParam.setCu(constructCu(l2tParam.getTargetMethod().getClassName()));
			l2tParam.setInitialTests(jdartParam.getGeneratedInitTest());
			RunTimeInfo l2tRuntimeInfo = runLearntest(l2tParam);

			LearnTestParams randoopParam = l2tParam.createNew();
			randoopParam.setApproach(LearnTestApproach.RANDOOP);
			randoopParam.setInitialTests(l2tParam.getInitialTests());
			randoopParam.setMaxTcs(l2tRuntimeInfo.getTestCnt());
			
			log.info("run randoop..");
			RunTimeInfo ranInfo = runLearntest(randoopParam);		
			
			printRuntimeInfo(jdartInfo, jdartParam);
			printRuntimeInfo(l2tRuntimeInfo, l2tParam);
			StringBuffer sb = new StringBuffer();
			sb.append("learned formulas : =====================================");
			for (FormulaInfo formulaInfo : ((TestRunTimeInfo)l2tRuntimeInfo).getLearnedFormulas()) {
				sb.append(formulaInfo + "\n");
			}
			log.info(sb.toString());
			printRuntimeInfo(ranInfo, randoopParam);
			
			return l2tRuntimeInfo;
		} catch (Exception e) {
			log.debug("Error when generating test: {}", e.getMessage());
			throw new CoreException(IStatusUtils.exception(e, e.getMessage()));
		} 
	}
	
	private CompilationUnit constructCu(String className) {

		String projectName = LearnTestConfig.getINSTANCE().getProjectName();
		final List<IPackageFragmentRoot> roots = IProjectUtils
				.getSourcePkgRoots(IProjectUtils.getJavaProject(projectName));
		try {
			for (IPackageFragmentRoot root : roots) {
				for (IJavaElement element : root.getChildren()) {
					if (element instanceof IPackageFragment) {
						IPackageFragment pkg = (IPackageFragment) element; 
						for (IJavaElement javaElement : pkg.getChildren()) {
							if (javaElement instanceof ICompilationUnit) {
								if (javaElement.getElementName().equals(className.substring(className.lastIndexOf('.')+1)+".java")) {
									ICompilationUnit icu = (ICompilationUnit) javaElement;
									CompilationUnit cu = LearnTestUtil.convertICompilationUnitToASTNode(icu);
									return cu;
								}
							}
						}
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
