/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import faultLocalization.LineCoverageInfo;
import icsetlv.common.dto.BkpInvariantResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.formula.Formula;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.DebugLine;

/**
 * @author LLT
 *
 */
public class LocatedLines {
	private Map<String, Double> suspeciousness;
	private List<BreakPoint> orgLocatedLines;
	private List<BkpInvariantResult> invariants;
	
	public LocatedLines(List<LineCoverageInfo> suspectLocations) {
		setSuspeciousnessResult(suspectLocations);
	}

	public void setSuspeciousnessResult(List<LineCoverageInfo> suspectLocations) {
		suspeciousness = new HashMap<String, Double>();
		orgLocatedLines = new ArrayList<BreakPoint>();
		for (LineCoverageInfo lineInfo : suspectLocations) {
			BreakPoint bkp = BreakpointUtils.toBreakPoint(lineInfo.getLocation());
			orgLocatedLines.add(bkp);
			suspeciousness.put(bkp.getId(), lineInfo.getSuspiciousness());
		}
	}
	
	public void updateInvariantResult(List<BkpInvariantResult> invariants) {
		this.invariants = invariants;
	}

	public String getDisplayResult() {
		List<BugLocalizationLine> bugLines = new ArrayList<BugLocalizationLine>();
		for(BkpInvariantResult invariant: invariants){
			if (!Formula.TRUE.equals(invariant.getLearnedLogic())) {
				BreakPoint debugLine =  invariant.getBreakPoint();
				for (int orgLine : debugLine.getOrgLineNos()) {
					double susp = suspeciousness.get(
								BreakpointUtils.getLocationId(debugLine.getClassCanonicalName(), orgLine));
					BugLocalizationLine bugLine = new BugLocalizationLine(orgLine, debugLine, susp, invariant);
					bugLines.add(bugLine);
				}
			}
		}
		Collections.sort(bugLines, new Comparator<BugLocalizationLine>() {

			@Override
			public int compare(BugLocalizationLine o1,
					BugLocalizationLine o2) {
				return o2.compare(o1);
			}
		});
		return StringUtils.join(bugLines, "\n\n");
	}

	public List<BreakPoint> getLocatedLines() {
		return orgLocatedLines;
	}
}
