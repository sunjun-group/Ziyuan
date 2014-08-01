/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.Variable;
import icsetlv.common.dto.VariablesExtractorResult;
import icsetlv.common.dto.VariablesExtractorResult.BreakpointResult;
import icsetlv.common.dto.VariablesExtractorResult.VarValue;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.BreakpointUtils;
import icsetlv.common.utils.VariableUtils;
import icsetlv.vm.VMAcquirer;
import icsetlv.vm.VMConfiguration;
import icsetlv.vm.VMRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
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
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;

/**
 * @author LLT
 * 
 */
public class VariablesExtractor {
	private VMConfiguration config;
	private Map<String, List<BreakPoint>> brkpsMap;
	private List<String> passTcs;
	private List<String> failTcs;

	public VariablesExtractor(VMConfiguration config,
			List<String> passTestcases, List<String> failTestcases,
			List<BreakPoint> brkps) {
		this.config = config;
		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		this.passTcs = passTestcases;
		this.failTcs = failTestcases;
	}

	public VariablesExtractorResult execute() throws IcsetlvException {
		Map<String, BreakPoint> locBrpMap = new HashMap<String, BreakPoint>();
		Map<String, BreakpointResult> valuesMap = new HashMap<String, BreakpointResult>();
		executeJunitTests(locBrpMap, valuesMap, true);
		executeJunitTests(locBrpMap, valuesMap, false);
		return new VariablesExtractorResult(valuesMap.values());
	}


	private VariablesExtractorResult executeJunitTests(
			Map<String, BreakPoint> locBrpMap, Map<String, BreakpointResult> valuesMap,
			boolean forPassTcs) throws IcsetlvException {
		if (forPassTcs) {
			config.setArgs(passTcs);
		} else {
			config.setArgs(failTcs);
		}
		// start jvm in debug mode
		VMRunner.startJVM(config);
		
		// remote debug
		VirtualMachine vm;
		vm = new VMAcquirer().connect(config.getPort());
		addClassWatch(vm);
		// process events
		EventQueue eventQueue = vm.eventQueue();
		boolean stop = false;
		while (!stop) {
			EventSet eventSet;
			try {
				eventSet = eventQueue.remove(1000);
			} catch (InterruptedException e) {
				// do nothing, just return.
				break;
			}
			if (eventSet == null) {
				break;
				// TODO LLT: try to get event from queue again until time
				// out is reached.
			}
			for (Event event : eventSet) {
				if (event instanceof VMDeathEvent
						|| event instanceof VMDisconnectEvent) {
					stop = true;
					break;
				} else if (event instanceof ClassPrepareEvent) {
					// watch field on loaded class
					ClassPrepareEvent classPrepEvent = (ClassPrepareEvent) event;
					ReferenceType refType = classPrepEvent.referenceType();
					// breakpoint
					addBreakpointWatch(vm, refType, locBrpMap);
				} else if (event instanceof BreakpointEvent) {
					BreakpointEvent bkpEvent = (BreakpointEvent) event;
					try {
						extractValuesAtLocation(bkpEvent.location(), bkpEvent,
								locBrpMap, valuesMap, forPassTcs);
					} catch (IncompatibleThreadStateException e) {
						// TODO LLT log
					} catch (AbsentInformationException e) {
						// TODO LLT log
					}
				}
			}
			eventSet.resume();
		}
		vm.resume();
		return new VariablesExtractorResult(valuesMap.values());
	}

	private BreakpointResult extractValuesAtLocation(Location location, LocatableEvent event,
			Map<String, BreakPoint> locBrpMap,
			Map<String, BreakpointResult> valuesMap, boolean arePassTcs)
			throws IncompatibleThreadStateException,
			AbsentInformationException, IcsetlvException {
		List<VarValue> result = new ArrayList<VariablesExtractorResult.VarValue>();
		BreakPoint brp = locBrpMap.get(location.toString());
		if (brp == null) {
			return null;
		}
		
		ThreadReference thread = event.thread();
		synchronized (thread) {
			if (!thread.frames().isEmpty()) {
				StackFrame frame = thread.frames().get(0);
				Method method = frame.location().method();
				ReferenceType refType;
				ObjectReference objRef = null;
				if (method.isStatic()) {
					refType = method.declaringType();
				} else {
					objRef = frame.thisObject();
					refType = objRef.referenceType();
				}
				// static fields & non static field
				for (Field field : refType.allFields()) {
					Variable inVar = VariableUtils.lookupVarInBreakpoint(field,
							brp);
					if (inVar != null) {
						if (field.isStatic()) {
							appendVarVal(result, inVar,
									refType.getValue(field));
						} else if (objRef != null) {
							appendVarVal(result, inVar,
									objRef.getValue(field));
						}
					}
				}
				for (LocalVariable var : frame.visibleVariables()) {
					Variable inVar = VariableUtils.lookupVarInBreakpoint(var,
							brp);
					if (inVar != null) {
						appendVarVal(result, inVar, frame.getValue(var));
					}
				}
			}
		}
		
		BreakpointResult bkpResult = valuesMap.get(location.toString());
		if (bkpResult == null) {
			bkpResult = new BreakpointResult(brp);
			valuesMap.put(location.toString(), bkpResult);
		}
		bkpResult.add(result, arePassTcs);
		return bkpResult;
	}

	private void appendVarVal(List<VarValue> varValList, Variable inVar,
			Value value) {
		VarValue varValue = new VarValue(inVar.getCode(), value.toString());
		varValList.add(varValue);
	}

	private void addBreakpointWatch(VirtualMachine vm, ReferenceType refType,
			Map<String, BreakPoint> locBrpMap) {
		EventRequestManager erm = vm.eventRequestManager();
		for (BreakPoint brkp : brkpsMap.get(refType.name())) {
			List<Location> locations;
			try {
				locations = refType
						.locationsOfLine(brkp.getLineNo());
			} catch (AbsentInformationException e) {
				// TODO LLT: log
				continue;
			}
			if (!locations.isEmpty()) {
				Location location = locations.get(0);
				BreakpointRequest breakpointRequest = erm
						.createBreakpointRequest(location);
				breakpointRequest.setEnabled(true);
				locBrpMap.put(location.toString(), brkp);
			}
		}
	}

	private void addClassWatch(VirtualMachine vm) {
		EventRequestManager erm = vm.eventRequestManager();
		for (String className : brkpsMap.keySet()) {
			registerClassRequest(erm, className);
		}
	}

	private void registerClassRequest(EventRequestManager erm, String className) {
		ClassPrepareRequest classPrepareRequest;
		classPrepareRequest = erm
				.createClassPrepareRequest();
		classPrepareRequest.addClassFilter(className);
		classPrepareRequest.setEnabled(true);
	}
	
}
