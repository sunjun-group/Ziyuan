/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.TcExecResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.Logger;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;
import sav.strategies.vm.VMConfiguration;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.event.BreakpointEvent;

/**
 * @author LLT
 * 
 */
public class TestcasesExecutor extends JunitDebugger {
	private static final Logger<?> LOGGER = Logger.getDefaultLogger();
	
	private TcExecResult result;
	/* for internal purpose */
	private Map<Integer, List<BreakpointValue>> bkpValsByTestIdx;
	private List<BreakpointValue> currentTestBkpValues;
	private DebugValueExtractor valueExtractor;
	
	public TestcasesExecutor(VMConfiguration config, List<String> allTests, int valRetrieveLevel) {
		super(config, allTests);
		valueExtractor = new DebugValueExtractor(valRetrieveLevel);
	}

	@Override
	protected void onStart() {
		bkpValsByTestIdx = new HashMap<Integer, List<BreakpointValue>>();
		currentTestBkpValues = new ArrayList<BreakpointValue>();
	}

	@Override
	protected void onEnterTestcase(int testIdx) {
		currentTestBkpValues = CollectionUtils.getListInitIfEmpty(bkpValsByTestIdx, testIdx);
	}

	@Override
	protected void onEnterBreakpoint(BreakPoint bkp, BreakpointEvent bkpEvent) {
		BreakpointValue bkpVal = extractValuesAtLocation(bkp, bkpEvent);
		addToCurrentValueList(currentTestBkpValues, bkpVal);
	}

	@Override
	protected void onFinish(JunitResult jResult) {
		Map<Boolean, List<BreakpointValue>> resultMap = new HashMap<Boolean, List<BreakpointValue>>();
		Map<String, Boolean> tcExResult = jResult.getResult(allTests);
		for (int i = 0; i < allTests.size(); i++) {
			CollectionUtils.getListInitIfEmpty(resultMap, tcExResult.get(allTests.get(i)))
					.addAll(bkpValsByTestIdx.get(i));
		}
		result = new TcExecResult(CollectionUtils.nullToEmpty(resultMap.get(true)), 
				CollectionUtils.nullToEmpty(resultMap.get(false)));
	}
	
	private BreakpointValue extractValuesAtLocation(BreakPoint bkp,
			BreakpointEvent bkpEvent) {
		try {
			return valueExtractor.extractValue(bkp, bkpEvent);
		} catch (IncompatibleThreadStateException e) {
			LOGGER.error(e);
		} catch (AbsentInformationException e) {
			LOGGER.error(e);
		}
		return null;
	}
	
	/**
	 * add breakpoint value to the current list, 
	 * we only keep the value of the last one, so replace the current value (if exists) with the new value.
	 */
	private void addToCurrentValueList(
			List<BreakpointValue> currentTestBkpValues, BreakpointValue bkpVal) {
		if (bkpVal == null) {
			return;
		}
		int i = 0;
		for (; i < currentTestBkpValues.size(); i++) {
			BreakpointValue curVal = currentTestBkpValues.get(i);
			if (curVal.getBkpId().equals(bkpVal.getBkpId())) {
				break;
			}
		}
		if (i < currentTestBkpValues.size()) {
			currentTestBkpValues.set(i, bkpVal);
		} else {
			currentTestBkpValues.add(bkpVal);
		}
	}

	public TcExecResult getResult() {
		return result;
	}
}

