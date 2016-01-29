/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package microbat.codeanalysis.runtime;

import static sav.strategies.junit.SavJunitRunner.ENTER_TC_BKP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import microbat.codeanalysis.ast.LocalVariableScope;
import microbat.codeanalysis.ast.VariableScopeParser;
import microbat.codeanalysis.runtime.jpda.expr.ExpressionParser;
import microbat.codeanalysis.runtime.jpda.expr.ParseException;
import microbat.codeanalysis.runtime.variable.VariableValueExtractor;
import microbat.model.BreakPoint;
import microbat.model.BreakPointValue;
import microbat.model.trace.StepVariableRelationEntry;
import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.variable.ArrayElementVar;
import microbat.model.variable.FieldVar;
import microbat.model.variable.LocalVar;
import microbat.model.variable.Variable;
import microbat.model.variable.VirtualVar;
import microbat.util.BreakpointUtils;
import microbat.util.JavaUtil;
import microbat.util.Settings;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SignatureUtils;
import sav.strategies.dto.AppJavaClassPath;
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
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
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
	public static final long DEFAULT_TIMEOUT = -1;
	
	private static Logger log = LoggerFactory.getLogger(TestcasesExecutor.class);	
	
	/**
	 * fundamental fields for debugging
	 */
	/** the class patterns indicating the classes into which I will not step to get the runtime values*/
	private String[] excludes = { "java.*", "javax.*", "sun.*", "com.sun.*", "org.junit.*"};
	private VMConfiguration config;
	private SimpleDebugger debugger = new SimpleDebugger();
	/** maps from a given class name to its contained breakpoints */
	private Map<String, List<BreakPoint>> brkpsMap;
	
	
	/**
	 * fields for junit
	 */
