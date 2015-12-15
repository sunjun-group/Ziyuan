/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LLT
 * This class allows to start a program in debug mode, and add breakpoints
 * in order to collect data.
 */
//@SuppressWarnings("restriction")
public class BreakpointDebugger {
	
	protected static Logger log = LoggerFactory.getLogger(BreakpointDebugger.class);
	
	
//	/**
//	 * fundamental fields for debugging
//	 */
//	private String[] excludes = { "java.*", "javax.*", "sun.*", "com.sun.*"};
//	private VMConfiguration config;
//	protected SimpleDebugger debugger = new SimpleDebugger();
//	/** map of classes and their breakpoints */
//	private Map<String, List<BreakPoint>> brkpsMap;
//	protected List<BreakPoint> bkps;
	
	
	
//	public void setup(VMConfiguration config) {
//		this.config = config;
//	}

//	public final void run(List<BreakPoint> brkps) throws SavException {
//		this.bkps = brkps;
//		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
//		/* before debugging */
//		beforeDebugging();
//		this.config.setDebug(true);
//		
//		/* start debugger */
//		VirtualMachine vm = debugger.run(config);
//		if (vm == null) {
//			throw new SavException(ModuleEnum.JVM, "cannot start jvm!");
//		}
//		
//		//System.out.println("This VM's classloader:" + vm.getClass().getClassLoader().getClass());
//		
//		/* add class watch */
//		EventRequestManager erm = vm.eventRequestManager(); 
//		addClassWatch(erm);
//		
//
//		/* process debug events */
//		EventQueue eventQueue = vm.eventQueue();
//		
//		boolean stop = false;
//		boolean eventTimeout = false;
//		Map<String, BreakPoint> locBrpMap = new HashMap<String, BreakPoint>();
//		
//		BreakPoint lastSteppingPoint = null;
//		
//		while (!stop && !eventTimeout) {
//			EventSet eventSet;
//			try {
//				eventSet = eventQueue.remove(3000);
//			} catch (InterruptedException e) {
//				// do nothing, just return.
//				break;
//			}
//			if (eventSet == null) {
//				log.warn("Time out! Cannot get event set!");
//				eventTimeout = true;
//				break;
//			}
//			for (Event event : eventSet) {
//				if(event instanceof VMStartEvent){
//					System.out.println("start threading");
//					/**
//					 * add step event
//					 */
//					StepRequest sr = erm.createStepRequest(((VMStartEvent) event).thread(), 
//							StepRequest.STEP_LINE, StepRequest.STEP_INTO);
//					sr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
//					
//					for(String ex: excludes){
//						sr.addClassExclusionFilter(ex);
//					}
////					sr.addClassFilter(refType);
//					sr.enable();
//					
//					/**
//					 * add method enter and exit event
//					 */
//					addMethodWatch(erm);
//				}
//				if (event instanceof VMDeathEvent
//						|| event instanceof VMDisconnectEvent) {
//					stop = true;
//					break;
//				} else if (event instanceof ClassPrepareEvent) {
//					// add breakpoint watch on loaded class
//					ClassPrepareEvent classPrepEvent = (ClassPrepareEvent) event;
//					handleClassPrepareEvent(vm, classPrepEvent);
//					/* add breakpoint request */
//					ReferenceType refType = classPrepEvent.referenceType();
//					// breakpoints
//					addBreakpointWatch(vm, refType, locBrpMap);
//					
//					
//				} else if (event instanceof BreakpointEvent) {
////					BreakpointEvent bkpEvent = (BreakpointEvent) event;
////					BreakPoint bkp = locBrpMap.get(bkpEvent.location().toString());
////					
////					if(bkp != null){
////						handleBreakpointEvent(bkp, vm, bkpEvent.thread(), bkpEvent.location());
////					}			
//				} else if(event instanceof StepEvent){
//					Location loc = ((StepEvent) event).location();
//					/**
//					 * collect the variable values after executing previous step
//					 */
//					if(lastSteppingPoint != null){
//						BreakPoint currnetPoint = new BreakPoint(lastSteppingPoint.getClassCanonicalName(), 
//								lastSteppingPoint.getLineNo());
//						onCollectValueOfPreviousStep(currnetPoint, ((StepEvent) event).thread(), loc);
//						lastSteppingPoint = null;
//					}
//					
//					BreakPoint bkp = locBrpMap.get(loc.toString());
//					if(bkp != null){
//						handleBreakpointEvent(bkp, vm, ((StepEvent) event).thread(), loc);
//						lastSteppingPoint = bkp;
//					}
//				} else if(event instanceof MethodEntryEvent){
//					MethodEntryEvent mee = (MethodEntryEvent)event;
//					//System.out.println("enter: " + mee.method().toString());
//				} else if (event instanceof MethodExitEvent){
//					MethodExitEvent mee = (MethodExitEvent)event;
//					//System.out.println("leave: " + mee.method().toString());
//				}
//			}
//			eventSet.resume();
//		}
////		if (!eventTimeout) {
////			vm.resume();
////			/* wait until the process completes */
////			debugger.waitProcessUntilStop();
////		}
//		/* end of debug */
//		afterDebugging();
//	}
	
//	private void addMethodWatch(EventRequestManager erm) {
//		MethodEntryRequest menr = erm.createMethodEntryRequest();
//		for(String classPattern: excludes){
//			menr.addClassExclusionFilter(classPattern);
//		}
//		menr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
//		menr.enable();
//		
//		MethodExitRequest mexr = erm.createMethodExitRequest();
//		for(String classPattern: excludes){
//			mexr.addClassExclusionFilter(classPattern);
//		}
//		mexr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
//		mexr.enable();
//	}

