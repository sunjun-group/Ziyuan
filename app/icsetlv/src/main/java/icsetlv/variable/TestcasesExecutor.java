/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import static sav.strategies.junit.SavJunitRunner.ENTER_TC_BKP;
import static sav.strategies.junit.SavJunitRunner.JUNIT_RUNNER_CLASS_NAME;
import icsetlv.common.dto.BreakPointValue;
import icsetlv.common.dto.BreakpointData;
import icsetlv.trial.model.Trace;
import icsetlv.trial.model.TraceNode;
import icsetlv.trial.variable.DebugValueExtractor2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.SavJunitRunner;
import sav.strategies.junit.JunitRunner.JunitRunnerProgramArgBuilder;
import sav.strategies.vm.SimpleDebugger;
import sav.strategies.vm.VMConfiguration;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;

/**
 * @author Yun Lin 
 * 
 * This class origins from three classes written by LLT, i.e., BreakpointDebugger, 
 * JunitDebugger, and TestcaseExecutor.
 * 
 */
@SuppressWarnings("restriction")
public class TestcasesExecutor{
	private static Logger log = LoggerFactory.getLogger(TestcasesExecutor.class);	
	
	/**
	 * fundamental fields for debugging
	 */
	/** the class patterns indicating the classes into which I will step to get the runtime values*/
	private String[] excludes = { "java.*", "javax.*", "sun.*", "com.sun.*", "org.junit.*"};
	private VMConfiguration config;
	private SimpleDebugger debugger = new SimpleDebugger();
	/** maps from a given class name to its contained breakpoints */
	private Map<String, List<BreakPoint>> brkpsMap;
	private List<BreakPoint> bkps;
	
	/**
	 * fields for junit
	 */
	public static final long DEFAULT_TIMEOUT = -1;
	private List<String> allTests;
	/** for internal purpose */
	private int testIdx = 0;
	private Location junitLoc;
	private String jResultFile;
	private boolean jResultFileDeleteOnExit = false;
	
	/**
	 * fields for test cases
	 */
	private List<BreakpointData> result;
	/** for internal purpose */
	private Map<Integer, List<BreakPointValue>> bkpValsByTestIdx;
	private List<BreakPointValue> currentTestBkpValues;
	private DebugValueExtractor valueExtractor;
	private int valRetrieveLevel;
	private ITestResultVerifier verifier = DefaultTestResultVerifier.getInstance();
	private JunitResult jResult;
	private StopTimer timer = new StopTimer("TestcasesExecutor");;
	private long timeout = DEFAULT_TIMEOUT;
	
	/**
	 * for recording execution trace
	 */
	private Trace trace = new Trace();
	
	public TestcasesExecutor(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
	}
	
	public TestcasesExecutor(DebugValueExtractor valueExtractor) {
		setValueExtractor(valueExtractor);
	}
	
	public void setup(VMConfiguration config) {
		this.config = config;
	}
	
	public void setup(AppJavaClassPath appClassPath, List<String> allTests) {
		VMConfiguration vmConfig = SavJunitRunner.createVmConfig(appClassPath);
		setup(vmConfig);
		this.allTests = allTests;
	}
	
	/** 
	 * record the method entrance and exit so that I can build a tree-structure for trace node
	 */
	private Stack<TraceNode> methodNodeStack = new Stack<>();
	private Stack<Method> methodStack = new Stack<>();
	private TraceNode lastestPopedOutMethodNode = null;
	
