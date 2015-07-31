/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.DefaultValues;
import icsetlv.common.dto.ArrayValue;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.PrimitiveValue;
import icsetlv.common.dto.ReferenceValue;
import icsetlv.common.dto.StringValue;
import icsetlv.common.utils.PrimitiveUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import sav.common.core.Logger;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.BooleanType;
import com.sun.jdi.BooleanValue;
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
import com.sun.jdi.event.BreakpointEvent;

/**
 * @author LLT
 *
 */
public class DebugValueExtractor {
	private static final Logger<?> LOGGER = Logger.getDefaultLogger();
	private static final String TO_STRING_SIGN= "()Ljava/lang/String;";
	private static final String TO_STRING_NAME= "toString";
	private static final Pattern OBJECT_ACCESS_PATTERN = Pattern.compile("^\\.([^.\\[]+)(\\..+)*(\\[.+)*$");
	private static final Pattern ARRAY_ACCESS_PATTERN = Pattern.compile("^\\[(\\d+)\\](.*)$");

	private int valRetrieveLevel;
	
	public DebugValueExtractor() {
		this.valRetrieveLevel = DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL;
	}
	
	public DebugValueExtractor(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
	}
	
	public final BreakpointValue extractValue(BreakPoint bkp, BreakpointEvent event)
			throws IncompatibleThreadStateException, AbsentInformationException, SavException {
		if (bkp == null) {
			return null;
		}
		
		BreakpointValue bkVal = new BreakpointValue(bkp.getId());
		ThreadReference thread = event.thread();
		synchronized (thread) {
			if (!thread.frames().isEmpty()) {
				StackFrame frame = findFrameByLocation(thread.frames(), event.location());
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
				final Map<Variable, JdiParam> allVariables = new HashMap<Variable, JdiParam>();
				final List<LocalVariable> visibleVars = frame.visibleVariables();
				final List<Field> allFields = refType.allFields();
				for (Variable bpVar : bkp.getVars()) {
					// First check local variable
					LocalVariable match = null;
					if (bpVar.getScope() != VarScope.THIS) {
						for (LocalVariable localVar : visibleVars) {
							if (localVar.name().equals(bpVar.getParentName())) {
								match = localVar;
								break;
							}
						}
					}
					
					JdiParam param = null;
					if (match != null) {
						param = recursiveMatch(frame, match, bpVar.getFullName());
					} else {
						// Then check class fields (static & non static)
						Field matchedField = null;
						for (Field field : allFields) {
							if (field.name().equals(bpVar.getParentName())) {
								matchedField = field;
								break;
							}
						}

						if (matchedField != null) {
							if (matchedField.isStatic()) {
								param = JdiParam.staticField(matchedField, refType, refType.getValue(matchedField));
							} else {
								param = JdiParam.nonStaticField(matchedField, objRef, objRef.getValue(matchedField));
							}
							if (param.getValue() != null && !matchedField.name().equals(bpVar.getFullName())) {
								param = recursiveMatch(param, extractSubProperty(bpVar.getFullName()));
							}
						}
					}
					if (param != null) {
						allVariables.put(bpVar, param);
					}
				}

				if (!allVariables.isEmpty()) {
					collectValue(bkVal, thread, allVariables);
				}
			}
		}
		return bkVal;
	}

	protected void collectValue(BreakpointValue bkVal, ThreadReference thread,
			final Map<Variable, JdiParam> allVariables) throws SavException {
		for (Entry<Variable, JdiParam> entry : allVariables.entrySet()) {
			Variable var = entry.getKey();
			String varId = var.getId();
			appendVarVal(bkVal, varId, entry.getValue().getValue(), 1, thread);
		}
	}
	
	protected String extractSubProperty(final String fullName) {
		// obj idx
		int idx = fullName.indexOf(".");
		int arrIndex = fullName.indexOf("[");
		if ((idx < 0) || (arrIndex >= 0 && arrIndex < idx)) {
			idx = arrIndex;
		}  
		if (idx >= 0) {
			return fullName.substring(idx);
		}
		return fullName;
	}
	
	protected JdiParam recursiveMatch(final StackFrame frame, final LocalVariable match, final String fullName) {
		Value value = frame.getValue(match);
		JdiParam param = JdiParam.localVariable(match, value);
		if (!match.name().equals(fullName)) {
			return recursiveMatch(param , extractSubProperty(fullName));
		}
		return param;
	}
	
