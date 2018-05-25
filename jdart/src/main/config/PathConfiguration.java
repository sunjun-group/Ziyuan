package config;

import java.io.File;

public class PathConfiguration {
	public static String jdartRoot = "E:\\git\\Ziyuan\\jdart";
	public static String savRoot = "E:\\git\\Ziyuan\\app\\sav.commons";
	
	static {
		if (!new File(jdartRoot).exists()) {
			jdartRoot = getProjectBaseDir() + "/jdart";
			savRoot = getProjectBaseDir() + "/app/sav.commons";
		}
	}
	
	public static String getProjectBaseDir() {
		String path = new File(PathConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getAbsolutePath();
		path = path.replace("\\", "/");
		path = path.substring(0, path.indexOf("/jdart"));
		return path;
	}
}
