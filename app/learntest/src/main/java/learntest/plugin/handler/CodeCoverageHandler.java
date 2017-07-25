package learntest.plugin.handler;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import learntest.core.CodeCoverage;
import learntest.core.LearnTestParams;
import learntest.core.LearntestParamsUtils;
import learntest.core.LearntestParamsUtils.GenTestPackage;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.TestGenerator;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.utils.StopTimer;
import sav.settings.SAVTimer;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

public class CodeCoverageHandler extends AbstractLearntestHandler {

	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		final List<IPackageFragmentRoot> roots = LearnTestUtil.findAllPackageRootInProject();
		try {
			SAVTimer.enableExecutionTimeout = true;
			SAVTimer.exeuctionTimeout = 100000;
			LearnTestParams params = initLearntestParamsFromPreference();
			GentestParams gentestParams = LearntestParamsUtils.createGentestParams(getAppClasspath(), params,
					GenTestPackage.INIT);
			gentestParams.setNumberOfTcs(20);
			List<File> newTests = new TestGenerator(getAppClasspath()).genTest(gentestParams).getJunitfiles();
			JavaCompiler jcompiler = new JavaCompiler(new VMConfiguration(getAppClasspath()));
			jcompiler.compile(getAppClasspath().getTestTarget(), newTests);
			StopTimer timer = new StopTimer("learntest");
			timer.start();
			timer.newPoint("learntest");

			CodeCoverage generator = new CodeCoverage();
			CfgCoverage cfgCoverage = generator.generateCoverage(getAppClasspath(), params);
			System.out.println(cfgCoverage);
			// TODO
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}
	
	@Override
	protected String getJobName() {
		return "code coverage";
	}

}
