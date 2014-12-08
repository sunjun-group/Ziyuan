/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.List;

/**
 * @author LLT
 *
 */
public class FilteredSliceBreakpointCollector extends SliceBreakpointCollector {
	private String pattern;
	
	/*
	 * TODO LLT: consider to use pattern for filtering the slicing result This
	 * class will replace SliceBkpByClassesCollector and
	 * SliceBkpByPackagesCollector
	 */
	public FilteredSliceBreakpointCollector(List<String> patternList) {
//		wildcar
	}
}
