package sav.tools.build;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionBuilder;
import sav.strategies.vm.VMRunner;

/**
 * @author LLT
 *
 */
public class SavJunitRunnerDeployer {

	public static void main(String[] args) throws SavException {
		CollectionBuilder<String, List<String>> cmd = new CollectionBuilder<String, List<String>>(new ArrayList<String>());
		VMRunner vmRunner = new VMRunner();
		
		String deployDir = ProjectConfiguration.getModuleFolder("active-learntest") + "/src/main/resources/";
		/* build sav.junit.runner and copy to active-learntest resources folder */
		cmd.append(ProjectConfiguration.getJavaHome() + "/bin/jar")
			.append("cf")
			.append(deployDir + "sav.testrunner.jar")
			.append("-C")
			.append(ProjectConfiguration.getModuleFolder("sav.junit.runner") + "/target/classes")
			.append("sav");
		vmRunner.startAndWaitUntilStop(cmd.toCollection());	
		cmd.clear();
		System.out.println("Deploy testrunner.jar to " + deployDir);
	}
}
