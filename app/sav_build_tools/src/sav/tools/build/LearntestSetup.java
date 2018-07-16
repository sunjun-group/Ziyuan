package sav.tools.build;

import sav.common.core.utils.FileUtils;
import static sav.tools.build.ProjectConfiguration.*;

public class LearntestSetup {

	
	public static void main(String[] args) {
		setupZ3prover();
	}

	private static void setupZ3prover() {
		if (OSValidator.isWindows()) {
			FileUtils.copyFileToFolder(getProjectBaseDir() + "/etc/libs/z3prover/windows/libz3.dll",
					"C:/Windows/System32", true);
			FileUtils.copyFileToFolder(getProjectBaseDir() + "/etc/libs/z3prover/windows/libz3java.dll",
					"C:/Windows/System32", true);
		} else if (OSValidator.isMac()) {
			
		} else if (OSValidator.isUnix()) {
			
		}
	}
	
	
	
}
