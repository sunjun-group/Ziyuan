/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.printout;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.utils.ClassUtils;
import icsetlv.common.utils.ExecutionResultFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sav.common.core.Pair;

/**
 * @author LLT
 * 
 */
public class PrintOutStmtWriter {

	public static void writePrintoutStmts(String filePath, String sourcePath)
			throws IOException {
		List<Pair<BreakPoint, List<String>>> brkPnts = ExecutionResultFileUtils
				.read(filePath);
		for (Pair<BreakPoint, List<String>> brkp : brkPnts) {
			File jFile = new File(ClassUtils.getJFilePath(sourcePath,
					brkp.a.getClassCanonicalName()));
			int i = 1;
			List<Object> newContent = new ArrayList<Object>();
			for (Object line : FileUtils.readLines(jFile)) {
				if (i++ == brkp.a.getLineNo()) {
					String indent = getIndent((String) line);
					for (String varVal : brkp.b) {
						newContent.add(indent + "System.out.println(\""
								+ varVal + "\");");
					}
				}
				newContent.add(line);
			}
			FileUtils.writeLines(jFile, newContent);
		}
	}

	private static String getIndent(String stmt) {
		StringBuilder indent = new StringBuilder();
		for (int i = 0; i < stmt.length(); i++) {
			if ('\t' == stmt.charAt(i)) {
				indent.append("\t");
			} else {
				break;
			}
		}
		return indent.toString();
	}
}
