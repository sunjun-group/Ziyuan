package config;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class PathConfiguration implements IJDartSettings {
	private static PathConfiguration instance;
	public static String jdartRoot = "E:\\git\\Ziyuan\\jdart";
	public static String savRoot = "E:\\git\\Ziyuan\\app\\sav.commons";
	
	public String getRuntimeCP() {
		StringBuffer sb = new StringBuffer();
		sb.append(jdartRoot+"\\bin;");
		sb.append(getJarPaths(jdartRoot+"\\libs"));
		sb.append(getJarPaths(savRoot+"\\lib"));
		sb.append(savRoot+"\\target\\classes;");
		sb.append(savRoot+"\\target\\test-classes;");
		return sb.toString();
	}
	
	public String getJarPaths(String path){
		StringBuffer sb = new StringBuffer();
		File root = new File(path);
		if (root.isDirectory()) {
			Queue<File> queue = new LinkedList<>();
			queue.add(root);
			while (!queue.isEmpty()) {
				File directory= queue.poll();
				for (File file : directory.listFiles()) {
					if (file.isDirectory()) {
						queue.add(file);
					}else {
						String jar = file.getAbsolutePath();
						if (jar.endsWith(".jar")) {
							sb.append(jar+";");
						}
					}
				}
			}
		}
		return sb.toString();
	}
	
	public static PathConfiguration getInstance() {
		if (instance == null) {
			instance = new PathConfiguration();
		}
		return instance;
	}

	@Override
	public String getProcessDirectory() {
		return jdartRoot;
	}
}
