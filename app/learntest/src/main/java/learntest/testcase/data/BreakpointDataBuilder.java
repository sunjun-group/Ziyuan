package learntest.testcase.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import icsetlv.common.dto.BreakpointValue;
import learntest.breakpoint.data.BreakpointBuilder;
import learntest.breakpoint.data.DecisionLocation;
import sav.strategies.dto.BreakPoint;

public class BreakpointDataBuilder {
	
	private BreakpointBuilder bkpBuilder;
	private DecisionLocation target;
	private Map<DecisionLocation, BreakpointData> bkpDataMap;
	//private List<BreakpointData> result;
	
	public BreakpointDataBuilder(BreakpointBuilder bkpBuilder) {
		this.bkpBuilder = bkpBuilder;
		bkpDataMap = new HashMap<DecisionLocation, BreakpointData>();
	}
	
	public void setTarget(DecisionLocation target) {
		this.target = target;
		bkpDataMap = new HashMap<DecisionLocation, BreakpointData>();
		//result = null;
	}
	

	public void build(List<BreakPoint> exePathOfTcI, List<BreakpointValue> inputValueOfTcI) {
		if(inputValueOfTcI.size() > 0) {
			build(exePathOfTcI, inputValueOfTcI.get(0));
			return;
		}
//		if(inputValueOfTcI.size() == 1) {
//			build(exePathOfTcI, inputValueOfTcI.get(0));
//			return;
//		}
//		
//		Map<Integer, List<BreakPoint>> paths = new HashMap<Integer, List<BreakPoint>>();
//		Stack<BreakPoint> todo = new Stack<BreakPoint>();
//		Stack<Integer> order = new Stack<Integer>();
//		int idx = 0;
//		for(BreakPoint bkp : exePathOfTcI) {
//			if (bkpBuilder.isEntryNode(bkp)) {
//				order.push(idx ++);
//			}
//			if(bkpBuilder.isReturnNode(bkp.getLineNo())) {
//				Stack<BreakPoint> cur = new Stack<BreakPoint>();
//				cur.push(bkp);
//				if(!bkpBuilder.isEntryNode(bkp)) {
//					while (!todo.isEmpty()) {
//						BreakPoint next = todo.pop();
//						cur.push(next);
//						if(bkpBuilder.isEntryNode(next)) {
//							break;
//						}
//					}
//				}
//				List<BreakPoint> curList = new ArrayList<BreakPoint>();
//				while (!cur.isEmpty()) {
//					curList.add(cur.pop());
//				}
//				paths.put(order.pop(), curList);
//			} else {
//				todo.push(bkp);
//			}
//		}
//		idx = 0;
//		for (BreakpointValue inputValue : inputValueOfTcI) {
//			build(paths.get(idx ++), inputValue);
//		}
//		
//		System.currentTimeMillis();
	}
	
	private void build(List<BreakPoint> path, BreakpointValue inputValue) {
		Map<BreakPoint, List<Integer>> pathMap = buildPathMap(path);
		if(target == null) {
			List<DecisionLocation> locations = bkpBuilder.getLocations();
			for (DecisionLocation location : locations) {
				build(pathMap, inputValue, location);
			}
		} else {
			build(pathMap, inputValue, target);
		}
	}
	
	private void build(Map<BreakPoint, List<Integer>> pathMap, BreakpointValue inputValue, DecisionLocation location) {
		BreakpointData bkpData = bkpDataMap.get(location);
		if (bkpData == null) {
			if (location.isLoop()) {
				bkpData = new LoopTimesData(location);
			} else {
				bkpData = new BranchSelectionData(location);
			}
			bkpDataMap.put(location, bkpData);
		}
		
		//only true branch data
		List<Integer> occurs = pathMap.get(bkpBuilder.getTrueBreakPoint(location));
		if (occurs == null || occurs.isEmpty()) {
			//bkpData.addFalseValue(inputValue);
			List<Integer> self = pathMap.get(bkpBuilder.getSelfBreakPoint(location));
			if (self != null && !self.isEmpty()) {
				bkpData.addFalseValue(inputValue);
			}
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
		int lastTimes = 0;
		for (Integer occur : occurs) {
			if (occur > last) {
				lastTimes ++;
			}
			if (lastTimes > 1) {
				return lastTimes;
			}
		}
		return Math.max(times, lastTimes);
	}
	
	public Map<DecisionLocation, BreakpointData> getResult() {
		/*if (result == null) {
			result = new ArrayList<BreakpointData>(bkpDataMap.values());
		}
		return result;*/
		return bkpDataMap;
	}
	
}