	/**
	 * Executing the program, each time of the execution, we catch a JVM event (e.g., step event, class 
	 * preparing event, method entry event, etc.). Generally, we collect the runtime program states in
	 * some interesting step event, and record these steps and their corresponding program states in a 
	 * trance. 
	 * 
	 * <br><br>
	 * Note that the trace node can form a tree-structure in terms of method invocation relations.
	 * 
	 * <br><br>
	 * See the field <code>trace</code> in this class.
	 * @param brkps
	 * @throws SavException
	 */
	public final void run(List<BreakPoint> brkps) throws SavException {
		this.bkps = brkps;
		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		this.config.setDebug(true);
		
		/* before debugging */
		beforeDebugging();
		
		/** start debugger */
		VirtualMachine vm = debugger.run(config);
		if (vm == null) {
			throw new SavException(ModuleEnum.JVM, "cannot start jvm!");
		}
		
		/** add class watch */
		EventRequestManager erm = vm.eventRequestManager(); 
		addClassWatch(erm);

		/** process debug events */
		EventQueue eventQueue = vm.eventQueue();
		
		boolean stop = false;
		boolean eventTimeout = false;
		Map<String, BreakPoint> locBrpMap = new HashMap<String, BreakPoint>();
		
		/** 
		 * This variable aims to record the last executed stepping point. If this variable is not null, then the 
		 * next time we listen a step event, the values collected then are considered the aftermath of latest
		 * recorded trace node.
		 */
		BreakPoint lastSteppingPoint = null;
		
		/**
		 * Yun Lin: <br>
		 * This variable <code>isLastStepEventRecordNode</code> is used to check whether a step performs a method 
		 * invocation. Based on the *observation*, a method entry event happens directly after a step event if this 
		 * step invokes a method. Therefore, if a step event contains the statements we need, meanwhile, the next 
		 * received event is a method entry event, then, I will consider the corresponding step invokes a method.
		 * 
		 * In the implementation, the variable <code>isLastStepEventRecordNode</code> is to indicate a method entry
		 * that an interesting step event just happened right before. Thus, the last recorded trace node should be 
		 * method invocation.
		 */
		boolean isLastStepEventRecordNode = false;
		
		while (!stop && !eventTimeout) {
			EventSet eventSet;
			try {
				eventSet = eventQueue.remove(3000);
			} catch (InterruptedException e) {
				// do nothing, just return.
				break;
			}
			if (eventSet == null) {
				log.warn("Time out! Cannot get event set!");
				eventTimeout = true;
				break;
			}
			
			for (Event event : eventSet) {
				if(event instanceof VMStartEvent){
					System.out.println("start threading");
					/**
					 * add step event
					 */
					StepRequest sr = erm.createStepRequest(((VMStartEvent) event).thread(), 
							StepRequest.STEP_LINE, StepRequest.STEP_INTO);
					sr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					for(String ex: excludes){
						sr.addClassExclusionFilter(ex);
					}
					sr.enable();
					addMethodWatch(erm);
				}
				if (event instanceof VMDeathEvent
						|| event instanceof VMDisconnectEvent) {
					stop = true;
					break;
				} else if (event instanceof ClassPrepareEvent) {
					/** add breakpoint watch on loaded class */
					ClassPrepareEvent classPrepEvent = (ClassPrepareEvent) event;
					handleClassPrepareEvent(vm, classPrepEvent);
					/** add breakpoint request */
					ReferenceType refType = classPrepEvent.referenceType();
					addBreakpointWatch(vm, refType, locBrpMap);
				} else if (event instanceof BreakpointEvent) {
				} else if(event instanceof StepEvent){
					Location loc = ((StepEvent) event).location();
					/**
					 * collect the variable values after executing previous step
					 */
					if(lastSteppingPoint != null){
						BreakPoint currnetPoint = new BreakPoint(lastSteppingPoint.getClassCanonicalName(), 
								lastSteppingPoint.getLineNo());
						onCollectValueOfPreviousStep(currnetPoint, ((StepEvent) event).thread(), loc);
						lastSteppingPoint = null;
					}
					
					BreakPoint bkp = locBrpMap.get(loc.toString());
					if(bkp != null){
						TraceNode node = handleBreakpointEvent(bkp, vm, ((StepEvent) event).thread(), loc);
						/**
						 * set step over previous/next node
						 */
						if(node != null && lastestPopedOutMethodNode != null){
							lastestPopedOutMethodNode.setStepOverNext(node);
							lastestPopedOutMethodNode.setAfterStepOverState(node.getProgramState());
							
							node.setStepOverPrevious(lastestPopedOutMethodNode);
							
							lastestPopedOutMethodNode = null;
						}
						lastSteppingPoint = bkp;
						isLastStepEventRecordNode = true;
					}
					else{
						isLastStepEventRecordNode = false;
					}
				} else if(event instanceof MethodEntryEvent){
					if(isLastStepEventRecordNode){
						MethodEntryEvent mee = (MethodEntryEvent)event;
						Method method = mee.method();
//						System.out.println("enter:" + method.toString());
						
						TraceNode lastestNode = this.trace.getLastestNode();
						
						this.methodNodeStack.push(lastestNode);
						this.methodStack.push(method);
					}
				} else if (event instanceof MethodExitEvent){
					MethodExitEvent mee = (MethodExitEvent)event;
					Method method = mee.method();
					if(!this.methodStack.isEmpty()){
						Method mInStack = this.methodStack.peek();
						if(method.equals(mInStack)){
//							System.out.println("exit:" + mee.method().toString());
							
							TraceNode node = this.methodNodeStack.pop();
							this.lastestPopedOutMethodNode = node;
							this.methodStack.pop();					
						}						
					}
				}
			}
			eventSet.resume();
		}
//		if (!eventTimeout) {
//			vm.resume();
//			/* wait until the process completes */
//			debugger.waitProcessUntilStop();
//		}
		/* end of debug */
		afterDebugging();
	}
	
