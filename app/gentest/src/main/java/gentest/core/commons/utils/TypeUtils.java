/**
 * Copyright TODO
 */
package gentest.core.commons.utils;

import static japa.parser.ast.type.PrimitiveType.Primitive.*;
import japa.parser.ast.type.PrimitiveType.Primitive;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LLT
 * 
 */
public class TypeUtils {
	private TypeUtils() {}
	
	private static final Map<Class<?>, Primitive> primitiveWrapperMap;
	static {
		primitiveWrapperMap = new HashMap<Class<?>, Primitive>();
		primitiveWrapperMap.put(Boolean.class, Boolean);
		primitiveWrapperMap.put(Character.class, Char);
		primitiveWrapperMap.put(Byte.class, Byte);
		primitiveWrapperMap.put(Short.class, Short);
		primitiveWrapperMap.put(Integer.class, Int);
		primitiveWrapperMap.put(Long.class, Long);
		primitiveWrapperMap.put(Float.class, Float);
		primitiveWrapperMap.put(Double.class, Double);
	}
	private static final Map<Class<?>, Primitive> primitiveTypeMap;
	static {
		primitiveTypeMap = new HashMap<Class<?>, Primitive>();
		primitiveTypeMap.put(boolean.class, Boolean);
		primitiveTypeMap.put(char.class, Char);
		primitiveTypeMap.put(byte.class, Byte);
		primitiveTypeMap.put(short.class, Short);
		primitiveTypeMap.put(int.class, Int);
		primitiveTypeMap.put(long.class, Long);
		primitiveTypeMap.put(float.class, Float);
		primitiveTypeMap.put(double.class, Double);
	}
	
	public static boolean isPrimitiveObject(Class<?> type) {
		return primitiveWrapperMap.get(type) != null;
	}
	
	public static boolean isPrimitive(Class<?> type) {
		return primitiveTypeMap.get(type) != null;
	}

	/**
	 * for both primitive and primitive wrapper.
	 */
	public static Primitive getAssociatePrimitiveType(Class<?> type) {
		Primitive primitiveType = primitiveTypeMap.get(type);
		if (primitiveType == null) {
			primitiveType = primitiveWrapperMap.get(type);
		}
		return primitiveType;
	}
	
	public static Primitive getEnumValue(Class<?> type) {
		Primitive primitiveType = primitiveTypeMap.get(type);
		if (primitiveType == null) {
			primitiveType = primitiveWrapperMap.get(type);
		}
		return primitiveType;
	}

	public static boolean isString(Class<?> vartype) {
		return vartype == String.class;
	}
	
	public static boolean isEnumType(Class<?> vartype) {
		return vartype.isEnum();
	}

	public static boolean isEnum(Object varValue) {
		return varValue.getClass().isEnum();
	}
}
