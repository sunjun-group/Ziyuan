/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.dto.ArrayValue;
import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.Variable;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.PrimitiveValue;
import icsetlv.common.dto.ReferenceValue;
import icsetlv.common.dto.TcExecResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.BreakpointUtils;
import icsetlv.common.utils.VariableUtils;
import icsetlv.iface.ITestcasesExecutor;
import icsetlv.vm.SimpleDebugger;
import icsetlv.vm.VMConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassType;
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
public class TestcasesExecutor implements ITestcasesExecutor {
	private SimpleDebugger debugger;
	private VMConfiguration config;
	// class and its breakpoints
	private Map<String, List<BreakPoint>> brkpsMap;
	private int valRetrieveLevel;

	public TestcasesExecutor(VMConfiguration config, int valRetrieveLevel) {
		this.config = config;
		debugger = new SimpleDebugger();
		this.valRetrieveLevel = valRetrieveLevel;
	}

	@Override
	public TcExecResult execute(List<String> passTestcases,
			List<String> failTestcases, List<BreakPoint> brkps)
			throws IcsetlvException {
		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		Map<String, BreakPoint> locBrpMap = new HashMap<String, BreakPoint>();
		List<BreakpointValue> passVals = executeJunitTests(locBrpMap, passTestcases);
		List<BreakpointValue> failVals = executeJunitTests(locBrpMap, failTestcases);
		return new TcExecResult(passVals, failVals);
	}

	private List<BreakpointValue> executeJunitTests(
			Map<String, BreakPoint> locBrpMap, List<String> tcs) throws IcsetlvException {
		List<BreakpointValue> result = new ArrayList<BreakpointValue>();
		config.setProgramArgs(tcs);
		VirtualMachine vm = debugger.run(config);
		if (vm == null) {
			throw new IcsetlvException("cannot start jvm!");
		}
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
						BreakpointValue bkpVal = extractValuesAtLocation(bkpEvent.location(), bkpEvent,
								locBrpMap);
						CollectionUtils.addIfNotNull(result, bkpVal);
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
		return result;
	}

	private BreakpointValue extractValuesAtLocation(Location location, LocatableEvent event,
			Map<String, BreakPoint> locBrpMap)
			throws IncompatibleThreadStateException,
			AbsentInformationException, IcsetlvException {
		BreakPoint bkp = locBrpMap.get(location.toString());
		if (bkp == null) {
			return null;
		}
		
		BreakpointValue bkVal = new BreakpointValue(bkp.getId());
		ThreadReference thread = event.thread();
		synchronized (thread) {
			if (!thread.frames().isEmpty()) {
				// LLT: get correct frame, not the first one.
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
				// class fields (static & non static)
				for (Field field : refType.allFields()) {
					Variable inVar = VariableUtils.lookupVarInBreakpoint(field,
							bkp);
					if (inVar != null) {
						if (field.isStatic()) {
							appendVarVal(bkVal, inVar.getCode(), refType.getValue(field), 1);
						} else if (objRef != null) {
							appendVarVal(bkVal, inVar.getCode(), objRef.getValue(field), 1);
						}
					}
				}
				// local variables (includes method args)
				for (LocalVariable var : frame.visibleVariables()) {
					Variable inVar = VariableUtils.lookupVarInBreakpoint(var,
							bkp);
					if (inVar != null) {
						appendVarVal(bkVal, inVar.getCode(), frame.getValue(var), 1);
					}
				}
			}
		}
		return bkVal;
	}
	
	private void appendVarVal(ExecValue parent, String varId,
			Value value, int level) {
		if (level == valRetrieveLevel || varId.endsWith("serialVersionUID")) {
			return;
		}
		level++;
		Type type = value.type();
		if (isPrimitiveOrString(type)) {
			parent.add(new PrimitiveValue(varId, value.toString()));
		} else if (type instanceof ArrayType) {
			appendArrVarVal(parent, varId, (ArrayReference)value, level);
		} else if (type instanceof ClassType) {
			appendClassVarVal(parent, varId, (ObjectReference) value, level);
		}
	}

	private void appendClassVarVal(ExecValue parent, String varId,
			ObjectReference value, int level) {
		ReferenceValue val = new ReferenceValue(varId);
		ClassType type = (ClassType) value.type();
		for (Field field : type.allFields()) {
			appendVarVal(val, val.getChildId(field.name()),
					value.getValue(field), level);
		}
		parent.add(val);
	}

	private void appendArrVarVal(ExecValue parent, String varId,
			ArrayReference value, int level) {
		ArrayValue val = new ArrayValue(varId);
		val.setLength(value.length());
		for (int i = 0; i < value.length(); i++) {
			appendVarVal(val, val.getChildId(i), value, level);
		}
		parent.add(val);
	}

	private boolean isPrimitiveOrString(Type type) {
		if (type instanceof PrimitiveType) {
			return true;
		}
		if (type instanceof ReferenceType) {
			ReferenceType refType = (ReferenceType) type;
			return refType.name().equals(String.class.getName());
		}
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
				System.err.println(e);
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
