package sav.tools.build;

import java.io.File;

public class ProjectConfiguration {
	private static String JAVA_HOME = null;
	
	public static void main(String[] args) {
		System.out.println(getProjectBaseDir());
	}
	
	public static String getEtcFolder() {
		return getProjectBaseDir() + "/etc";
	}
	
	public static String getModuleFolder(String module) {
		return getProjectBaseDir() + "/app/" + module;
	}
	
	public static String getProjectBaseDir() {
		String path = new File(ProjectConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getAbsolutePath();
		path = path.replace("\\", "/");
		path = path.substring(0, path.indexOf("/app"));
		return path;
	}
	
	public static String getTargetFolder(String prjName) {
		return getModuleFolder(prjName) + "/target";
	}

	
	public static String getJavaHome() {
		if (JAVA_HOME != null) {
			return JAVA_HOME;
		}
		// work around in case java home not point to jdk but jre.
		String javaHome = System.getProperty("java.home");
		if (javaHome.endsWith("jre")) {
			javaHome = javaHome.substring(0,
					javaHome.lastIndexOf(File.separator + "jre"));
		}
		return javaHome;
	}
}
