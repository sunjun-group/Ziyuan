/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.Logger;
import sav.common.core.SavException;
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
	
	private List<BreakpointData> result;
	/* for internal purpose */
	private Map<Integer, List<BreakpointValue>> bkpValsByTestIdx;
	private List<BreakpointValue> currentTestBkpValues;
	private DebugValueExtractor valueExtractor;
	private int valRetrieveLevel;
	private ITestResultVerifier verifier = DefaultTestResultVerifier.getInstance();
	private JunitResult jResult;
	
	public TestcasesExecutor(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
	}
	
	public TestcasesExecutor(DebugValueExtractor valueExtractor) {
		setValueExtractor(valueExtractor);
	}
	
	public void setup(VMConfiguration config, List<String> allTests) {
		super.setup(config, allTests);
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
	protected void onEnterBreakpoint(BreakPoint bkp, BreakpointEvent bkpEvent) throws SavException {
		BreakpointValue bkpVal = extractValuesAtLocation(bkp, bkpEvent);
		addToCurrentValueList(currentTestBkpValues, bkpVal);
	}

	@Override
	protected void onFinish(JunitResult jResult) {
		Map<TestResult, List<BreakpointValue>> resultMap = new HashMap<TestResult, List<BreakpointValue>>();
		Map<String, TestResult> tcExResult = getTcExResult(jResult);
		for (int i = 0; i < allTests.size(); i++) {
			TestResult testResult = tcExResult.get(allTests.get(i));
			if (testResult != TestResult.UNKNOWN) {
				CollectionUtils.getListInitIfEmpty(resultMap, testResult)
						.addAll(bkpValsByTestIdx.get(i));
			}
		}
		result = buildBreakpointData(CollectionUtils.nullToEmpty(resultMap.get(TestResult.PASS)), 
				CollectionUtils.nullToEmpty(resultMap.get(TestResult.FAIL)));
		this.jResult = jResult; 
	}

	private Map<String, TestResult> getTcExResult(JunitResult jResult) {
		Map<String, TestResult> testResults = new HashMap<String, TestcasesExecutor.TestResult>();
		for (String test : allTests) {
			TestResult testResult = verifier.verify(jResult, test);
			testResults.put(test, testResult);
		}
		return testResults;
	}
	
	private List<BreakpointData> buildBreakpointData(
			List<BreakpointValue> passValues, List<BreakpointValue> failValues) {
		List<BreakpointData> result = new ArrayList<BreakpointData>(bkps.size());
		for (BreakPoint bkp : bkps) {
			BreakpointData bkpData = new BreakpointData();
			bkpData.setBkp(bkp);
			bkpData.setPassValues(getValuesOfBkp(bkp.getId(), passValues));
			bkpData.setFailValues(getValuesOfBkp(bkp.getId(), failValues));
			result.add(bkpData);
		}
		return result;
	}
	
	private List<BreakpointValue> getValuesOfBkp(String bkpId,
			List<BreakpointValue> allValues) {
		List<BreakpointValue> result = new ArrayList<BreakpointValue>();
		for (BreakpointValue val : allValues) {
			if (val.getBkpId().equals(bkpId)) {
				result.add(val);
			}
		}
		return result;
	}

	private BreakpointValue extractValuesAtLocation(BreakPoint bkp,
			BreakpointEvent bkpEvent) throws SavException {
		try {
			return getValueExtractor().extractValue(bkp, bkpEvent);
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

	public List<BreakpointData> getResult() {
		return result;
	}
	
	public JunitResult getjResult() {
		return jResult;
	}
	
	private DebugValueExtractor getValueExtractor() {
		if (valueExtractor == null) {
			setValueExtractor(new DebugValueExtractor(valRetrieveLevel));
		}
		return valueExtractor;
	}

	public void setValueExtractor(DebugValueExtractor valueExtractor) {
		this.valueExtractor = valueExtractor;
		if (valueExtractor != null) {
			this.valRetrieveLevel = valueExtractor.getValRetrieveLevel();
		}
	}
	
	public void setValRetrieveLevel(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
		if (valueExtractor != null) {
			valueExtractor.setValRetrieveLevel(valRetrieveLevel);
		}
	}
	
	public int getValRetrieveLevel() {
		return valRetrieveLevel;
	}
	
	public void setTestResultVerifier(ITestResultVerifier verifier) {
		this.verifier = verifier;
	}
	
	public static enum TestResult {
		PASS,
		FAIL,
		UNKNOWN;
		
		public static TestResult of(boolean isPass) {
			if (isPass) {
				return PASS;
			}
			return FAIL;
		}
	}
}