	/** abstract methods */
//	protected abstract void beforeDebugging() throws SavException;
//	protected abstract void handleClassPrepareEvent(VirtualMachine vm, ClassPrepareEvent event);
//	protected abstract void handleBreakpointEvent(BreakPoint bkp, VirtualMachine vm, 
//			ThreadReference thread, Location loc) throws SavException;
//	protected abstract void afterDebugging() throws SavException ;
//
//	protected abstract void onCollectValueOfPreviousStep(BreakPoint currentPosition, 
//			ThreadReference thread, Location loc) throws SavException;

//	/** add watch requests **/
//	private void addClassWatch(EventRequestManager erm) {
//		/* add class watch for breakpoints */
//		for (String className : brkpsMap.keySet()) {
//			addClassWatch(erm, className);
//		}
//	}
//
//	private final void addClassWatch(EventRequestManager erm, String className) {
//		ClassPrepareRequest classPrepareRequest = erm
//				.createClassPrepareRequest();
//		classPrepareRequest.addClassFilter(className);
//		classPrepareRequest.setEnabled(true);
//	}
//	
//	private void addBreakpointWatch(VirtualMachine vm, ReferenceType refType,
//			Map<String, BreakPoint> locBrpMap) {
//		List<BreakPoint> brkpList = CollectionUtils.initIfEmpty(brkpsMap.get(refType.name()));
//		for (BreakPoint brkp : brkpList) {
//			Location location = addBreakpointWatch(vm, refType, brkp.getLineNo());
//			if (location != null) {
//				locBrpMap.put(location.toString(), brkp);
//			} else {
//				log.warn("Cannot add break point " + brkp);
//			}
//		}
//	}
//	
//	private final Location addBreakpointWatch(VirtualMachine vm,
//			ReferenceType refType, int lineNumber) {
//		List<Location> locations;
//		try {
//			locations = refType.locationsOfLine(lineNumber);
//		} catch (AbsentInformationException e) {
//			log.warn(e.getMessage());
//			return null;
//		}
//		if (!locations.isEmpty()) {
//			Location location = locations.get(0);
//			BreakpointRequest breakpointRequest = vm.eventRequestManager()
//					.createBreakpointRequest(location);
//			breakpointRequest.setEnabled(true);
//			return location;
//		} 
//		return null;
//	}
//	
//	public String getProccessError() {
//		return debugger.getProccessError();
//	}
//	
//	public VMConfiguration getVmConfig() {
//		return config;
//	}
}
