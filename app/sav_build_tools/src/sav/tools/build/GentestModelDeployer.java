package sav.tools.build;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionBuilder;
import sav.common.core.utils.FileUtils;
import sav.strategies.vm.VMRunner;

public class GentestModelDeployer {

	public static void main(String[] args) throws SavException {
		CollectionBuilder<String, List<String>> cmd = new CollectionBuilder<String, List<String>>(new ArrayList<String>());
		VMRunner vmRunner = new VMRunner();
		
		String gentestLibs = ProjectConfiguration.getModuleFolder("gentest") + "/lib/";
		String savJunitRunnerLibs = ProjectConfiguration.getModuleFolder("sav.junit.runner") + "/libs";
		/* build sav.junit.runner and copy to active-learntest resources folder */
		String jarDestFile = gentestLibs + "gentest-model.jar";
		cmd.append(ProjectConfiguration.getJavaHome() + "/bin/jar")
			.append("cf")
			.append(jarDestFile)
			.append("-C")
			.append(ProjectConfiguration.getModuleFolder("gentest-model") + "/target/classes")
			.append("gentest");
		vmRunner.startAndWaitUntilStop(cmd.toCollection());	
		cmd.clear();
		System.out.println("Deploy gentest-model.jar to " + gentestLibs);
		FileUtils.copyFileToFolder(jarDestFile , savJunitRunnerLibs, true);
		System.out.println("Deploy gentest-model.jar to " + savJunitRunnerLibs);
	}
}