//	private List<String> allTests;
//	private long timeout = DEFAULT_TIMEOUT;
	
	/**
	 * for recording execution trace
	 */
	private Trace trace = new Trace();
	
	public TestcasesExecutor() {
	}
	
	public void setup(VMConfiguration config) {
		this.config = config;
	}
	
	public void setup(AppJavaClassPath appClassPath){
		VMConfiguration vmConfig = new VMConfiguration(appClassPath);
		this.config = vmConfig;
		this.config.setLaunchClass(Settings.lanuchClass);
		this.config.setWorkingDirectory(appClassPath.getWorkingDirectory());
	}
	
	
	
	/**
	 * Executing the program, each time of the execution, we catch a JVM event (e.g., step event, class 
	 * preparing event, method entry event, etc.). Generally, we collect the runtime program states in
	 * some interesting step event, and record these steps and their corresponding program states in a 
	 * trace node. 
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
		
		/** start debugger */
		VirtualMachine vm = new VMStarter(this.config).start();
		EventRequestManager erm = vm.eventRequestManager(); 
		
		/** add class watch, otherwise, I cannot catch the registered event */
		addClassWatch(erm);

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
		
		/** record the method entrance and exit so that I can build a tree-structure for trace node. */
		Stack<TraceNode> methodNodeStack = new Stack<>();
		Stack<Method> methodStack = new Stack<>();
		
		/** this variable is used to build step-over relation between trace nodes. */
		TraceNode methodNodeJustPopedOut = null;
		
		/** this variable is used to handle exception case. */
		Location caughtLocationForJustException = null;
		
		while (!stop && !eventTimeout) {
			EventSet eventSet;
			try {
				eventSet = eventQueue.remove(3000);
			} catch (InterruptedException e) {
				break;
			}
			if (eventSet == null) {
				System.out.println("Time out! Cannot get event set!");
				eventTimeout = true;
				break;
			}
			
			if(trace.getLastestNode() != null){
				if(trace.getLastestNode().getOrder() == 21){
					System.currentTimeMillis();
				}
			}
			
			for (Event event : eventSet) {
				if(event instanceof VMStartEvent){
					System.out.println("JVM is started...");
					
					addStepWatch(erm, event);
					addMethodWatch(erm);
					addExceptionWatch(erm);					
				}
				if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
					stop = true;
					break;
				} else if (event instanceof ClassPrepareEvent) {
					parseBreakpoints(vm, (ClassPrepareEvent)event, locBrpMap);
				} else if(event instanceof StepEvent){
					Location currentLocation = ((StepEvent) event).location();
					if(currentLocation.lineNumber() == 88){
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
						collectValueOfPreviousStep(lastSteppingInPoint, ((StepEvent) event).thread(), currentLocation);	
						
						/** 
						 * Parsing the written variables of last step.
						 * 
						 * If the context changes, the value of some variables may not be retrieved. Thus, the variable ID cannot be 
						 * generated. Note that the ID of a variable need parsing its heap ID which can only be accessed by runtime.
						 */
						boolean isContextChange = checkContext(lastSteppingInPoint, currentLocation);
						if(!isContextChange){
							parseReadWrittenVariableInThisStep(((StepEvent) event).thread(), currentLocation, 
									this.trace.getLastestNode(), this.trace.getStepVariableTable(), WRITTEN);			
						}
						
						lastSteppingInPoint = null;
					}
					
					BreakPoint bkp = locBrpMap.get(currentLocation.toString());
					/**
					 * This step is an interesting step (sliced statement) in our debugging process
					 */
					if(bkp != null){
						//TraceNode node = handleBreakpoint(bkp, ((StepEvent) event).thread(), currentLocation);
						BreakPointValue bkpVal = extractValuesAtLocation(bkp, ((StepEvent) event).thread(), currentLocation);
						TraceNode node = recordTrace(bkp, bkpVal);
						
						if(caughtLocationForJustException != null){
							if(!methodNodeStack.isEmpty()){
								TraceNode invocationNode = this.trace.findLastestExceptionNode();
								boolean isInvocationEnvironmentContainingLocation = isInvocationEnvironmentContainingLocation(invocationNode, caughtLocationForJustException);
								while(!isInvocationEnvironmentContainingLocation){
									if(!methodNodeStack.isEmpty()){
										invocationNode = methodNodeStack.pop();		
										methodNodeJustPopedOut = invocationNode;
										methodStack.pop();
										
										isInvocationEnvironmentContainingLocation = isInvocationEnvironmentContainingLocation(invocationNode, caughtLocationForJustException);
									}
									else{
										break;
									}
								}
							}
							caughtLocationForJustException = null;
						}
						
						/**
						 * Build parent-child relation between trace nodes.
						 */
						if(!methodNodeStack.isEmpty()){
							TraceNode parentInvocationNode = methodNodeStack.peek();
							parentInvocationNode.addInvocationChild(node);
							node.setInvocationParent(parentInvocationNode);
						}
						
						parseReadWrittenVariableInThisStep(((StepEvent) event).thread(), currentLocation, node, 
								this.trace.getStepVariableTable(), READ);
						
						/**
						 * set step over previous/next node when this step just come back from a method invocation (
						 * i.e., lastestPopedOutMethodNode != null).
						 */
						if(node != null && methodNodeJustPopedOut != null){
							methodNodeJustPopedOut.setStepOverNext(node);
							methodNodeJustPopedOut.setAfterStepOverState(node.getProgramState());
							
							node.setStepOverPrevious(methodNodeJustPopedOut);
							
							methodNodeJustPopedOut = null;
						}
						
						/**
						 * create virtual variable for return statement
						 */
						if(this.trace.size() > 1){
							TraceNode lastestNode = this.trace.getExectionList().get(this.trace.size()-2);
							if(lastestNode.getBreakPoint().isReturnStatement()){
								createVirutalVariableForReturnStatement(node, lastestNode);
							}
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
						TraceNode lastestNode = this.trace.getLastestNode();
						
						try {
							if(!method.arguments().isEmpty()){
								parseReadWrittenVirtualVariableForMethodInvocation(event, mee, lastestNode);							
							}
						} catch (AbsentInformationException e) {
							e.printStackTrace();
						}
						
						methodNodeStack.push(lastestNode);
						methodStack.push(method);
					}
				} else if (event instanceof MethodExitEvent){
					MethodExitEvent mee = (MethodExitEvent)event;
					Method method = mee.method();
					if(!methodStack.isEmpty()){
						Method mInStack = methodStack.peek();
						if(method.equals(mInStack)){
							TraceNode node = methodNodeStack.pop();
							methodNodeJustPopedOut = node;
							methodStack.pop();					
						}						
					}
				} else if(event instanceof ExceptionEvent){
					ExceptionEvent ee = (ExceptionEvent)event;
					Location catchLocation = ee.catchLocation();
					this.trace.getLastestNode().setException(true);
					
					if(catchLocation == null){
						stop = true;
					}
					else{
						caughtLocationForJustException = ee.catchLocation();
					}
				}
			}
			
			eventSet.resume();
		}
	}

	class MethodFinder extends ASTVisitor{
		CompilationUnit cu;
		int lineNumber;
		
		MethodDeclaration candidate;
		
		public MethodFinder(CompilationUnit cu, int lineNumber){
			this.cu = cu;
			this.lineNumber = lineNumber;
		}
		
		public boolean visit(MethodDeclaration md){
			int startLine = cu.getLineNumber(md.getStartPosition());
			int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength());
			
			if(startLine<=lineNumber && endLine >=lineNumber){
				if(candidate == null){
					candidate = md;
				}
				else{
					int candStartLine = cu.getLineNumber(candidate.getStartPosition());
					int candEndLine = cu.getLineNumber(candidate.getStartPosition() + candidate.getLength());
					
					if(startLine>=candStartLine && endLine<=candEndLine){
						candidate = md;
					}
				}
			}
			
			return true;
		}
	}
	
	private boolean isInvocationEnvironmentContainingLocation(TraceNode methodNode, Location caughtLocationForJustException) {
		String qualifiedName = methodNode.getBreakPoint().getDeclaringCompilationUnitName();
		int lineNumber = methodNode.getLineNumber();
		
		try {
			String path = caughtLocationForJustException.sourcePath();
			path = path.substring(0, path.indexOf(".java"));
			path = path.replace("\\", ".");
			
			if(qualifiedName.equals(path)){
				CompilationUnit cu = JavaUtil.findCompilationUnitInProject(qualifiedName);
				if(cu != null){
					MethodFinder mFinder = new MethodFinder(cu, lineNumber);
					cu.accept(mFinder);
					MethodDeclaration md = mFinder.candidate;
					
					if(md != null){
						int mdStartLine = cu.getLineNumber(md.getStartPosition());
						int mdEndLine = cu.getLineNumber(md.getStartPosition()+md.getLength());
						
						int caughtLine = caughtLocationForJustException.lineNumber();
						if(caughtLine >= mdStartLine && caughtLine <= mdEndLine){
							return true;
						}
					}
				}
				
			}
		} catch (AbsentInformationException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	private void addExceptionWatch(EventRequestManager erm) {
		
		ExceptionRequest request = erm.createExceptionRequest(null, true, true);
		request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		for(String ex: excludes){
			request.addClassExclusionFilter(ex);
		}
//		request.addClassFilter("java.io.FileNotFoundException");
		request.enable();
	}

	private void addStepWatch(EventRequestManager erm, Event event) {
		StepRequest sr = erm.createStepRequest(((VMStartEvent) event).thread(), 
				StepRequest.STEP_LINE, StepRequest.STEP_INTO);
		sr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		for(String ex: excludes){
			sr.addClassExclusionFilter(ex);
		}
		sr.enable();
	}

	/**
	 * when the last interesting stepping statement is a return statement, create a virtual
	 * variable.
	 */
	private void createVirutalVariableForReturnStatement(TraceNode node,
			TraceNode lastestNode) {
		String name = VirtualVar.VIRTUAL_PREFIX + lastestNode.getOrder();
		VirtualVar var = new VirtualVar(name, VirtualVar.VIRTUAL_TYPE);
		var.setVarID(name);
		
		Map<String, StepVariableRelationEntry> map = this.trace.getStepVariableTable();
		StepVariableRelationEntry entry = new StepVariableRelationEntry(var.getVarID());
		entry.addAliasVariable(var);
		entry.addProducer(lastestNode);
		entry.addConsumer(node);
		
		map.put(var.getVarID(), entry);
	}

	/**
	 * build the written relations between method invocation
	 */
	private void parseReadWrittenVirtualVariableForMethodInvocation(Event event,
			MethodEntryEvent mee, TraceNode lastestNode) {
		try {
			Method method = mee.method();
			for(LocalVariable lVar: method.arguments()){
				StackFrame frame = findFrame(((MethodEntryEvent) event).thread(), mee.location());
				
				if(frame == null){
					return;
				}
				
				Value value = frame.getValue(lVar);
				
				if(!(value instanceof ObjectReference)){
					
					LocalVar localVar = new LocalVar(lVar.name(), lVar.typeName());
					
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
					
					StepVariableRelationEntry entry = this.trace.getStepVariableTable().get(localVar.getVarID());
					if(entry == null){
						entry = new StepVariableRelationEntry(localVar.getVarID());
					}
					entry.addAliasVariable(localVar);
					entry.addProducer(lastestNode);
				}
				
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
	
	private void parseBreakpoints(VirtualMachine vm, ClassPrepareEvent classPrepEvent, Map<String, BreakPoint> locBrpMap) {
		ReferenceType refType = classPrepEvent.referenceType();
		List<BreakPoint> brkpList = CollectionUtils.initIfEmpty(brkpsMap.get(refType.name()));
		for (BreakPoint brkp : brkpList) {
			Location location = checkBreakpoint(vm, refType, brkp.getLineNo());
			if (location != null) {
				locBrpMap.put(location.toString(), brkp);
			} else {
				System.err.println("Cannot add break point " + brkp);
			}
		}
	}
	
	private final Location checkBreakpoint(VirtualMachine vm,
			ReferenceType refType, int lineNumber) {
		List<Location> locations;
		try {
			locations = refType.locationsOfLine(lineNumber);
		} catch (AbsentInformationException e) {
			e.printStackTrace();
			return null;
		}
		if (!locations.isEmpty()) {
			Location location = locations.get(0);
			return location;
		} 
		return null;
	}
	
	/**
	 * add junit relevant classes into VM configuration, i.e., launch the program with JUnit Launcher.
	 * @throws SavException
	 */
//	private final void setDebuggingConfiguration() throws SavException {
		
//		getVmConfig().setLaunchClass(JUNIT_RUNNER_CLASS_NAME);
//		JunitRunnerProgramArgBuilder builder = new JunitRunnerProgramArgBuilder();
//		List<String> args = 
//				builder.methods(allTests)
//				.testcaseTimeout(getTimeoutInSec(), TimeUnit.SECONDS)
//				.build();
//		getVmConfig().setProgramArgs(args);
//		getVmConfig().resetPort();
//	}

//	private long getTimeoutInSec() {
//		return timeout;
//	}

//	private TraceNode handleBreakpoint(BreakPoint bkp, ThreadReference thread, Location loc) throws SavException {
//		BreakPointValue bkpVal = extractValuesAtLocation(bkp, thread, loc);
//		TraceNode node = recordTrace(bkp, bkpVal);
//		
//		if(!this.methodNodeStack.isEmpty()){
//			TraceNode parentInvocationNode = this.methodNodeStack.peek();
//			parentInvocationNode.addInvocationChild(node);
//			node.setInvocationParent(parentInvocationNode);
//		}
//		
//		return node;
//	}
	
	private String generateVarID(StackFrame frame, Variable var, TraceNode node){
		String varName = var.getName();
		
		if(varName.equals("val$a")){
			System.currentTimeMillis();
		}
		
		try{
			ExpressionValue expValue = retriveExpression(frame, varName, node.getBreakPoint());
			
//			Field f = frame.thisObject().referenceType().allFields().get(0);
//			frame.thisObject().getValue(f);
			if(expValue != null){
				Value value = expValue.value;
				
				if(value instanceof ObjectReference){
					ObjectReference objRef = (ObjectReference)value;
					String varID = String.valueOf(objRef.uniqueID());
					
					var.setVarID(varID);
				}
				else{
					if(var instanceof LocalVar){
						LocalVariableScope scope = Settings.localVariableScopes.findScope(var.getName(), 
								node.getBreakPoint().getLineNo(), node.getBreakPoint().getDeclaringCompilationUnitName());
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
						
						if(var instanceof FieldVar){
							String varID = String.valueOf(objRef.uniqueID()) + "." + var.getSimpleName();
							var.setVarID(varID);							
						}
						else if(var instanceof ArrayElementVar){
							String index = var.getSimpleName();
							ExpressionValue indexValue = retriveExpression(frame, index, node.getBreakPoint());
							String indexValueString = indexValue.value.toString();
							String varID = String.valueOf(objRef.uniqueID()) + "[" + indexValueString + "]";
							
							var.setVarID(varID);
						}
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
	
	private void parseReadWrittenVariableInThisStep(ThreadReference thread, Location location, TraceNode node, 
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
	}

	private void processReadVariable(TraceNode node,
			Map<String, StepVariableRelationEntry> stepVariableTable,
			StackFrame frame) {
		
		List<Variable> readVariables = node.getBreakPoint().getReadVariables();
		for(Variable readVar: readVariables){
			String varID = generateVarID(frame, readVar, node);
			System.currentTimeMillis();
			if(varID == null){
				System.err.println("When processing read variable, there is an error when generating the id for " + readVar + 
						" in line " + node.getBreakPoint().getLineNo() + " of " + node.getBreakPoint().getClassCanonicalName());
				//varID = generateVarID(frame, readVar, node);
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
				System.err.println("When processing written variable, there is an error when generating the id for " + writtenVar + 
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
				
				if(node.getOrder() == 4){
					System.currentTimeMillis();
				}
				
				
				entry.addProducer(node);
			}
		}
	}
	
	
	private ExpressionValue retriveExpression(final StackFrame frame, String expression, BreakPoint point){
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
        	ExpressionParser.clear();
        	
        	CompilationUnit cu = JavaUtil.findCompilationUnitInProject(point.getClassCanonicalName());
        	ExpressionParser.setParameters(cu, point.getLineNo());
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

	private void collectValueOfPreviousStep(BreakPoint lastSteppingInPoint, 
			ThreadReference thread, Location loc) throws SavException {
		
		BreakPoint currnetPoint = new BreakPoint(lastSteppingInPoint.getClassCanonicalName(), 
				lastSteppingInPoint.getLineNo());
		
		BreakPointValue bkpVal = extractValuesAtLocation(currnetPoint, thread, loc);
		
		int len = trace.getExectionList().size();
		TraceNode node = trace.getExectionList().get(len-1);
		node.setAfterStepInState(bkpVal);
		
	}

	private TraceNode recordTrace(BreakPoint bkp, BreakPointValue bkpVal) {
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

	private BreakPointValue extractValuesAtLocation(BreakPoint bkp, ThreadReference thread, 
			Location loc) throws SavException {
		try {
			//return getValueExtractor().extractValue(bkp, bkpEvent);
			VariableValueExtractor extractor = new VariableValueExtractor(bkp, thread, loc);
			BreakPointValue bpValue = extractor.extractValue();
			return bpValue;
			
		} catch (IncompatibleThreadStateException e) {
			log.error(e.getMessage());
		} catch (AbsentInformationException e) {
			log.error(e.getMessage());
		}
		return null;
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
}