	/**
	 * add method enter and exit event
	 */
	private void addMethodWatch(EventRequestManager erm) {
		MethodEntryRequest menr = erm.createMethodEntryRequest();
		for(String classPattern: excludes){
			menr.addClassExclusionFilter(classPattern);
		}
		menr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		menr.enable();
		
		MethodExitRequest mexr = erm.createMethodExitRequest();
		for(String classPattern: excludes){
			mexr.addClassExclusionFilter(classPattern);
		}
		mexr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		mexr.enable();
	}
	
	
//	private void addClassWatch(EventRequestManager erm) {
//		/* add class watch for breakpoints */
//		for (String className : brkpsMap.keySet()) {
//			addClassWatch(erm, className);
//		}
//	}
	
	/** add watch requests **/
	private final void addClassWatch(EventRequestManager erm) {
		/* class watch request for breakpoint */
		for (String className : brkpsMap.keySet()) {
			addClassWatch(erm, className);
		}
		/* class watch request for junitRunner start point */
		addClassWatch(erm, ENTER_TC_BKP.getClassCanonicalName());
	}
	
	private final void addClassWatch(EventRequestManager erm, String className) {
		ClassPrepareRequest classPrepareRequest = erm
				.createClassPrepareRequest();
		classPrepareRequest.addClassFilter(className);
		classPrepareRequest.setEnabled(true);
	}
	
	private void addBreakpointWatch(VirtualMachine vm, ReferenceType refType,
			Map<String, BreakPoint> locBrpMap) {
		List<BreakPoint> brkpList = CollectionUtils.initIfEmpty(brkpsMap.get(refType.name()));
		for (BreakPoint brkp : brkpList) {
			Location location = addBreakpointWatch(vm, refType, brkp.getLineNo());
			if (location != null) {
				locBrpMap.put(location.toString(), brkp);
			} else {
				log.warn("Cannot add break point " + brkp);
			}
		}
	}
	
	private final Location addBreakpointWatch(VirtualMachine vm,
			ReferenceType refType, int lineNumber) {
		List<Location> locations;
		try {
			locations = refType.locationsOfLine(lineNumber);
		} catch (AbsentInformationException e) {
			log.warn(e.getMessage());
			return null;
		}
		if (!locations.isEmpty()) {
			Location location = locations.get(0);
			BreakpointRequest breakpointRequest = vm.eventRequestManager()
					.createBreakpointRequest(location);
			breakpointRequest.setEnabled(true);
			return location;
		} 
		return null;
	}
	
	/**
	 * add junit relevant classes into VM configuration
	 * @throws SavException
	 */
	private final void beforeDebugging() throws SavException {
		testIdx = 0;
		junitLoc = null;
		jResultFile = createExecutionResultFile();
		getVmConfig().setLaunchClass(JUNIT_RUNNER_CLASS_NAME);
		List<String> args = new JunitRunnerProgramArgBuilder()
				.methods(allTests).destinationFile(jResultFile)
				.storeSingleTestResultDetail()
				.testcaseTimeout(getTimeoutInSec(), TimeUnit.SECONDS)
				.build();
		getVmConfig().setProgramArgs(args);
		getVmConfig().resetPort();
		onStart();
	}

	private long getTimeoutInSec() {
		return timeout;
	}
	
	private final void handleClassPrepareEvent(VirtualMachine vm,
			ClassPrepareEvent event) {
		/* add junitRunner breakpoint */
		ReferenceType refType = event.referenceType();
		if (refType.name().equals(ENTER_TC_BKP.getClassCanonicalName())) {
			junitLoc = addBreakpointWatch(vm, refType,
					ENTER_TC_BKP.getLineNo());
		} 
	}
	