	protected JdiParam recursiveMatch(JdiParam param, final String property) {
		if (StringUtils.isBlank(property)) {
			return param;
		}
		Value value = param.getValue();
		if (value == null) {
			// cannot get property for a null object
			return null;
		}
		JdiParam subParam = null;
		String subProperty = null;
		// NOTE: must check Array before Object because ArrayReferenceImpl
		// implements both ArrayReference and ObjectReference (by extending
		// ObjectReferenceImpl)
		if (ArrayReference.class.isAssignableFrom(value.getClass())) {
			ArrayReference array = (ArrayReference) value;
			// Can access to the array's length or values
			if (".length".equals(property)) {
				subParam = JdiParam.nonStaticField(null, array, array.virtualMachine().mirrorOf(array.length()));
				// No sub property is available after this
			} else {
				final Matcher matcher = ARRAY_ACCESS_PATTERN.matcher(property);
				if (matcher.matches()) {
					int index = Integer.valueOf(matcher.group(1));
					subParam = JdiParam.arrayElement(array, index, array.getValue(index)); 
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
					subParam = JdiParam.nonStaticField(propertyField, object, object.getValue(propertyField));
					subProperty = matcher.group(2);
					if (sav.common.core.utils.StringUtils.isEmpty(subProperty)) {
						subProperty = matcher.group(3);
					}
				}
			}
		}
		return recursiveMatch(subParam, subProperty);
	}

	/** append execution value*/
	private void appendVarVal(ExecValue parent, String varId,
			Value value, int level, ThreadReference thread) {
		if (level >= valRetrieveLevel || varId.endsWith("serialVersionUID")) {
			return;
		}
		if (value == null) {
			appendNullVarVal(parent, varId);
			return;
		}
		level++;
		Type type = value.type();
		if (type instanceof PrimitiveType) {
			/* TODO LLT: add Primitive type && refactor */
			if (type instanceof BooleanType) {
				parent.add(icsetlv.common.dto.BooleanValue.of(varId, ((BooleanValue)value).booleanValue()));
			} else {
				parent.add(new PrimitiveValue(varId, value.toString()));
			}
		} else if (type instanceof ArrayType) {
			appendArrVarVal(parent, varId, (ArrayReference)value, level, thread);
		} else if (type instanceof ClassType) {
			if (PrimitiveUtils.isString(type.name())) {
				parent.add(new StringValue(varId, toPrimitiveValue((ClassType) type, (ObjectReference)value, thread)));
			} else if (PrimitiveUtils.isPrimitiveType(type.name())) {
				parent.add(new PrimitiveValue(varId, toPrimitiveValue((ClassType) type, (ObjectReference)value, thread)));
			} else {
				appendClassVarVal(parent, varId, (ObjectReference) value, level, thread);
			}
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
	
	private void appendNullVarVal(ExecValue parent, String varId) {
		ReferenceValue val = ReferenceValue.nullValue(varId);
		parent.add(val);
	}

	private void appendClassVarVal(ExecValue parent, String varId,
			ObjectReference value, int level, ThreadReference thread) {
		ReferenceValue val = new ReferenceValue(varId, false);
		ClassType type = (ClassType) value.type();
		Map<Field, Value> fieldValueMap = value.getValues(type.allFields());
		for (Field field : type.allFields()) {
			appendVarVal(val, val.getChildId(field.name()),
					fieldValueMap.get(field), level, thread);
		}
		parent.add(val);
	}

	private void appendArrVarVal(ExecValue parent, String varId,
			ArrayReference value, int level, ThreadReference thread) {
		ArrayValue val = new ArrayValue(varId);
		val.setValue(value);
		//add value of elements
		for (int i = 0; i < value.length(); i++) {
			appendVarVal(val, val.getElementId(i), value.getValue(i), level, thread);
		}
		parent.add(val);
	}
	/***/
	protected StackFrame findFrameByLocation(List<StackFrame> frames,
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
	
	public int getValRetrieveLevel() {
		return valRetrieveLevel;
	}
	
	public void setValRetrieveLevel(int valRetrieveLevel) {
		this.valRetrieveLevel = valRetrieveLevel;
	}
	
}
