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

import org.apache.commons.io.FileUtils;

/**
 * @author LLT
 *
 */
public abstract class ClassAppender {
	protected static String BASE = "D:/_1_Projects/Tzuyu/";
	protected static String TRUNK = BASE + "workspace/trunk/";
	protected static final String START_GENERATED_PART_TOKEN = "//	Generated part";
	protected static final String END_GENERATED_PART_TOKEN = "//	End generated part";
	
	protected void appendJavaFile() throws IOException {
		System.out.println("Start generating...");
		String generatedContent = getGeneratedContent();
		String classPath = getClassPath();
		File file = new File(classPath);
		StringBuilder content = new StringBuilder();
		boolean inGeneratedZone = false;
		for (Object ln : FileUtils.readLines(file)) {
			String line = (String) ln;
			if (!inGeneratedZone || line.contains(END_GENERATED_PART_TOKEN)) {
				content.append(line).append("\n");
			}
			if (line.contains(START_GENERATED_PART_TOKEN)) {
				inGeneratedZone = true;
				content.append(generatedContent);
			} else if (line.contains(END_GENERATED_PART_TOKEN)) {
				inGeneratedZone = false;
			} 
		}
		// update class
		FileUtils.writeStringToFile(file, content.toString());
		System.err.println("Success!!");
		System.err.println(String.format("\nWarning: Refresh %s!", classPath));
	}

	protected abstract String getGeneratedContent();

	protected abstract String getClassPath();
}
