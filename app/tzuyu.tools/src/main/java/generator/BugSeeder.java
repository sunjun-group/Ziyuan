/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import sav.common.core.Constants;
import sav.commons.utils.TestConfigUtils;

/**
 * @author LLT
 *
 */
public class BugSeeder {
	private static final String TEST_SCR_FOLDER = TestConfigUtils.getTrunkPath()
			+ "/app/sav.commons" + "/src/test/java"; 
	private static final String ORG_TEST_CLASS_SUFIX = "Org";
	private SeedParser seedParser = new SeedParser();
	private ResourceBundle props;
	private String orgClassFilePath;
	private String overrideClassFilePath;
	private String classSimpleName;
	
	public BugSeeder(Class<?> modifiedClass, String seedResource) {
		props = ToolsUtils.getResourceBundle(seedResource);
		String classFile = TEST_SCR_FOLDER + Constants.FILE_SEPARATOR
						+ modifiedClass.getName().replace(".", Constants.FILE_SEPARATOR);
		orgClassFilePath = classFile + ORG_TEST_CLASS_SUFIX + Constants.JAVA_EXT;
		overrideClassFilePath = classFile + Constants.JAVA_EXT;
		classSimpleName = modifiedClass.getSimpleName();
	}
	
	public void seedBug(String[] args) throws IOException {
		if (args == null) {
			throw new IllegalArgumentException(
					"args cannot be null, but bug name (check *Seeds.properties)");
		}
		
		List<Seed> seededLines = new ArrayList<Seed>();
		for (String arg : args) {
			if (!sav.common.core.utils.StringUtils.isEmpty(arg)) {
				seededLines.addAll(parseSeed(props.getString(arg)));
			}
		}
		List<String> orgLines = readFileContent(orgClassFilePath);
		List<String> modifiedContent = getModifiedContent(orgLines, seededLines);
		FileUtils.writeLines(new File(overrideClassFilePath), modifiedContent);
	}

	private List<Seed> parseSeed(String seedStr) {
		return seedParser.parse(seedStr);
	}

	private List<String> getModifiedContent(List<String> orgLines,
			List<Seed> seeds) {
		List<String> result = new ArrayList<String>(orgLines);
		/**
		 * modify class name
		 */
		for (int i = 0; i < result.size(); i++) {
			String line = result.get(i);
			if (line.contains("class") && line.contains(classSimpleName)) {
				result.set(i, StringUtils.replace(line, classSimpleName + ORG_TEST_CLASS_SUFIX, 
						classSimpleName));
				break;
			}
		}
		for (Seed seed : seeds) {
			seed.apply(result);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<String> readFileContent(String filePath) throws IOException {
		return FileUtils.readLines(new File(filePath));
	}
	
	public static class SeedParser {
		private static final String SEPARATOR_REGEX = "::";
		private static final String SEED_SEPARATOR_REGEX = ":::";
		
		public List<Seed> parse(String seedsStr) {
			String[] seeds = seedsStr.split(SEED_SEPARATOR_REGEX);
			List<Seed> result = new ArrayList<Seed>();
			for (String seedStr : seeds) {
				if (seedStr.isEmpty()) {
					continue;
				}
				String[] seedTks = seedStr.split(SEPARATOR_REGEX);
				Seed seed = Seed.fromType(seedTks[0]);
				for (int i = 1; i < seedTks.length; i++) {
					seed.append(seedTks[i]);
				}
				result.add(seed);
			}
			return result;
		}

	}
	
}
