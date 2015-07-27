/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import icsetlv.common.dto.BkpInvariantResult;
import sav.common.core.formula.Formula;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.DebugLine;

/**
 * @author khanh
 *
 */
public class BugLocalizationLine {

	private DebugLine breakpoint;
	private double suspiciousness;
	private BkpInvariantResult learningResult;
	
	public BugLocalizationLine(DebugLine breakPoint, double suspiciousness, BkpInvariantResult invariant){
		this.breakpoint = breakPoint;
		this.suspiciousness = suspiciousness;
		this.learningResult = invariant;
	}

	public BreakPoint getBreakpoint() {
		return breakpoint;
	}

	public double getSuspiciousness() {
		return suspiciousness;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		str.append(breakpoint.getClassCanonicalName()).append(":")
				.append(breakpoint.getOrgLineNo())
				.append(" (debugLine: ")
				.append(breakpoint.getLineNo()).append(")\n");
		str.append("suspiciousness: " + String.format("%.2f", suspiciousness) + "\n");
		Formula learnedLogic = learningResult.getLearnedLogic();
		if (learnedLogic == null) {
			str.append("Could not learn anything.");
		} else if (Formula.FALSE.equals(learnedLogic)) {
			str.append("This line is likely a bug!");
		} else {
			str.append("Logic: ").append(learnedLogic).append("\n");
			str.append("Accuracy: ").append(1).append("\n");
		}
		
		return str.toString();
	}
}
