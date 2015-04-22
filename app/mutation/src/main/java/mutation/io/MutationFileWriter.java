/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation.io;

import japa.parser.ast.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mutation.mutator.insertdebugline.DebugLineData;
import mutation.utils.FileUtils;
import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class MutationFileWriter implements IMutationWriter {
	private static final int JAVA_PARSER_TAB_SIZE = 8;
	private String muSrcFolder;
	private String scrFolder;

	public MutationFileWriter(String srcFolder) {
		this.scrFolder = srcFolder;
		muSrcFolder = FileUtils.createTempFolder("mutatedSource")
				.getAbsolutePath();
	}

	public File write(List<DebugLineData> data, String className) {
		File javaFile = new File(ClassUtils.getJFilePath(scrFolder, className));
		File muFile = new File(muSrcFolder, javaFile.getName());
		try {
			List<?> lines = org.apache.commons.io.FileUtils.readLines(javaFile);
			List<String> newContent = new ArrayList<String>();
			int preIdx = 1;
			for (DebugLineData debugLine : data) {
				switch (debugLine.getInsertType()) {
				case ADD:
					Node insertNode = debugLine.getInsertNode();
					copy(lines, newContent, preIdx, insertNode.getBeginLine());
					newContent.add(insertNode.toString());
					break;
				case REPLACE:
					/* we might have some text before and after the node, just keep them all
					 * in new separate line
					 * */
					String line = (String) lines.get(debugLine.getLocation()
							.getLineNo());
					Node orgNode = debugLine
							.getOrgNode();
					String beforeNode = subString(line, 1, orgNode.getBeginColumn());
					addIfNotEmpty(newContent, beforeNode);
					/* add new node */
					for (Node newNode : debugLine.getReplaceNodes()) {
						String[] stmt = newNode.toString().split("\n");
						CollectionUtils.addAll(newContent, stmt);
					}
					/* remain content at the same line but right after the org node */
					String afterNode = subString(
							(String) lines.get(orgNode.getEndLine()),
							orgNode.getEndColumn());
					addIfNotEmpty(newContent, afterNode);
					break;
				}
				debugLine.setDebugLine(newContent.size());
			}
			org.apache.commons.io.FileUtils.writeLines(muFile, newContent);
		} catch (IOException e) {
			throw new SavRtException(e);
		}
		return muFile;
	}
	
	private void addIfNotEmpty(List<String> lines, String newLine) {
		if (!newLine.isEmpty()) {
			lines.add(newLine);
		}
	}

	protected String subString(String line, int javaParserStartCol,
			int javaParserEndCol) {
		char[] chars = line.toCharArray();
		int start = getMappedColIdx(chars, javaParserStartCol, 0, 1);
		int end = getMappedColIdx(chars, javaParserEndCol, start, javaParserStartCol);
		return line.substring(start, end);
	}
	
	protected String subString(String line, int javaParserStartCol) {
		char[] chars = line.toCharArray();
		int start = getMappedColIdx(chars, javaParserStartCol, 0, 1);
		return line.substring(start);
	}

	protected int getMappedColIdx(char[] chars, int javaParserCol, int startIdx, int startPos) {
		int pos = startPos;
		for (int i = startIdx; i < chars.length; i++) {
			char ch = chars[i];
			if (pos == javaParserCol) {
				return i;
			}
			if (ch == '\t') {
				pos += JAVA_PARSER_TAB_SIZE;
			} else {
				pos++;
			}
		}
		throw new SavRtException(
				StringUtils.spaceJoin("cannot map column index between inputStream and javaparser, line = ",
						String.valueOf(chars), ", column=", javaParserCol));
	}

	/**
	 * copy content, exclude line at endIdx
	 * */
	private void copy(List<?> from, List<String> to, int startIdx, int endIdx) {
		for (int i = startIdx; i < endIdx; i++) {
			to.add((String) from.get(i));
		}
	}

}
