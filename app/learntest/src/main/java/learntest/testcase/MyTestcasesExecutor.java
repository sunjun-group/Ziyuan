package learntest.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.event.BreakpointEvent;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.variable.DebugValueExtractor;
import icsetlv.variable.JunitDebugger;
import learntest.data.BreakPointDataBuilder;
import learntest.data.BreakpointData;
import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;

public class MyTestcasesExecutor extends JunitDebugger {

	private static Logger log = LoggerFactory.getLogger(MyTestcasesExecutor.class);	
	private List<BreakpointData> result;
	/* for internal purpose */
	private Map<Integer, List<BreakpointValue>> bkpValsByTestIdx;
	private List<BreakpointValue> currentTestBkpValues;
	private DebugValueExtractor valueExtractor;
	private int valRetrieveLevel;
	private BreakPointDataBuilder builder;
	private StopTimer timer = new StopTimer("TestcasesExecutor");
	private long timeout = DEFAULT_TIMEOUT;
	
	public MyTestcasesExecutor(int valRetrieveLevel, BreakPointDataBuilder builder) {
		this.valRetrieveLevel = valRetrieveLevel;
		this.builder = builder;
	}
	
	public MyTestcasesExecutor(DebugValueExtractor valueExtractor, BreakPointDataBuilder builder) {
		setValueExtractor(valueExtractor);
		this.builder = builder;
	}
	
	@Override
	protected void onStart() {
		bkpValsByTestIdx = new HashMap<Integer, List<BreakpointValue>>();
		currentTestBkpValues = new ArrayList<BreakpointValue>();
		timer.start();
	}

	@Override
	protected void onEnterTestcase(int testIdx) {
		timer.newPoint(String.valueOf(testIdx));
		currentTestBkpValues = CollectionUtils.getListInitIfEmpty(bkpValsByTestIdx, testIdx);
	}

	@Override
	protected void onEnterBreakpoint(BreakPoint bkp, BreakpointEvent bkpEvent) throws SavException {
		BreakpointValue bkpVal = extractValuesAtLocation(bkp, bkpEvent);
		addToCurrentValueList(currentTestBkpValues, bkpVal);
	}

	@Override
	protected void onFinish(JunitResult jResult) {
		timer.stop();
		if (jResult.getTestResults().isEmpty()) {
			log.warn("TestResults is empty!");
			log.debug(getProccessError());
		}
		Map<String, BreakpointData> bkpDataMap = new HashMap<String, BreakpointData>();
		int size = bkpValsByTestIdx.size();
		for (int i = 0; i < size; i++) {
			List<BreakpointValue> bkpValueOfTcI = bkpValsByTestIdx.get(i);
			Assert.assertNotNull(bkpValueOfTcI, "Missing breakpoint value for test " + i);
			getBuilder().build(bkpDataMap, bkpValueOfTcI);
		}
		result = buildBreakpointData(bkpDataMap);
	}
	
	private List<BreakpointData> buildBreakpointData(Map<String, BreakpointData> bkpDataMap) {
		List<BreakpointData> result = new ArrayList<BreakpointData>();
		for (BreakPoint bkp : bkps) {
			BreakpointData bkpData = bkpDataMap.get(bkp.getId());
			if (bkpData != null) {
				bkpData.setBkp(bkp);
			} else {
				bkpData = new BreakpointData(bkp);
			}			
			result.add(bkpData);
		}
		return result;
	}

	private BreakpointValue extractValuesAtLocation(BreakPoint bkp,
			BreakpointEvent bkpEvent) throws SavException {
		try {
			return getValueExtractor().extractValue(bkp, bkpEvent);
		} catch (IncompatibleThreadStateException e) {
			log.error(e.getMessage());
		} catch (AbsentInformationException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
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
		return CollectionUtils.initIfEmpty(result);
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
	
	public int getValRetrieveLevel() {
		return valRetrieveLevel;
	}
	
	public void setValRetrieveLevel(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
		if (valueExtractor != null) {
			valueExtractor.setValRetrieveLevel(valRetrieveLevel);
		}
	}
	
	public BreakPointDataBuilder getBuilder() {
		return builder;
	}
	
	@Override
	protected long getTimeoutInSec() {
		return timeout;
	}
	
	public StopTimer getTimer() {
		return timer;
	}
	
	public void setTimeout(long timeout, TimeUnit timeUnit) {
		long timeoutInSec = timeUnit.toSeconds(timeout);
		log.debug("Testcase execution timeout = " + timeoutInSec + "s");
		this.timeout = timeoutInSec;
	}

	public static enum BranchResultType {
		TRUE,
		FALSE,
		UNKNOWN;
	}

}
