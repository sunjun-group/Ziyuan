package learntest.testcase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.event.BreakpointEvent;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.variable.DebugValueExtractor;
import icsetlv.variable.JunitDebugger;
import learntest.testcase.data.BranchSelectionData;
import learntest.testcase.data.BreakpointDataBuilder;
import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;

public class TestcasesExecutorwithoutLoopTimes extends JunitDebugger {

	private static Logger log = LoggerFactory.getLogger(TestcasesExecutorwithoutLoopTimes.class);	
	private List<BranchSelectionData> result;
	/* for internal purpose */
	private Map<Integer, BreakpointValue> inputValuesByTestIdx;
	private Map<Integer, Set<BreakPoint>> exePathsByTestIdx;
	private int currentTestIdx;
	private Set<BreakPoint> currentTestExePath;
	private DebugValueExtractor valueExtractor;
	private int valRetrieveLevel;
	private BreakpointDataBuilder builder;
	private StopTimer timer = new StopTimer("TestcasesExecutorwithoutLoopTimes");
	private long timeout = DEFAULT_TIMEOUT;
	
	public TestcasesExecutorwithoutLoopTimes(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
	}
	
	public TestcasesExecutorwithoutLoopTimes(DebugValueExtractor valueExtractor) {
		setValueExtractor(valueExtractor);
	}
	
	@Override
	protected void onStart() {
		inputValuesByTestIdx = new HashMap<Integer, BreakpointValue>();
		exePathsByTestIdx = new HashMap<Integer, Set<BreakPoint>>();	
		timer.start();
	}

	@Override
	protected void onEnterTestcase(int testIdx) {
		timer.newPoint(String.valueOf(testIdx));
		currentTestIdx = testIdx;
		currentTestExePath = exePathsByTestIdx.get(testIdx);
		if (currentTestExePath == null) {
			currentTestExePath = new HashSet<BreakPoint>();
			exePathsByTestIdx.put(testIdx, currentTestExePath);
		}
	}

	@Override
	protected void onEnterBreakpoint(BreakPoint bkp, BreakpointEvent bkpEvent) throws SavException {
		if (!bkp.getVars().isEmpty() && inputValuesByTestIdx.get(currentTestIdx) == null) {
			BreakpointValue bkpValue = extractValuesAtLocation(bkp, bkpEvent);
			inputValuesByTestIdx.put(currentTestIdx, bkpValue);
		}
		currentTestExePath.add(bkp);
	}

	@Override
	protected void onFinish(JunitResult jResult) {
		timer.stop();
		if (jResult.getTestResults().isEmpty()) {
			log.warn("TestResults is empty!");
			log.debug(getProccessError());
		}
		int size = inputValuesByTestIdx.size();
		for (int i = 0; i < size; i++) {
			BreakpointValue inputValueOfTcI = inputValuesByTestIdx.get(i);
			Assert.assertNotNull(inputValueOfTcI, "Missing input value for test " + i);
			Set<BreakPoint> exePathOfTcI = exePathsByTestIdx.get(i);
			Assert.assertNotNull(exePathOfTcI, "Missing execution path for test " + i);
			//getBuilder().build(exePathOfTcI, inputValueOfTcI);
		}
		//result = getBuilder().getResult();
		System.out.println(result);
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
	
	public List<BranchSelectionData> getResult() {
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
	
	public void setBuilder(BreakpointDataBuilder builder) {
		this.builder = builder;
	}
	
	public BreakpointDataBuilder getBuilder() {
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
