/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.dto.ArrayValue;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.PrimitiveValue;
import icsetlv.common.dto.ReferenceValue;
import icsetlv.common.dto.TcExecResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.PrimitiveUtils;
import icsetlv.vm.SimpleDebugger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import sav.common.core.Logger;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunner.JunitRunnerProgramArgBuilder;
import sav.strategies.vm.VMConfiguration;

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
import com.sun.jdi.StringReference;
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
public class TestcasesExecutor {
	private static final Logger<?> LOGGER = Logger.getDefaultLogger();
	private static final String JUNIT_RUNNER_CLASS_NAME = JunitRunner.class.getName();
	private static final String TO_STRING_SIGN= "()Ljava/lang/String;";
	private static final String TO_STRING_NAME= "toString";
	private static final Pattern OBJECT_ACCESS_PATTERN = Pattern.compile("^\\.(.+)(.*)$");
	private static final Pattern ARRAY_ACCESS_PATTERN = Pattern.compile("^\\[(\\d+)\\](.*)$");
	
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

	public TcExecResult execute(List<String> allTests,
			List<BreakPoint> brkps) throws IcsetlvException, SavException {
		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		Map<String, BreakPoint> locBrpMap = new HashMap<String, BreakPoint>();
		Map<Boolean, List<BreakpointValue>> resultMap = executeJunitTests(locBrpMap, allTests);
		return new TcExecResult(CollectionUtils.nullToEmpty(resultMap.get(true)), 
				CollectionUtils.nullToEmpty(resultMap.get(false)));
	}

