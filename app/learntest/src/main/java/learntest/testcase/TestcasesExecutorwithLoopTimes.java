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
import icsetlv.variable.DebugValueInstExtractor;
import icsetlv.variable.JunitDebugger;
import learntest.breakpoint.data.BreakpointBuilder;
import learntest.breakpoint.data.DecisionLocation;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.BreakpointDataBuilder;
import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.StopTimer;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;

public class TestcasesExecutorwithLoopTimes extends JunitDebugger {

	private static Logger log = LoggerFactory.getLogger(TestcasesExecutorwithLoopTimes.class);	
	//private Map<DecisionLocation, BreakpointData> result;
	/* for internal purpose */
	private Map<Integer, List<BreakpointValue>> inputValuesByTestIdx;
	private Map<Integer, List<BreakPoint>> exePathsByTestIdx;
	private List<BreakPoint> currentTestExePath;
	private List<BreakpointValue> currentTestInputValues;
	private DebugValueExtractor valueExtractor;
	private DebugValueExtractor instValueExtractor;
	private int valRetrieveLevel;
	private BreakpointDataBuilder dtbuilder;
	private StopTimer timer = new StopTimer("TestcasesExecutorwithLoopTimes");
	private long timeout = DEFAULT_TIMEOUT;
	
	private BreakpointBuilder bkpBuilder;
	private DecisionLocation target;
	
	private List<Map<String,Object>> instrVarMaps;
	private boolean instrMode;
	
	public TestcasesExecutorwithLoopTimes(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
		this.valueExtractor = new DebugValueExtractor(valRetrieveLevel);
	}
	
	public void run() throws SavException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (target == null) {
			this.run(bkpBuilder.getBreakPoints());
		} else {
			this.run(bkpBuilder.getBreakpoints(target));
		}
	}
	
	@Override
	protected void onStart() {
		inputValuesByTestIdx = new HashMap<Integer, List<BreakpointValue>>();
		exePathsByTestIdx = new HashMap<Integer, List<BreakPoint>>();
		timer.start();
	}

	@Override
	protected void onEnterTestcase(int testIdx) {
		timer.newPoint(String.valueOf(testIdx));
		currentTestExePath = exePathsByTestIdx.get(testIdx);
		if (currentTestExePath == null) {
			currentTestExePath = new ArrayList<BreakPoint>();
			exePathsByTestIdx.put(testIdx, currentTestExePath);
		}
		currentTestInputValues = inputValuesByTestIdx.get(testIdx);
		if (currentTestInputValues == null) {
			currentTestInputValues = new ArrayList<BreakpointValue>();
			inputValuesByTestIdx.put(testIdx, currentTestInputValues);
		}
		if (instrMode) {
			setVarMap(instrVarMaps.get(testIdx));
		}
	}

	@Override
	protected void onEnterBreakpoint(BreakPoint bkp, BreakpointEvent bkpEvent) throws SavException {
		if (!bkp.getVars().isEmpty()) {
			BreakpointValue bkpValue = extractValuesAtLocation(bkp, bkpEvent);
			currentTestInputValues.add(bkpValue);
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
			List<BreakpointValue> inputValueOfTcI = inputValuesByTestIdx.get(i);
			Assert.assertNotNull(inputValueOfTcI, "Missing input value for test " + i);
			List<BreakPoint> exePathOfTcI = exePathsByTestIdx.get(i);
			Assert.assertNotNull(exePathOfTcI, "Missing execution path for test " + i);
			/*System.out.println("Tc" + i + ":");
			System.out.println("input:" + inputValueOfTcI);
			for (BreakPoint bkp : exePathOfTcI) {
				System.out.println("\t" + bkp.getId());
			}
			System.out.println("================");*/
			getBuilder().build(exePathOfTcI, inputValueOfTcI);
		}
		//result = getBuilder().getResult();
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
	
	public Map<DecisionLocation, BreakpointData> getResult() {
		return getBuilder().getResult();
	}
	
	public void setTcNum(int num) {		
		int size = allTests.size();
		if (size == num) {
			return;
		}
		if (size > num) {
			allTests = allTests.subList(0, num);
		} else {
			int remain = num - size;
			while (remain >= size) {
				allTests.addAll(allTests);
				remain -= size;
				size += size;
			}
			allTests.addAll(allTests.subList(0, remain));
		}		
	}
	
	public void setTarget(DecisionLocation target) {
		this.target = target;
		dtbuilder.setTarget(target);
	}
	
	public void setInstrMode(boolean mode) {
		instrMode = mode;
	}
	
	public void setVarMaps(List<Map<String, Object>> instrVarMaps) {
		this.instrVarMaps = instrVarMaps;
	}

	private void setVarMap(Map<String, Object> instrVarMap) {
		this.instValueExtractor = new DebugValueInstExtractor(getValRetrieveLevel(), instrVarMap);
	}
	
	private DebugValueExtractor getValueExtractor() {
		if (instValueExtractor != null && currentTestInputValues.isEmpty()) {
			return instValueExtractor;
		}
		return valueExtractor;
	}
	
	public int getValRetrieveLevel() {
		return valRetrieveLevel;
	}
	
	public void setValRetrieveLevel(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
		if (valueExtractor != null) {
			valueExtractor.setValRetrieveLevel(valRetrieveLevel);
		}
		if (instValueExtractor != null) {
			instValueExtractor.setValRetrieveLevel(valRetrieveLevel);
		}
	}
	
	public void setBuilder(BreakpointDataBuilder dtbuilder) {
		this.dtbuilder = dtbuilder;
	}
	
	public BreakpointDataBuilder getBuilder() {
		return dtbuilder;
	}	
	
	public void setBkpBuilder(BreakpointBuilder bkpBuilder) {
		this.bkpBuilder = bkpBuilder;
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
