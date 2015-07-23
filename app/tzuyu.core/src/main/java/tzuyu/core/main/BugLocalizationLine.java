/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import org.apache.commons.lang.StringUtils;

import icsetlv.Engine.Result;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.DebugLine;

/**
 * @author khanh
 *
 */
public class BugLocalizationLine {

	private DebugLine breakpoint;
	private double suspiciousness;
	private Result learningResult;
	
	public BugLocalizationLine(DebugLine breakPoint, double suspiciousness, Result learningResult){
		this.breakpoint = breakPoint;
		this.suspiciousness = suspiciousness;
		this.learningResult = learningResult;
	}
	
	public BreakPoint getBreakpoint() {
		return breakpoint;
	}
	public double getSuspiciousness() {
		return suspiciousness;
	}
	public Result getLearningResult() {
		return learningResult;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		str.append(breakpoint.getClassCanonicalName()).append(":")
				.append(breakpoint.getOrgLineNo()).append("\n");
		str.append("suspiciousness: " + String.format("%.2f", suspiciousness) + "\n");
		if (StringUtils.isBlank(learningResult.getLearnedLogic())) {
			str.append("Could not learn anything.");
		} else {
			str.append("Logic: ").append(learningResult.getLearnedLogic()).append("\n");
			str.append("Accuracy: ").append(learningResult.getAccuracy()).append("\n");
		}
		
		return str.toString();
	}
}