	private TraceNode handleBreakpointEvent(BreakPoint bkp, VirtualMachine vm,
			ThreadReference thread, Location loc) throws SavException {
		TraceNode node = null;
		try {
			if (areLocationsEqual(loc, junitLoc)) {
				onEnterTestcase(testIdx++);
			} else {
				node = onEnterBreakpoint(bkp, thread, loc);
			}
		} catch (AbsentInformationException e) {
			log.error(e.getMessage());
		}
		
		return node;
	}
	
//	protected void handleMethodEntry

	private final void afterDebugging() throws SavException {
		try {
			JunitResult jResult = JunitResult.readFrom(jResultFile);
			if(jResult.getTestResult().size() == 0){
				System.err.println("Cannot generate test result from an execution.");
			}
			else{
				onFinish(jResult);				
			}
			
		} catch (IOException e) {
			throw new SavException(ModuleEnum.JVM, "cannot read junitResult in temp file");
		}
	}

	private String createExecutionResultFile() throws SavException {
		try {
			File tempFile = File.createTempFile("tcsExResult", ".txt");
			if (jResultFileDeleteOnExit) {
				tempFile.deleteOnExit();
			}
			return tempFile.getAbsolutePath();
		} catch (IOException e1) {
			throw new SavException(ModuleEnum.JVM, "cannot create temp file");
		}
	}
	
	private boolean areLocationsEqual(Location location1, Location location2) throws AbsentInformationException {
		//return location1.compareTo(location2) == 0;
		return location1.equals(location2);
	}
	
	
	
	
	private void onStart() {
		bkpValsByTestIdx = new HashMap<Integer, List<BreakPointValue>>();
		currentTestBkpValues = new ArrayList<BreakPointValue>();
		timer.start();
	}

	private void onEnterTestcase(int testIdx) {
		timer.newPoint(String.valueOf(testIdx));
		currentTestBkpValues = CollectionUtils.getListInitIfEmpty(bkpValsByTestIdx, testIdx);
	}

	private TraceNode onEnterBreakpoint(BreakPoint bkp, ThreadReference thread, Location loc) throws SavException {
		BreakPointValue bkpVal = extractValuesAtLocation(bkp, thread, loc);
		//replace existing one with the new one
		addToCurrentValueList(currentTestBkpValues, bkpVal);
		
		TraceNode node = collectTrace(bkp, bkpVal);
		
		if(!this.methodNodeStack.isEmpty()){
			TraceNode parentInvocationNode = this.methodNodeStack.peek();
			parentInvocationNode.addInvocationChild(node);
			node.setInvocationParent(parentInvocationNode);
		}
		
		return node;
	}
	
	private void onCollectValueOfPreviousStep(BreakPoint currentPosition, 
			ThreadReference thread, Location loc) throws SavException {
		
		if(currentPosition.getLineNo() == 36){
			System.currentTimeMillis();
		}
		
		BreakPointValue bkpVal = extractValuesAtLocation(currentPosition, thread, loc);
		
		int len = trace.getExectionList().size();
		TraceNode node = trace.getExectionList().get(len-1);
		
		node.setAfterStepInState(bkpVal);
	}

	private TraceNode collectTrace(BreakPoint bkp, BreakPointValue bkpVal) {
		int order = trace.size() + 1;
		TraceNode node = new TraceNode(bkp, bkpVal, order);
		
		TraceNode stepInPrevious = null;
		if(order >= 2){
			stepInPrevious = trace.getExectionList().get(order-2);
		}
		
		node.setStepInPrevious(stepInPrevious);
		if(stepInPrevious != null){
			stepInPrevious.setStepInNext(node);			
		}
		
		trace.addTraceNode(node);
		
		return node;
	}

