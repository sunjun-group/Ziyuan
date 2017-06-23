/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.breakpoint.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class DecisionBkpsData {
	private List<BreakPoint> breakPoints;
	private Map<DecisionLocation, List<BreakPoint>> bkpListMap;
	private Map<DecisionLocation, BreakPoint> decisionMap;
	private Map<DecisionLocation, DecisionLocation> parentMap;
	private List<DecisionLocation> decisionLocations;
	//to change false branch logic
	private Map<DecisionLocation, BreakPoint> selfBkps;
	
	public DecisionBkpsData() {
		bkpListMap = new HashMap<DecisionLocation, List<BreakPoint>>();
		breakPoints = new ArrayList<BreakPoint>();
	}
	
	public DecisionBkpsData(List<DecisionLocation> locations, Set<BreakPoint> allBkps,
			Map<DecisionLocation, BreakPoint> decisionMap, Map<DecisionLocation, DecisionLocation> parentMap) {
		this();
		addBreakpoints(allBkps);
		this.decisionMap = decisionMap;
		this.parentMap = parentMap;
		this.decisionLocations = locations;
	}
	
	public void addBreakpoints(Collection<BreakPoint> newBkps) {
		getBreakPoints().addAll(newBkps);
	}
	
	public void addBreakpoint(BreakPoint newBkp) {
		getBreakPoints().add(newBkp);
	}

	public Map<DecisionLocation, List<BreakPoint>> getDecisionBkpsMap() {
		return bkpListMap;
	}

	public void add(DecisionLocation location, Set<BreakPoint> bkps) {
		bkpListMap.put(location, new ArrayList<BreakPoint>(bkps));		
	}
	
	public List<BreakPoint> getBreakpoints(DecisionLocation location) {
		return bkpListMap.get(location);
	}
	
	public BreakPoint getSelfBreakPoint(DecisionLocation location) {
		return selfBkps.get(location);
	}
	
	public List<BreakPoint> getBreakPoints() {
		return breakPoints;
	}
	
	public BreakPoint getTrueBreakPoint(DecisionLocation location) {
		return decisionMap.get(location);
	}
	
	public BreakPoint getParentBreakPoint(DecisionLocation location) {
		return decisionMap.get(parentMap.get(location));
	}

	public List<DecisionLocation> getLocations() {
		return decisionLocations;
	}
	
	public void setSelfBkps(Map<DecisionLocation, BreakPoint> selfBkps) {
		this.selfBkps = selfBkps;
	}
	
	public Map<DecisionLocation, BreakPoint> getSelfBkps() {
		return selfBkps;
	}

	
}
