package learntest.testcase.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.BreakpointBuilder;
import learntest.breakpoint.data.DecisionLocation;
import sav.strategies.dto.BreakPoint;

public class BreakpointDataBuilder {
	
	private BreakpointBuilder bkpBuilder;
	private DecisionLocation target;
	private Map<DecisionLocation, BreakpointData> bkpDataMap;
	private List<BreakpointData> result;
	
	public BreakpointDataBuilder(BreakpointBuilder bkpBuilder) {
		this.bkpBuilder = bkpBuilder;
		bkpDataMap = new HashMap<DecisionLocation, BreakpointData>();
	}
	
	public void setTarget(DecisionLocation target) {
		this.target = target;
		bkpDataMap = new HashMap<DecisionLocation, BreakpointData>();
		result = null;
	}
	
	public void build(List<BreakPoint> path, BreakpointValue inputValue) {
		Map<BreakPoint, List<Integer>> pathMap = buildPathMap(path);
		if(target == null) {
			List<DecisionLocation> locations = bkpBuilder.getLocations();
			for (DecisionLocation location : locations) {
				build(pathMap, inputValue, location);
			}
		} else {
			build(pathMap, inputValue, target);
			target = null;
		}
	}
	
	private void build(Map<BreakPoint, List<Integer>> pathMap, BreakpointValue inputValue, DecisionLocation location) {
		List<Integer> occurs = pathMap.get(bkpBuilder.getBreakPoint(location));
		BreakpointData bkpData = bkpDataMap.get(location);
		if (bkpData == null) {
			if (location.isLoop()) {
				bkpData = new LoopTimesData(location);
			} else {
				bkpData = new BranchSelectionData(location);
			}
			bkpDataMap.put(location, bkpData);
		}
		if (occurs == null) {
			bkpData.addFalseValue(inputValue);
			return;
		}
		if (location.isLoop()) {
			List<Integer> parentOccurs = pathMap.get(bkpBuilder.getParentBreakPoint(location));
			int cnt = 0;
			if (parentOccurs == null) {
				cnt = occurs.size();
			} else {
				cnt = calculateLoopTimes(occurs, parentOccurs);
			}
			if (cnt == 1) {
				((LoopTimesData)bkpData).addOneTimeValue(inputValue);
			} else {
				((LoopTimesData)bkpData).addMoreTimesValue(inputValue);
			}
		} else {
			((BranchSelectionData)bkpData).addTrueValue(inputValue);
		}
	}

	private Map<BreakPoint, List<Integer>> buildPathMap(List<BreakPoint> path){
		Map<BreakPoint, List<Integer>> pathMap = new HashMap<BreakPoint, List<Integer>>();
		int index = 0;
		for (BreakPoint bkp : path) {
			List<Integer> occurs = pathMap.get(bkp);
			if (occurs == null) {
				occurs = new ArrayList<Integer>();
				pathMap.put(bkp, occurs);
			}
			occurs.add(index ++);
		}
		return pathMap;
	}
	
	private int calculateLoopTimes(List<Integer> occurs, List<Integer> parentOccurs) {
		int times = 0;
		int maxIdx = parentOccurs.size() - 1;
		for (int i = 0; i < maxIdx; i++) {
			times = 0;
			int first = parentOccurs.get(i);
			int second = parentOccurs.get(i + 1);
			for (Integer occur : occurs) {
				if (occur > first && occur < second) {
					times ++;
				}
				if (times > 1) {
					return times;
				}
			}
		}
		int last = parentOccurs.get(maxIdx);
		times = 0;
		for (Integer occur : occurs) {
			if (occur > last) {
				times ++;
			}
			if (times > 1) {
				return times;
			}
		}
		return times;
	}
	
	public List<BreakpointData> getResult() {
		if (result == null) {
			result = new ArrayList<BreakpointData>(bkpDataMap.values());
		}
		return result;
	}

}
