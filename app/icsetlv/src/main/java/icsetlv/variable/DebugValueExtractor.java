/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.BooleanType;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteType;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharType;
import com.sun.jdi.CharValue;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleType;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.Field;
import com.sun.jdi.FloatType;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerType;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.LongType;
import com.sun.jdi.LongValue;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;

import icsetlv.DefaultValues;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.PrimitiveUtils;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.PrimitiveValue;
import sav.strategies.dto.execute.value.ReferenceValue;
import sav.strategies.dto.execute.value.StringValue;

/**
 * @author LLT
 *
 */
public class DebugValueExtractor {
	protected static Logger log = LoggerFactory.getLogger(DebugValueExtractor.class);
	private static final String TO_STRING_SIGN= "()Ljava/lang/String;";
	private static final String TO_STRING_NAME= "toString";
	private static final Pattern OBJECT_ACCESS_PATTERN = Pattern.compile("^\\.([^.\\[]+)(\\..+)*(\\[.+)*$");
	private static final Pattern ARRAY_ACCESS_PATTERN = Pattern.compile("^\\[(\\d+)\\](.*)$");
	private static final int MAX_ARRAY_ELEMENT_TO_COLLECT = 5;

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
				List<Variable> vars = new ArrayList<Variable>(bkp.getVars());
				
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
					}
					// to get variables such as Class.FIELD
					// in case full name == simple name, it is local variable
					else if (!bpVar.getSimpleName().equals(bpVar.getFullName()) &&
							bpVar.getSimpleName().charAt(0) >= 'A' && bpVar.getSimpleName().charAt(0) <= 'Z') {
						ClassLoader loader = bkp.getClass().getClassLoader();
						String className = bkp.getClassCanonicalName();
						String packageName = className.substring(0, className.lastIndexOf('.')); 
						try {
							Class<?> cl = loader.loadClass(packageName + "." + bpVar.getParentName());
							
							java.lang.reflect.Field f = cl.getField(bpVar.getSimpleName());
							Class<?> fl = f.getType();
							
							if (!java.lang.reflect.Modifier.isFinal(f.getModifiers()) && !f.isEnumConstant()) {
								if (fl.equals(boolean.class) || fl.equals(Boolean.class)) {
									boolean value = cl.getField(bpVar.getSimpleName()).getBoolean(null);
									bkVal.add(sav.strategies.dto.execute.value.BooleanValue.of(bpVar.getFullName(), value));
								} else if (fl.equals(int.class) || fl.equals(Integer.class)) {
									int value = cl.getField(bpVar.getSimpleName()).getInt(null);
									bkVal.add(sav.strategies.dto.execute.value.IntegerValue.of(bpVar.getFullName(), value));
								} else if (fl.equals(long.class) || fl.equals(Long.class)) {
									long value = cl.getField(bpVar.getSimpleName()).getLong(null);
									bkVal.add(sav.strategies.dto.execute.value.LongValue.of(bpVar.getFullName(), value));
								} else if (fl.equals(float.class) || fl.equals(Float.class)) {
									float value = cl.getField(bpVar.getSimpleName()).getFloat(null);
									bkVal.add(sav.strategies.dto.execute.value.FloatValue.of(bpVar.getFullName(), value));
								} else if (fl.equals(double.class) || fl.equals(Double.class)) {
									double value = cl.getField(bpVar.getSimpleName()).getDouble(null);
									bkVal.add(sav.strategies.dto.execute.value.DoubleValue.of(bpVar.getFullName(), value));
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							// log.info(e.getMessage());
						}
					}
					
					// because it seems we need field information even that field is not accessed explicitly
					// try to get all field, will be modified later (Long)
					else {
						// Then check class fields (static & non static)
						Field matchedField = null;
						for (Field field : allFields) {
							if (field.name().equals(bpVar.getParentName())) {
								matchedField = field;
								break;
							}
						}

						// if (matchedField != null) {
						if (matchedField != null && !matchedField.isFinal() && !matchedField.isEnumConstant()) {
							if (matchedField.isStatic()) {
								param = JdiParam.staticField(matchedField, refType, refType.getValue(matchedField));
							} else {
								Value value = objRef == null ? null : objRef.getValue(matchedField);
								param = JdiParam.nonStaticField(matchedField, objRef, value);
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
				
				// this code is added to get all fields of this object
				for (Field field : allFields) {
					// boolean isConstant = (field.isStatic() && field.isFinal()) || field.isEnumConstant();
					boolean isConstant = field.isFinal() || field.isEnumConstant();
					if (!isConstant) {
						if (field.isStatic()) {
							Variable bpVar = new Variable(field.name(), field.name(), VarScope.THIS);
							JdiParam param = JdiParam.staticField(field, refType, refType.getValue(field));
							allVariables.put(bpVar, param);
							vars.add(bpVar);
						} else {
							Variable bpVar = new Variable(field.name(), field.name(), VarScope.THIS);
							Value value = objRef == null ? null : objRef.getValue(field);
							JdiParam param = JdiParam.nonStaticField(field, objRef, value);
							allVariables.put(bpVar, param);
							vars.add(bpVar);
						}
					}
				}
				
				if (!allVariables.isEmpty()) {
					// collectValue(bkVal, thread, allVariables);
					collectValue(bkVal, thread, allVariables, vars);
				}
			}
		}
		return bkVal;
	}

	protected void collectValue(BreakpointValue bkVal, ThreadReference thread,
			final Map<Variable, JdiParam> allVariables, final List<Variable> bpVars) throws SavException {
		for (Variable var : bpVars) {
			if (allVariables.containsKey(var)) {
				String varId = var.getId();
				appendVarVal(bkVal, varId, allVariables.get(var).getValue(), 1, thread);
			}
		}
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
					subParam = JdiParam.arrayElement(array, index, getArrayEleValue(array, index)); 
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

	private Value getArrayEleValue(ArrayReference array, int index) {
		if (array == null) {
			return null;
		}
		if (index >= array.length()) {
			return null;
		}
		return array.getValue(index);
	}

	/** append execution value*/
	private void appendVarVal(ExecValue parent, String varId,
			Value value, int level, ThreadReference thread) {
		if (level > valRetrieveLevel || varId.endsWith("serialVersionUID")) {
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
				parent.add(sav.strategies.dto.execute.value.BooleanValue.of(varId, ((BooleanValue)value).booleanValue()));
			} else if (type instanceof ByteType) {
				parent.add(sav.strategies.dto.execute.value.ByteValue.of(varId, ((ByteValue)value).byteValue()));
			} else if (type instanceof CharType) {
				parent.add(sav.strategies.dto.execute.value.CharValue.of(varId, ((CharValue)value).charValue()));
			} else if (type instanceof DoubleType) {
				parent.add(sav.strategies.dto.execute.value.DoubleValue.of(varId, ((DoubleValue)value).doubleValue()));
			} else if (type instanceof FloatType) {
				parent.add(sav.strategies.dto.execute.value.FloatValue.of(varId, ((FloatValue)value).floatValue()));
			} else if (type instanceof IntegerType) {
				parent.add(sav.strategies.dto.execute.value.IntegerValue.of(varId, ((IntegerValue)value).intValue()));
			} else if (type instanceof LongType) {
				parent.add(sav.strategies.dto.execute.value.LongValue.of(varId, ((LongValue)value).longValue()));
			} else if (type instanceof ShortType) {
				parent.add(sav.strategies.dto.execute.value.ShortValue.of(varId, ((ShortValue)value).shortValue()));
			} else {
				parent.add(new PrimitiveValue(varId, value.toString()));
			}
		} else if (type instanceof ArrayType) {
			appendArrVarVal(parent, varId, (ArrayReference)value, level, thread);
		} else if (type instanceof ClassType) {
			String typeName = type.name();
			
			if (PrimitiveUtils.isString(typeName)) {
				appendStringVarVal(parent, varId, (ObjectReference) value, level, thread);
				// parent.add(new StringValue(varId, toPrimitiveValue((ClassType) type, (ObjectReference)value, thread)));
			} else if (PrimitiveUtils.isBoolean(typeName)) {
				parent.add(new sav.strategies.dto.execute.value.BooleanValue(
						varId, Boolean.parseBoolean(toPrimitiveValue((ClassType) type, (ObjectReference)value, thread))));
			} else if (PrimitiveUtils.isByte(typeName)) {
				parent.add(new sav.strategies.dto.execute.value.ByteValue(
						varId, Byte.parseByte(toPrimitiveValue((ClassType) type, (ObjectReference)value, thread))));
			} else if (PrimitiveUtils.isChar(typeName)) {
				parent.add(new sav.strategies.dto.execute.value.CharValue(
						varId, toPrimitiveValue((ClassType) type, (ObjectReference)value, thread).charAt(0)));
			} else if (PrimitiveUtils.isDouble(typeName)) {
				String s = toPrimitiveValue((ClassType) type, (ObjectReference)value, thread);
				if (s.contains("\"")) {
					s = s.substring(1, s.length() - 1);
				}
				parent.add(new sav.strategies.dto.execute.value.DoubleValue(
						varId, Double.parseDouble(s)));
			} else if (PrimitiveUtils.isFloat(typeName)) {
				parent.add(new sav.strategies.dto.execute.value.FloatValue(
						varId, Float.parseFloat(toPrimitiveValue((ClassType) type, (ObjectReference)value, thread))));
			} else if (PrimitiveUtils.isInteger(typeName)) {
				String s = toPrimitiveValue((ClassType) type, (ObjectReference)value, thread);
				if (s.contains("\"")) s = s.substring(1, s.length() - 1);
				parent.add(new sav.strategies.dto.execute.value.IntegerValue(
						varId, Integer.parseInt(s)));
			} else if (PrimitiveUtils.isLong(typeName)) {
				parent.add(new sav.strategies.dto.execute.value.LongValue(
						varId, Long.parseLong(toPrimitiveValue((ClassType) type, (ObjectReference)value, thread))));
			} else if (PrimitiveUtils.isShort(typeName)) {
				parent.add(new sav.strategies.dto.execute.value.ShortValue(
						varId, Short.parseShort(toPrimitiveValue((ClassType) type, (ObjectReference)value, thread))));
			} else if (PrimitiveUtils.isPrimitiveType(typeName)) {
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
				log.warn(e.getMessage());
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
			boolean isConstant = field.isFinal() || field.isEnumConstant();
			if (!isConstant) {
			// if (!isConstant && !field.name().equals("hash")) {
				appendVarVal(val, val.getChildId(field.name()),
						fieldValueMap.get(field), level, thread);
			}
		}
		parent.add(val);
	}
	
	private void appendStringVarVal(ExecValue parent, String varId,
			ObjectReference value, int level, ThreadReference thread) {
		ReferenceValue val = new ReferenceValue(varId, false);
		ClassType type = (ClassType) value.type();
		Map<Field, Value> fieldValueMap = value.getValues(type.allFields());
		for (Field field : type.allFields()) {
			boolean isConstant = (field.isStatic() && field.isFinal()) || field.isEnumConstant();
			if (!isConstant) {
			// if (!isConstant && !field.name().equals("hash")) {
				appendVarVal(val, val.getChildId(field.name()),
						fieldValueMap.get(field), level, thread);
			}
		}
		parent.add(val);
		
//		ReferenceValue val1 = new ReferenceValue(varId, false);
//		ReferenceValue val2 = new ReferenceValue(varId + ".value", false);
//		val2.add(new StringValue(varId + ".value", toPrimitiveValue((ClassType) value.type(), (ObjectReference)value, thread)));
//		parent.add(val1);
//		parent.add(val2);
	}

	private void appendArrVarVal(ExecValue parent, String varId,
			ArrayReference value, int level, ThreadReference thread) {
		ArrayValue val = new ArrayValue(varId);
		val.setValue(value);
		//add value of elements
//		for (int i = 0; i < value.length() && i < MAX_ARRAY_ELEMENT_TO_COLLECT; i++) {
//			appendVarVal(val, val.getElementId(i), getArrayEleValue(value, i), level, thread);
//		}
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
