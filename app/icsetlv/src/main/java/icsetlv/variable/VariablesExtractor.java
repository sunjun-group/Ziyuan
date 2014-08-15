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
import icsetlv.iface.IVariableExtractor;
import icsetlv.vm.SimpleDebugger;
import icsetlv.vm.VMConfiguration;

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
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
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
public class VariablesExtractor implements IVariableExtractor {
	private VMConfiguration config;
	private Map<String, List<BreakPoint>> brkpsMap;
	private List<String> passTcs;
	private List<String> failTcs;
	private SimpleDebugger debugger;

	public VariablesExtractor(VMConfiguration config) {
		this.config = config;
		debugger = new SimpleDebugger();
	}

	@Override
	public VariablesExtractorResult execute(List<String> passTestcases,
			List<String> failTestcases, List<BreakPoint> brkps)
			throws IcsetlvException {
		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		this.passTcs = passTestcases;
		this.failTcs = failTestcases;
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
			config.setProgramArgs(passTcs);
		} else {
			config.setProgramArgs(failTcs);
		}
		
		VirtualMachine vm = debugger.run(config);
		
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
						System.err.println(e);
						// TODO LLT log
					} catch (AbsentInformationException e) {
						System.err.println(e);
						// TODO LLT log
					}
				}
			}
			eventSet.resume();
		}
		vm.resume();
		return new VariablesExtractorResult(valuesMap.values());
	}

	private void extractValuesAtLocation(Location location, LocatableEvent event,
			Map<String, BreakPoint> locBrpMap,
			Map<String, BreakpointResult> valuesMap, boolean arePassTcs)
			throws IncompatibleThreadStateException,
			AbsentInformationException, IcsetlvException {
		List<VarValue> result = new ArrayList<VariablesExtractorResult.VarValue>();
		BreakPoint brp = locBrpMap.get(location.toString());
		if (brp == null) {
			return;
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
		
		if (!result.isEmpty()) {
			BreakpointResult bkpResult = valuesMap.get(location.toString());
			if (bkpResult == null) {
				bkpResult = new BreakpointResult(brp);
				valuesMap.put(location.toString(), bkpResult);
			}
			bkpResult.add(result, arePassTcs);
		}
	}

	private void appendVarVal(List<VarValue> varValList, Variable inVar,
			Value value) {
		Type type = value.type();
		if (isPrimitiveOrString(type)) {
			VarValue varValue = new VarValue(inVar.getCode(), value.toString());
			varValList.add(varValue);
		}
	}

	private boolean isPrimitiveOrString(Type type) {
		if (type instanceof PrimitiveType) {
			return true;
		}
//		if (type instanceof ReferenceType) {
//			ReferenceType refType = (ReferenceType) type;
//			return refType.name().equals(String.class.getName());
//		}
		return false;
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
