package sav.tools.build;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionBuilder;
import sav.common.core.utils.FileUtils;
import sav.strategies.vm.VMRunner;

/**
 * @author LLT
 *
 */
public class SavJunitRunnerDeployer {

	public static void main(String[] args) throws SavException {
		GentestModelDeployer.main(args);
		
		CollectionBuilder<String, List<String>> cmd = new CollectionBuilder<String, List<String>>(new ArrayList<String>());
		VMRunner vmRunner = new VMRunner();
		
		String resourceDir = ProjectConfiguration.getModuleFolder("active-learntest") + "/src/main/resources/";
		String libsFolder = ProjectConfiguration.getModuleFolder("active-learntest") + "/libs";
		/* build sav.junit.runner and copy to active-learntest resources folder */
		String jarDestFile = resourceDir + "sav.testrunner.jar";
		cmd.append(ProjectConfiguration.getJavaHome() + "/bin/jar")
			.append("cf")
			.append(jarDestFile)
			.append("-C")
			.append(ProjectConfiguration.getModuleFolder("sav.junit.runner") + "/target/classes")
			.append("sav")
			.append("-C")
			.append(ProjectConfiguration.getModuleFolder("gentest-model") + "/target/classes")
			.append("gentest");
		vmRunner.startAndWaitUntilStop(cmd.toCollection());	
		cmd.clear();
		System.out.println("Deploy testrunner.jar to " + resourceDir);
		FileUtils.copyFileToFolder(jarDestFile , libsFolder, true);
		System.out.println("Deploy testrunner.jar to " + libsFolder);
	}
	
}
