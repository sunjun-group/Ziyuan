/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package microbat.codeanalysis.runtime;

import static sav.strategies.junit.SavJunitRunner.ENTER_TC_BKP;
import static sav.strategies.junit.SavJunitRunner.JUNIT_RUNNER_CLASS_NAME;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import microbat.codeanalysis.ast.LocalVariableScope;
import microbat.codeanalysis.ast.VariableScopeParser;
import microbat.codeanalysis.runtime.jpda.expr.ExpressionParser;
import microbat.codeanalysis.runtime.jpda.expr.ParseException;
import microbat.codeanalysis.runtime.variable.DebugValueExtractor;
import microbat.model.BreakPoint;
import microbat.model.BreakPointValue;
import microbat.model.trace.StepVariableRelationEntry;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.variable.LocalVar;
import microbat.model.variable.Variable;
import microbat.util.BreakpointUtils;
import microbat.util.Settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.StopTimer;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner.JunitRunnerProgramArgBuilder;
import sav.strategies.junit.SavJunitRunner;
import sav.strategies.vm.SimpleDebugger;
import sav.strategies.vm.VMConfiguration;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
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
//	private List<BreakpointData> result;
//	private DebugValueExtractor valueExtractor;
	
	/** for internal purpose */
	private Map<Integer, List<BreakPointValue>> bkpValsByTestIdx;
	private List<BreakPointValue> currentTestBkpValues;
	private int valRetrieveLevel;
	//private ITestResultVerifier verifier = DefaultTestResultVerifier.getInstance();
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
	 * trace. 
	 * 
	 * <br><br>
	 * Note that the trace node can form a tree-structure in terms of method invocation relations.
	 * 
	 * <br><br>
	 * See the field <code>trace</code> in this class.
	 * @param brkps
	 * @throws SavException
	 */
	public final void run(List<BreakPoint> brkps) throws SavException{
		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		this.config.setDebug(true);
		
		/** before debugging */
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
		BreakPoint lastSteppingInPoint = null;
		
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
				System.out.println("Time out! Cannot get event set!");
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
					
					if(loc.lineNumber() == 5){
						System.currentTimeMillis();
					}
					/**
					 * collect the variable values after executing previous step
					 * 
					 * TODO it is possible to optimize the program space this step. I just need 
					 * the consequence of last step, thus, I may just check those written variables 
					 * and their values.
					 */
					if(lastSteppingInPoint != null){
						
						/**
						 * If context change, it means that last stepping point should be a method invocation. Thus,
						 * its consequence state should be collected when the method invocation is step over (instead
						 * of stepping into this method).
						 */
						boolean isContextChange = checkContext(lastSteppingInPoint, loc);
						BreakPoint currnetPoint = new BreakPoint(lastSteppingInPoint.getClassCanonicalName(), 
								lastSteppingInPoint.getLineNo());
						
						onCollectValueOfPreviousStep(currnetPoint, ((StepEvent) event).thread(), loc, isContextChange);	
						
						lastSteppingInPoint = null;
					}
					
					BreakPoint bkp = locBrpMap.get(loc.toString());
					/**
					 * This step is an interesting step (sliced statement) in our debugging process
					 */
					if(bkp != null){
						if(loc.lineNumber() == 16){
							System.currentTimeMillis();
						}
						
						TraceNode node = handleBreakpointEvent(bkp, vm, ((StepEvent) event).thread(), loc);
						/**
						 * set step over previous/next node when this step just come back from a method invocation (
						 * i.e., lastestPopedOutMethodNode != null).
						 */
						if(node != null && lastestPopedOutMethodNode != null){
							lastestPopedOutMethodNode.setStepOverNext(node);
							lastestPopedOutMethodNode.setAfterStepOverState(node.getProgramState());
							
							node.setStepOverPrevious(lastestPopedOutMethodNode);
							
							lastestPopedOutMethodNode = null;
							
//							/**
//							 * update the written variable after finishing a method invocation.
//							 */
//							updateStepVariableRelationTable(((StepEvent) event).thread(), loc, node, 
//									this.trace.getStepVariableTable(), WRITTEN);	
							
						}
						lastSteppingInPoint = bkp;
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
						
						updateStepVariableRelationTableByMethodInvocation(event, mee, method, lastestNode);
						
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
	 * build the written relations between method invocation
	 */
	private void updateStepVariableRelationTableByMethodInvocation(Event event,
			MethodEntryEvent mee, Method method, TraceNode lastestNode) {
		try {
			for(LocalVariable lVar: method.arguments()){
				LocalVar localVar = new LocalVar(lVar.name(), lVar.typeName());
				
				StackFrame frame = findFrame(((MethodEntryEvent) event).thread(), mee.location());
				Value value = frame.getValue(lVar);
				
				if(value instanceof ObjectReference){
					ObjectReference objRef = (ObjectReference)value;
					String varID = String.valueOf(objRef.uniqueID());
					
					localVar.setVarID(varID);
				}
				else{
					VariableScopeParser parser = new VariableScopeParser();
					String typeSig = method.declaringType().signature();
					String typeName = SignatureUtils.signatureToName(typeSig);
					LocalVariableScope scope = parser.parseMethodScope(typeName, 
							mee.location().lineNumber(), localVar.getName());
					String varID;
					if(scope != null){
						varID = typeName + "[" + scope.getStartLine() + "," 
								+ scope.getEndLine() + "] " + localVar.getName();				
						localVar.setVarID(varID);
					}
					else{
						System.err.println("cannot find the method when parsing parameter scope");
					}
				}
				
				StepVariableRelationEntry entry = this.trace.getStepVariableTable().get(localVar.getVarID());
				if(entry == null){
					entry = new StepVariableRelationEntry(localVar.getVarID());
				}
				entry.addAliasVariable(localVar);
				entry.addProducer(lastestNode);
			}
		} catch (AbsentInformationException e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkContext(BreakPoint lastSteppingPoint, Location loc) {
		String methodSign1 = lastSteppingPoint.getMethodSign();
		methodSign1 = methodSign1.substring(methodSign1.lastIndexOf(".")+1, methodSign1.length());
		
		String methodSign2 = loc.method().signature();
		methodSign2 = loc.method().name() + methodSign2;
		
		String class1 = loc.declaringType().signature();
		class1 = SignatureUtils.signatureToName(class1);
		String class2 = lastSteppingPoint.getClassCanonicalName();
		
		if(methodSign1.equals(methodSign2) && class1.equals(class2)){
			return false;
		}
		else{
			return true;			
		}
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
				//log.warn("Cannot add break point " + brkp);
				System.out.println("Cannot add break point " + brkp);
			}
		}
	}
	
	private final Location addBreakpointWatch(VirtualMachine vm,
			ReferenceType refType, int lineNumber) {
		List<Location> locations;
		try {
			locations = refType.locationsOfLine(lineNumber);
		} catch (AbsentInformationException e) {
//			log.warn(e.getMessage());
			e.printStackTrace();
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
			//log.error(e.getMessage());
			e.printStackTrace();
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

		updateStepVariableRelationTable(thread, loc, node, this.trace.getStepVariableTable(), READ);
		
		if(!this.methodNodeStack.isEmpty()){
			TraceNode parentInvocationNode = this.methodNodeStack.peek();
			parentInvocationNode.addInvocationChild(node);
			node.setInvocationParent(parentInvocationNode);
		}
		
		return node;
	}
	
	private String generateVarID(StackFrame frame, Variable var, TraceNode node){
		String varName = var.getName();
		try{
			ExpressionValue expValue = retriveExpression(frame, varName);
			if(expValue != null){
				Value value = expValue.value;
				
				if(value instanceof ObjectReference){
					ObjectReference objRef = (ObjectReference)value;
					String varID = String.valueOf(objRef.uniqueID());
					
					var.setVarID(varID);
				}
				else{
					if(var instanceof LocalVar){
						//VariableScopeParser parser = new VariableScopeParser();
						//TODO a bug, should include the definition of this variable
						//LocalVariableScope scope = parser.parseScope(node.getBreakPoint(), (LocalVar)var);
						
						LocalVariableScope scope = Settings.localVariableScopes.findScope(var.getName(), 
								node.getBreakPoint().getLineNo(), node.getBreakPoint().getClassCanonicalName());
						String varID;
						if(scope != null){
							varID = node.getBreakPoint().getClassCanonicalName() + "[" + scope.getStartLine() + "," 
									+ scope.getEndLine() + "] " + var.getName();				
						}
						/**
						 * it means that an implicit "this" variable is visited.
						 * 
						 */
						else if(var.getName().equals("this")){
							varID = String.valueOf(frame.thisObject().uniqueID());
						}
						else{
							System.err.println("the local variable " + var.getName() + " cannot find its scope to generate its id");
							return null;
						}
						var.setVarID(varID);
					}
					else{
						Value parentValue = expValue.parentValue;
						ObjectReference objRef = (ObjectReference)parentValue;
						String varID = String.valueOf(objRef.uniqueID()) + var.getSimpleName();
						var.setVarID(varID);
					}
					
				}
				
				return var.getVarID();
			}							
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String READ = "read";
	public static String WRITTEN = "written";
	
	private StackFrame findFrame(ThreadReference thread, Location location){
		StackFrame frame = null;
		try {
			for (StackFrame f : thread.frames()) {
				if (f.location().equals(location)) {
					frame = f;
					break;
				}
			}
		} catch (IncompatibleThreadStateException e) {
			e.printStackTrace();
		}
		
		return frame;
	}
	
	private void updateStepVariableRelationTable(ThreadReference thread, Location location, TraceNode node, 
			Map<String, StepVariableRelationEntry> stepVariableTable, String action) {
		
		StackFrame frame = findFrame(thread, location);
		if(frame == null){
			System.err.println("get a null frame from thread!");
			return;
		}
		
		synchronized (frame) {
			if(action.equals(READ)){
				processReadVariable(node, stepVariableTable, frame);							
			}
			else if(action.equals(WRITTEN)){
				processWrittenVariable(node, stepVariableTable, frame);
			}
		}
		//processWrittenVariable(node, stepVariableTable, frame);
	}

	private void processReadVariable(TraceNode node,
			Map<String, StepVariableRelationEntry> stepVariableTable,
			StackFrame frame) {
		List<Variable> readVariables = node.getBreakPoint().getReadVariables();
		for(Variable readVar: readVariables){
			String varID = generateVarID(frame, readVar, node);
			System.currentTimeMillis();
			if(varID == null){
				System.err.println("there is an error when generating the id for " + readVar + 
						" in line " + node.getBreakPoint().getLineNo() + " of " + node.getBreakPoint().getClassCanonicalName());
				varID = generateVarID(frame, readVar, node);
			}
			else{
				StepVariableRelationEntry entry = stepVariableTable.get(varID);
				if(entry == null){
					entry = new StepVariableRelationEntry(varID);	
					stepVariableTable.put(varID, entry);
				}
				entry.addAliasVariable(readVar);
				
				entry.addConsumer(node);
			}
		}
	}
	
	private void processWrittenVariable(TraceNode node,
			Map<String, StepVariableRelationEntry> stepVariableTable,
			StackFrame frame) {
		List<Variable> writtenVariables = node.getBreakPoint().getWrittenVariables();
		for(Variable writtenVar: writtenVariables){
			String varID = generateVarID(frame, writtenVar, node);
			if(varID == null){
				System.err.println("there is an error when generating the id for " + writtenVar + 
						" in line " + node.getBreakPoint().getLineNo() + " of " + node.getBreakPoint().getClassCanonicalName());
				varID = generateVarID(frame, writtenVar, node);
			}
			else{
				StepVariableRelationEntry entry = stepVariableTable.get(varID);
				if(entry == null){
					entry = new StepVariableRelationEntry(varID);	
					stepVariableTable.put(varID, entry);
				}
				entry.addAliasVariable(writtenVar);
				
				entry.addProducer(node);
			}
		}
	}
	
	
	private ExpressionValue retriveExpression(final StackFrame frame, String expression){
		ExpressionParser.GetFrame frameGetter = new ExpressionParser.GetFrame() {
            @Override
            public StackFrame get()
                throws IncompatibleThreadStateException
            {
            	return frame;
                
            }
        };
        
        ExpressionValue eValue = null;
        
        try {
        	ExpressionParser.parentValue = null;
        	Value val = ExpressionParser.evaluate(expression, frame.virtualMachine(), frameGetter);
			
			eValue = new ExpressionValue(val, ExpressionParser.parentValue);
			
			System.currentTimeMillis();
			
		} catch (ParseException e) {
			//e.printStackTrace();
		} catch (InvocationException e) {
			e.printStackTrace();
		} catch (InvalidTypeException e) {
			e.printStackTrace();
		} catch (ClassNotLoadedException e) {
			e.printStackTrace();
		} catch (IncompatibleThreadStateException e) {
			e.printStackTrace();
		}
        
        return eValue;
	}

	private void onCollectValueOfPreviousStep(BreakPoint currentPosition, 
			ThreadReference thread, Location loc, boolean isContextChange) throws SavException {
		
		if(currentPosition.getLineNo() == 36){
			System.currentTimeMillis();
		}
		
		BreakPointValue bkpVal = extractValuesAtLocation(currentPosition, thread, loc);
		
		int len = trace.getExectionList().size();
		TraceNode node = trace.getExectionList().get(len-1);
		
		node.setAfterStepInState(bkpVal);
		
		if(!isContextChange){
			updateStepVariableRelationTable(thread, loc, node, this.trace.getStepVariableTable(), WRITTEN);			
		}
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
//		result = buildBreakpointData(CollectionUtils.initIfEmpty(resultMap.get(TestResultType.PASS)), 
//				CollectionUtils.initIfEmpty(resultMap.get(TestResultType.FAIL)));
		this.jResult = jResult; 
	}

	private Map<String, TestResultType> getTcExResult(JunitResult jResult) {
		Map<String, TestResultType> testResults = new HashMap<String, TestcasesExecutor.TestResultType>();
		log.debug(StringUtils.toStringNullToEmpty(jResult.getTestResults()));
		for (String test : allTests) {
			//TODO test verfier is uncommented, I may need to check this later.
//			TestResultType testResult = getTestVerifier().verify(jResult, test);
//			testResults.put(test, testResult);
		}
		return testResults;
	}

//	private ITestResultVerifier getTestVerifier() {
//		if (verifier == null) {
//			verifier = DefaultTestResultVerifier.getInstance();
//		}
//		return verifier;
//	}
	
//	private List<BreakpointData> buildBreakpointData(
//			List<BreakPointValue> passValues, List<BreakPointValue> failValues) {
//		List<BreakpointData> result = new ArrayList<BreakpointData>(bkps.size());
//		for (BreakPoint bkp : bkps) {
//			BreakpointData bkpData = new BreakpointData();
//			bkpData.setBkp(bkp);
//			bkpData.setPassValues(getValuesOfBkp(bkp.getId(), passValues));
//			bkpData.setFailValues(getValuesOfBkp(bkp.getId(), failValues));
//			result.add(bkpData);
//		}
//		return result;
//	}
	
	private List<BreakPointValue> getValuesOfBkp(String bkpId, List<BreakPointValue> allValues) {
		List<BreakPointValue> result = new ArrayList<BreakPointValue>();
		for (BreakPointValue val : allValues) {
			if (val.getBkpId().equals(bkpId)) {
				result.add(val);
			}
		}
		return result;
	}

	private BreakPointValue extractValuesAtLocation(BreakPoint bkp, ThreadReference thread, 
			Location loc) throws SavException {
		try {
			//return getValueExtractor().extractValue(bkp, bkpEvent);
			DebugValueExtractor extractor = new DebugValueExtractor();
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

//	public List<BreakpointData> getResult() {
//		return CollectionUtils.initIfEmpty(result);
//	}
	
	public JunitResult getjResult() {
		return jResult;
	}
	
//	public DebugValueExtractor getValueExtractor() {
//		if (valueExtractor == null) {
//			setValueExtractor(new DebugValueExtractor(valRetrieveLevel));
//		}
//		return valueExtractor;
//	}
//
//	public void setValueExtractor(DebugValueExtractor valueExtractor) {
//		this.valueExtractor = valueExtractor;
//		if (valueExtractor != null) {
//			this.valRetrieveLevel = valueExtractor.getValRetrieveLevel();
//		}
//	}
	
	public void setValRetrieveLevel(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
//		if (valueExtractor != null) {
//			valueExtractor.setValRetrieveLevel(valRetrieveLevel);
//		}
	}
	
	public int getValRetrieveLevel() {
		return valRetrieveLevel;
	}
	
//	public void setTestResultVerifier(ITestResultVerifier verifier) {
//		this.verifier = verifier;
//	}
	
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
	
	class ExpressionValue{
		Value value;
		/**
		 * used to decide the memory address, this value must be an ObjectReference.
		 */
		Value parentValue;
		
		public ExpressionValue(Value value, Value parentValue){
			this.value = value;
			this.parentValue = parentValue;
		}
		
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

