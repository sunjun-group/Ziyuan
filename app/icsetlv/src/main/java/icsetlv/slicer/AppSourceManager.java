/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.slicer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sav.common.core.utils.BreakpointUtils;
import sav.strategies.dto.BreakPoint;

import com.ibm.wala.util.collections.Pair;

/**
 * @author LLT
 *
 */
public class AppSourceManager {
	private String srcPath;
	
	public AppSourceManager(String sourcePath, List<BreakPoint> breakpoints) {
		this.srcPath = sourcePath;
		Map<String, List<BreakPoint>> brkpsMap = BreakpointUtils.initBrkpsMap(breakpoints);
		for (String clazz : brkpsMap.keySet()) {
			
		}
	}

	public List<Pair<File, String>> getSourceFiles() {
		List<Pair<File, String>> result = new ArrayList<Pair<File,String>>();
		try {
			for (Object obj : FileUtils.listFiles(new File(srcPath), new String[]{"java"}, true)) {
				File file = (File) obj;
				String fileName = file.getAbsolutePath().replace(srcPath + "\\", "").replace("\\", "/");
				if (file.getName().equals("FindMaxCallerTest.java")) {
					result.add(Pair.make(replacedFile(file), 
								fileName));
				} else {
					result.add(Pair.make(file, fileName));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private File replacedFile(File file) throws IOException {
		int i = 1;
		List<String> newContent = new ArrayList<String>();
		for (Object lineObj : FileUtils.readLines(file)) {
			String line = (String) lineObj;
			if (i == 28) {
				newContent.add("Assert.assertEquals(max, 90);");
			}
			
			newContent.add(line);
			i++;
		}
		File newFile = File.createTempFile("FindMaxCallerTest", "java");
		FileUtils.writeLines(newFile, newContent);
		return newFile;
	}	
}