	private Map<Boolean, List<BreakpointValue>> executeJunitTests(
			Map<String, BreakPoint> locBrpMap, List<String> tcs) throws IcsetlvException, SavException {
		String jResultFile = createExecutionResultFile();
		Map<Integer, List<BreakpointValue>> bkpValsByTestIdx = new HashMap<Integer, List<BreakpointValue>>();
		List<BreakpointValue> currentTestBkpValues = new ArrayList<BreakpointValue>();
		config.setLaunchClass(JunitRunner.class.getName());
		List<String> args = new JunitRunnerProgramArgBuilder().methods(tcs).destinationFile(jResultFile)
											.build();
		config.setProgramArgs(args);
		VirtualMachine vm = debugger.run(config);
		if (vm == null) {
			throw new IcsetlvException("cannot start jvm!");
		}
		addClassWatch(vm);
		addClassWatch(vm.eventRequestManager(), JUNIT_RUNNER_CLASS_NAME);
		
		// process events
		EventQueue eventQueue = vm.eventQueue();
		boolean stop = false;
		int testIdx = 0;
		Location junitLoc = null;
		while (!stop) {
			EventSet eventSet;
			try {
				eventSet = eventQueue.remove(1000);
			} catch (InterruptedException e) {
				LOGGER.debug(e);
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
					if (refType.name().equals(JUNIT_RUNNER_CLASS_NAME)) {
						// junitRunner breakpoint
						junitLoc = addBreakpointWatch(vm, refType,
								JunitRunner.TESTCASE_PROCESS_START_LINE_NO);
					} else {
						// breakpoints
						addBreakpointWatch(vm, refType, locBrpMap);
					}
				} else if (event instanceof BreakpointEvent) {
					BreakpointEvent bkpEvent = (BreakpointEvent) event;
					try {
						if (areLocationsEqual(bkpEvent.location(), junitLoc)) {
							currentTestBkpValues = CollectionUtils.getListInitIfEmpty(bkpValsByTestIdx, testIdx++);
							continue;
						}
						BreakpointValue bkpVal = extractValuesAtLocation(bkpEvent.location(), bkpEvent,
								locBrpMap);
						CollectionUtils.addIfNotNull(currentTestBkpValues, bkpVal);
					} catch (IncompatibleThreadStateException e) {
						LOGGER.error(e);
					} catch (AbsentInformationException e) {
						LOGGER.error(e);
					}
				}
			}
			eventSet.resume();
		}
		vm.resume();
		try {
			debugger.waitProcessUntilStop();
			JunitResult jResult = JunitResult.readFrom(jResultFile);
			Map<Boolean, List<BreakpointValue>> resultMap = new HashMap<Boolean, List<BreakpointValue>>();
			Map<String, Boolean> tcExResult = jResult.getResult(tcs);
			for (int i = 0; i < tcs.size(); i++) {
				CollectionUtils.getListInitIfEmpty(resultMap, tcExResult.get(tcs.get(i)))
						.addAll(bkpValsByTestIdx.get(i));
			}
			return resultMap;
		} catch (IOException e) {
			throw new SavException(ModuleEnum.JVM, "cannot read junitResult in temp file");
		}
	}

	private String createExecutionResultFile() throws SavException {
		try {
			return File.createTempFile("tcsExResult", ".txt").getAbsolutePath();
		} catch (IOException e1) {
			throw new SavException(ModuleEnum.JVM, "cannot create temp file");
		}
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
				StackFrame frame = findFrameByLocation(thread.frames(), location);
				Method method = frame.location().method();
				ReferenceType refType;
				ObjectReference objRef = null;
				if (method.isStatic()) {
					refType = method.declaringType();
				} else {
					objRef = frame.thisObject();
					refType = objRef.referenceType();
				}
				/*
				 * LOCALVARIABLES MUST BE NAVIGATED BEFORE FIELDS, because: in
				 * case a class field and a local variable in method have the
				 * same name, and the breakpoint variable with that name has the
				 * scope UNDEFINED, it must be the variable in the method.
				 */
				final Map<Variable, Value> allVariables = new HashMap<Variable, Value>();
				final List<LocalVariable> visibleVars = frame.visibleVariables();
				final List<Field> allFields = refType.allFields();
				for (Variable bpVar : bkp.getVars()) {
					// First check local variable
					LocalVariable match = null;
					for (LocalVariable localVar : visibleVars) {
						if (localVar.name().equals(bpVar.getName())) {
							match = localVar;
							break;
						}
					}

					if (match != null) {
						allVariables.put(bpVar, lookup(frame, match, bpVar.getFullName()));
					} else {
						// Then check class fields (static & non static)
						Field matchedField = null;
						for (Field field : allFields) {
							if (field.name().equals(bpVar.getName())) {
								matchedField = field;
								break;
							}
						}

						if (matchedField != null) {
							final Value value = matchedField.isStatic() ? refType
									.getValue(matchedField) : objRef != null ? objRef
									.getValue(matchedField) : null;
							if (value != null) {
								if (matchedField.name().equals(bpVar.getFullName())) {
									allVariables.put(bpVar, value);
								} else {
									allVariables.put(bpVar,
											lookup(value, extractSubProperty(bpVar.getFullName())));
								}
							}
						}
					}
				}

				if (!allVariables.isEmpty()) {
					for (Entry<Variable, Value> entry : allVariables.entrySet()) {
						Variable var = entry.getKey();
						String varId = var.getId();
						appendVarVal(bkVal, varId, entry.getValue(), 1, thread);
					}
				}
			}
		}
		return bkVal;
	}

	private Value lookup(final StackFrame frame, final LocalVariable match, final String fullName) {
		final Value value = frame.getValue(match);
		if (!match.name().equals(fullName)) {
			return lookup(value, extractSubProperty(fullName));
		}
		return value;
	}
	
	private String extractSubProperty(final String fullName) {
		int objIndex = fullName.indexOf(".");
		int arrIndex = fullName.indexOf("[");
		int index = objIndex < arrIndex || arrIndex < 0 ? objIndex : arrIndex;
		return fullName.substring(index);
	}

	private Value lookup(final Value value, final String property) {
		if (StringUtils.isBlank(property)) {
			return value;
		}
		Value subValue = value;
		String subProperty = null;
		// NOTE: must check Array before Object because ArrayReferenceImpl
		// implements both ArrayReference and ObjectReference (by extending
		// ObjectReferenceImpl)
		if (ArrayReference.class.isAssignableFrom(value.getClass())) {
			ArrayReference array = (ArrayReference) value;
			// Can access to the array's length or values
			if (".length".equals(property)) {
				subValue = array.virtualMachine().mirrorOf(array.length());
				// No sub property is available after this
			} else {
				final Matcher matcher = ARRAY_ACCESS_PATTERN.matcher(property);
				if (matcher.matches()) {
					int index = Integer.valueOf(matcher.group(1));
					subValue = array.getValue(index);
					// After this we can have access to another dimension of the
					// array or access to the retrieved object's property
					subProperty = matcher.group(2);
				}
			}
		} else if (ObjectReference.class.isAssignableFrom(value.getClass())) {
			ObjectReference object = (ObjectReference) value;
			final Matcher matcher = OBJECT_ACCESS_PATTERN.matcher(property);
			if (matcher.matches()) {
				final String propertyName = matcher.group(1);
				Field propertyField = null;
				for (Field field : object.referenceType().allFields()) {
					if (field.name().equals(propertyName)) {
						propertyField = field;
						break;
					}
				}
				if (propertyField != null) {
					subValue = object.getValue(propertyField);
					subProperty = matcher.group(2);
				}
			}
		}
		return lookup(subValue, subProperty);
	}

	private StackFrame findFrameByLocation(List<StackFrame> frames,
			Location location) throws AbsentInformationException {
		for (StackFrame frame : frames) {
			if (areLocationsEqual(frame.location(), location)) {
				return frame;
			}
		}
		throw new SavRtException("Can not find frame");
	}
	
	private boolean areLocationsEqual(Location location1, Location location2) throws AbsentInformationException {
		return location1.compareTo(location2) == 0;
	}

	private void appendVarVal(ExecValue parent, String varId,
			Value value, int level, ThreadReference thread) {
		if (level == valRetrieveLevel || varId.endsWith("serialVersionUID")
				|| value == null) {
			return;
		}
		level++;
		Type type = value.type();
		if (type instanceof PrimitiveType) {
			parent.add(new PrimitiveValue(varId, value.toString()));
		} else if (type instanceof ClassType && PrimitiveUtils.isPrimitiveTypeOrString(type.name())) {
			parent.add(new PrimitiveValue(varId, toPrimitiveValue((ClassType) type, (ObjectReference)value, thread)));
		} else if (type instanceof ArrayType) {
			appendArrVarVal(parent, varId, (ArrayReference)value, level, thread);
		} else if (type instanceof ClassType) {
			appendClassVarVal(parent, varId, (ObjectReference) value, level, thread);
		}
	}

	private synchronized String toPrimitiveValue(ClassType type, ObjectReference value,
			ThreadReference thread) {
		Method method = type.concreteMethodByName(TO_STRING_NAME,
				TO_STRING_SIGN);
		if (method != null) {
			try {
				if (thread.isSuspended()) {
					if (value instanceof StringReference) {
						return ((StringReference) value).value();
					}
					Value toStringValue = value.invokeMethod(thread, method,
							new ArrayList<Value>(),
							ObjectReference.INVOKE_SINGLE_THREADED);
					return toStringValue.toString();
					
				}
			} catch (Exception e) {
				// ignore.
				LOGGER.warn((Object[])e.getStackTrace());
			}
		}
		return null;
	}

	private void appendClassVarVal(ExecValue parent, String varId,
			ObjectReference value, int level, ThreadReference thread) {
		ReferenceValue val = new ReferenceValue(varId);
		ClassType type = (ClassType) value.type();
		for (Field field : type.allFields()) {
			appendVarVal(val, val.getChildId(field.name()),
					value.getValue(field), level, thread);
		}
		parent.add(val);
	}

	private void appendArrVarVal(ExecValue parent, String varId,
			ArrayReference value, int level, ThreadReference thread) {
		ArrayValue val = new ArrayValue(varId);
		val.setValue(value);
		
//		//add value of elements
//		for (int i = 0; i < value.length(); i++) {
//			appendVarVal(val, val.getChildId(i), value.getValue(i), level, thread);
//		}
		parent.add(val);
	}

	private void addBreakpointWatch(VirtualMachine vm, ReferenceType refType,
			Map<String, BreakPoint> locBrpMap) {
		for (BreakPoint brkp : brkpsMap.get(refType.name())) {
			Location location = addBreakpointWatch(vm, refType, brkp.getLineNo());
			if (location != null) {
				locBrpMap.put(location.toString(), brkp);
			} else {
				LOGGER.warn("Cannot add break point " + brkp);
			}
		}
	}
	
	private Location addBreakpointWatch(VirtualMachine vm,
			ReferenceType refType, int lineNumber) {
		// We assume that it is always possible to add the break point
		List<Location> locations;
		try {
			locations = refType.locationsOfLine(lineNumber);
		} catch (AbsentInformationException e) {
			LOGGER.warn((Object[]) e.getStackTrace());
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
	
	private void addClassWatch(VirtualMachine vm) {
		EventRequestManager erm = vm.eventRequestManager();
		for (String className : brkpsMap.keySet()) {
			addClassWatch(erm, className);
		}
	}

	private void addClassWatch(EventRequestManager erm, String className) {
		ClassPrepareRequest classPrepareRequest = erm.createClassPrepareRequest();
		classPrepareRequest.addClassFilter(className);
		classPrepareRequest.setEnabled(true);
	}

}

