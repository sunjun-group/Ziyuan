/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ObjectUtils;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.DebugLine;

/**
 * @author LLT
 *
 */
public class DebugLinePreProcessor {
	
	public List<DebugLine> preProcess(List<DebugLine> debugLines) {
		List<DebugLine> result = new ArrayList<DebugLine>(debugLines.size());
		Map<String, List<DebugLine>> bkpMap = BreakpointUtils.initBrkpsMap(debugLines);
		for (String className : bkpMap.keySet()) {
			List<DebugLine> allLines = bkpMap.get(className);
			sortIncr(allLines);
			allLines = joinDuplicatedLines(allLines);
			result.addAll(allLines);
		}
		return result;
	}

	/**
	 * sort debugLines of the same class
	 */
	private void sortIncr(List<DebugLine> allLines) {
		Collections.sort(allLines, new Comparator<DebugLine>() {

			@Override
			public int compare(DebugLine o1, DebugLine o2) {
				return ObjectUtils.compare(o1.getLineNo(), o2.getLineNo());
			}
		});
	}

	/**
	 * debugLines are in the same class.
	 * 2 debug lines are considered to be duplicate if
	 * they point to the same line. 
	 * 
	 */
	private List<DebugLine> joinDuplicatedLines(List<DebugLine> debugLines) {
		List<DebugLine> result = new ArrayList<DebugLine>(debugLines.size());
		int i = 0;
		while (i < debugLines.size()) {
			DebugLine lineI = debugLines.get(i);
			int j = i + 1;
			DebugLine curLine = lineI;
			while (j < debugLines.size()) {
				DebugLine nextLine = debugLines.get(j);
				if (isDuplicate(curLine, nextLine)) {
					/* 
					 * if first duplicate found, clone the debugline in order to 
					 * modify orgLines and variables without make the original debugline obj dirty
					 *  */
					if (curLine == lineI) {
						lineI = lineI.clone();
					}
					lineI.setVars(getNewVarsOfDuplicateLines(lineI, nextLine));
					lineI.addOrgLineNos(nextLine.getOrgLineNos());
					curLine = nextLine;
					j++;
				} else {
					break;
				}
			}
			result.add(lineI);
			i = j;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Variable> getNewVarsOfDuplicateLines(DebugLine curLine, DebugLine nextLine) {
		return (List<Variable>) org.apache.commons.collections.CollectionUtils
				.union(curLine.getVars(), nextLine.getVars());
	}

	private boolean isDuplicate(DebugLine curLine, DebugLine nextLine) {
		return nextLine.getLineNo() == curLine.getLineNo();
	}
	
	/**
	 * get supperset of 2 list
	 * return null if no list is the subset of the other
	 */
	public static <T> List<T> getSupperList(List<T> list1, List<T> list2) {
		List<T> biggerList = list1;
		List<T> smallerList = list2;
		if (list1.size() < list2.size()) {
			biggerList = list2;
			smallerList = list1;
		}
		if (org.apache.commons.collections.CollectionUtils.isSubCollection(
				smallerList, biggerList)) {
			return biggerList;
		}
		return null;
	}
}
