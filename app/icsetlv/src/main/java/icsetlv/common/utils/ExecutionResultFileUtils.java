/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import icsetlv.common.Pair;
import icsetlv.common.dto.BreakPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class ExecutionResultFileUtils {
	private static final String BREAKPOINT_TOKEN = "BREAKPOINT";
	private static final String CLASS_TOKEN = "CLASS";
	private static final String LINE_NO_TOKEN = "LINE_NO";
	private static final String VAR_VALS_TOKEN = "VAR_VALUES";
	private static final String ATT_SEPARATORR = ",\t";
	private static final String VAR_VAL_JOINER = "=";

	private static int BREAKPOINT_STATE = 1;
	private static int VAR_VALUE_STATE = 2;
	
//	public static void writeToFile(VariablesExtractorResult extractedResult,
//			String filePath) throws FileNotFoundException {
//		List<Pair<BreakPoint, String>> brpValPairs = new ArrayList<Pair<BreakPoint,String>>();
//		for (BreakpointResult bkp : extractedResult.getResult()) {
//			brpValPairs.add(Pair.of(bkp.getBreakpoint(), 
//					new StringBuilder()
//						.append("pass: \n")
//						.append(bkp.getPassValues())
//						.append("\nfail: \n")
//						.append(bkp.getFailValues())
//						.toString()));
//		}
//		writeToFile(brpValPairs, filePath);
//	}

	public static void writeToFile(List<Pair<BreakPoint, String>> brpValPairs,
			String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		PrintStream out = new PrintStream(file);
		for (Pair<BreakPoint, String> brpVal : brpValPairs) {
			out.println(BREAKPOINT_TOKEN);
			out.print(varVal(CLASS_TOKEN, brpVal.a.getClassCanonicalName()));
			out.print(ATT_SEPARATORR);
			out.println(varVal(LINE_NO_TOKEN, brpVal.a.getLineNo()));
			out.println(VAR_VALS_TOKEN);
			out.println(brpVal.b);
		}
		out.flush();
		out.close();
	}

	private static String varVal(String var, Object val) {
		return var + VAR_VAL_JOINER + val;
	}

	public static List<Pair<BreakPoint, List<String>>> read(String filePath)
			throws IOException {
		List<Pair<BreakPoint, List<String>>> brpValPairs = new ArrayList<Pair<BreakPoint, List<String>>>();
		File exReFile = new File(filePath);
		BreakPoint brp = null;
		List<String> val = null;
		/*
		 * state: 1-breakpoint, 2-varValues
		 */
		int state = 0;
		for (Object line : FileUtils.readLines(exReFile)) {
			if (BREAKPOINT_TOKEN.equals(line)) {
				if (brp != null) {
					brpValPairs.add(Pair.of(brp, val));
				}
				state = BREAKPOINT_STATE;
				continue;
			}
			if (VAR_VALS_TOKEN.equals(line)) {
				val = new ArrayList<String>();
				state = VAR_VALUE_STATE;
				continue;
			}
			String str = (String) line;
			if (state == BREAKPOINT_STATE) {
				String[] atts = str.split(ATT_SEPARATORR);
				brp = new BreakPoint(
						atts[0].split(VAR_VAL_JOINER)[BREAKPOINT_STATE],
						Integer.parseInt(atts[BREAKPOINT_STATE]
								.split(VAR_VAL_JOINER)[BREAKPOINT_STATE]));
			} else if (state == VAR_VALUE_STATE && !StringUtils.isEmpty(str)) {
				val.add(str);
			}
		}
		if (brp != null) {
			brpValPairs.add(Pair.of(brp, val));
		}
		return brpValPairs;
	}
}