	private void onFinish(JunitResult jResult) {
		timer.stop();
		if (jResult.getTestResults().isEmpty()) {
			log.warn("TestResults is empty!");
			log.debug(getProccessError());
		}
		Map<TestResultType, List<BreakPointValue>> resultMap = new HashMap<TestResultType, List<BreakPointValue>>();
		Map<String, TestResultType> tcExResult = getTcExResult(jResult);
		for (int i = 0; i < bkpValsByTestIdx.size(); i++) {
			TestResultType testResult = tcExResult.get(allTests.get(i));
			if (testResult != TestResultType.UNKNOWN) {
				List<BreakPointValue> bkpValueOfTcI = bkpValsByTestIdx.get(i);
				Assert.assertNotNull(bkpValueOfTcI, "Missing breakpoint value for test " + i);
				CollectionUtils.getListInitIfEmpty(resultMap, testResult)
						.addAll(bkpValueOfTcI);
			}
		}
		result = buildBreakpointData(CollectionUtils.initIfEmpty(resultMap.get(TestResultType.PASS)), 
				CollectionUtils.initIfEmpty(resultMap.get(TestResultType.FAIL)));
		this.jResult = jResult; 
	}

	private Map<String, TestResultType> getTcExResult(JunitResult jResult) {
		Map<String, TestResultType> testResults = new HashMap<String, TestcasesExecutor.TestResultType>();
		log.debug(StringUtils.toStringNullToEmpty(jResult.getTestResults()));
		for (String test : allTests) {
			TestResultType testResult = getTestVerifier().verify(jResult, test);
			testResults.put(test, testResult);
		}
		return testResults;
	}

	private ITestResultVerifier getTestVerifier() {
		if (verifier == null) {
			verifier = DefaultTestResultVerifier.getInstance();
		}
		return verifier;
	}
	
	private List<BreakpointData> buildBreakpointData(
			List<BreakPointValue> passValues, List<BreakPointValue> failValues) {
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
	
	private List<BreakPointValue> getValuesOfBkp(String bkpId,
			List<BreakPointValue> allValues) {
		List<BreakPointValue> result = new ArrayList<BreakPointValue>();
		for (BreakPointValue val : allValues) {
			if (val.getBkpId().equals(bkpId)) {
				result.add(val);
			}
		}
		return result;
	}

	private BreakPointValue extractValuesAtLocation(BreakPoint bkp,
			/*BreakpointEvent bkpEvent*/ThreadReference thread, Location loc) throws SavException {
		try {
			//return getValueExtractor().extractValue(bkp, bkpEvent);
			DebugValueExtractor2 extractor = new DebugValueExtractor2();
			BreakPointValue bpValue = extractor.extractValue(bkp, thread, loc);
			return bpValue;
			
		} catch (IncompatibleThreadStateException e) {
			log.error(e.getMessage());
		} catch (AbsentInformationException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * add breakpoint value to the current list, 
	 * we only keep the value of the last one, so replace the current value (if exists) with the new value.
	 */
	private void addToCurrentValueList(
			List<BreakPointValue> currentTestBkpValues, BreakPointValue bkpVal) {
		if (bkpVal == null) {
			return;
		}
		int i = 0;
		for (; i < currentTestBkpValues.size(); i++) {
			BreakPointValue curVal = currentTestBkpValues.get(i);
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
	
	public void setjResultFileDeleteOnExit(boolean jResultFileDeleteOnExit) {
		this.jResultFileDeleteOnExit = jResultFileDeleteOnExit;
	}

	public List<BreakpointData> getResult() {
		return CollectionUtils.initIfEmpty(result);
	}
	
	public JunitResult getjResult() {
		return jResult;
	}
	
	public DebugValueExtractor getValueExtractor() {
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
	
	public StopTimer getTimer() {
		return timer;
	}
	
	public void setTimeout(long timeout, TimeUnit timeUnit) {
		long timeoutInSec = timeUnit.toSeconds(timeout);
		log.debug("Testcase execution timeout = " + timeoutInSec + "s");
		this.timeout = timeoutInSec;
	}
	
	public Trace getTrace() {
		int len = this.trace.size();
		TraceNode lastNode = this.trace.getExectionList().get(len-1);
		if(lastNode.getAfterState() == null){
			BreakPointValue previousState = lastNode.getProgramState();
			lastNode.setAfterStepInState(previousState);
		}
		
		return trace;
	}
	
	public String getProccessError() {
		return debugger.getProccessError();
	}
	
	public VMConfiguration getVmConfig() {
		return config;
	}
	

	public static enum TestResultType {
		PASS,
		FAIL,
		UNKNOWN;
		
		public static TestResultType of(boolean isPass) {
			if (isPass) {
				return PASS;
			}
			return FAIL;
		}
	}
}

