package learntest.handler;

import java.io.File;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import learntest.core.CodeCoverageGenerator;
import learntest.main.TestGenerator;
import learntest.util.LearnTestUtil;
import sav.common.core.utils.StopTimer;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

public class CodeCoverageHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// final IPackageFragmentRoot root =
		// LearnTestUtil.findMainPackageRootInProject();
		final List<IPackageFragmentRoot> roots = LearnTestUtil.findAllPackageRootInProject();
		Job job = new Job("Do evaluation") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					SAVTimer.enableExecutionTimeout = true;
					SAVTimer.exeuctionTimeout = 100000;
					
					AppJavaClassPath appClasspath = HandlerUtils.initAppJavaClassPath();
					TestGenerator.NUMBER_OF_INIT_TEST = 20;
					List<File> newTests = new TestGenerator(appClasspath).genTest();
					JavaCompiler jcompiler = new JavaCompiler(new VMConfiguration(appClasspath));
					jcompiler.compile(appClasspath.getTestTarget(), newTests);
					StopTimer timer = new StopTimer("learntest");
					timer.start();
					timer.newPoint("learntest");

					CodeCoverageGenerator generator = new CodeCoverageGenerator();
					CfgCoverage cfgCoverage = generator.generateCoverage(appClasspath);
					System.out.println(cfgCoverage);
					// TODO
				} catch (Exception e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}

		};

		job.schedule();

		return null;
	}

}
