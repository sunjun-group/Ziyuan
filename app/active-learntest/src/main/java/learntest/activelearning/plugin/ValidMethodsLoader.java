package learntest.activelearning.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import learntest.plugin.LearnTestConfig;
import learntest.plugin.ProjectSetting;
import sav.common.core.Constants;
import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.FileUtils;
import sav.strategies.dto.ClassLocation;

public class ValidMethodsLoader {
	
	public List<LearnTestConfig> loadValidMethodInfos(String projectName) throws CoreException {
		String learntestOutput = ProjectSetting.getLearntestOutputFolder(projectName);
		String validMethodFile = FileUtils.getFilePath(learntestOutput, "allValidMethods.txt");
		List<String> validMethods = loadValidMethods(validMethodFile);
		List<ClassLocation> methodLocs = toMethodLocations(validMethods);
		List<LearnTestConfig> learntestConfigs = new ArrayList<>(methodLocs.size());
		for (ClassLocation method : methodLocs) {
			LearnTestConfig config = new LearnTestConfig();
			config.setProjectName(projectName);
			config.setTargetClassName(method.getClassCanonicalName());
			config.setTargetMethodName(method.getMethodName());
			config.setTargetMethodLineNum(String.valueOf(method.getLineNo()));
			learntestConfigs.add(config);
		}
		return learntestConfigs;
	}

	public List<String> loadValidMethods(String validMethodFile) {
		try {
			List<?> objs = org.apache.commons.io.FileUtils.readLines(new File(validMethodFile));
			List<String> lines = new ArrayList<String>(objs.size());
			for (Object obj : objs) {
				String line = (String) obj;
				if (!line.startsWith("#")) {
					lines.add(line);
				}
			}
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<ClassLocation> toMethodLocations(List<String> methods) {
		List<ClassLocation> locs = new ArrayList<>(methods.size());
		for (String name : methods) {
			try {
				int idx = name.lastIndexOf(Constants.DOT);
				if (idx > 0) {
					String classMethod = name.substring(0, idx);
					int line = Integer.valueOf(name.substring(idx + 1));
					Pair<String, String> pair = ClassUtils.splitClassMethod(classMethod);
					locs.add(new ClassLocation(pair.a, pair.b, line));
				} else {
					throw new IllegalArgumentException();
				}
			} catch(Exception e) {
				System.out.println("Error format: " + name);
			}
		}
		return locs;
	}
}
