/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.formula;

import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.utils.StringUtils;
import tzuyu.engine.utils.TzuYuPrimtiveTypes;

/**
 * @author LLT
 *
 */
public abstract class Box<T> {
	protected T value;
	
	public Box(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public boolean isNullBox() {
		return false;
	}
	
	public abstract boolean evaluate(ObjectInfo objectInfo);
	
	@SuppressWarnings("unchecked")
	public static <T> Box<T> capture(T value, Class<?> type) {
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return (Box<T>) new BooleanBox((Boolean) value);
		} else if (type.equals(String.class)) {
			return (Box<T>) new StringBox((String) value);
		} else if (type.equals(char.class) || type.equals(Character.class)) {
			return (Box<T>) new CharBox((Character) value);
		} else if (type.isEnum()) {
			return (Box<T>) new EnumBox((Enum<?>) value);
		} else {// The reference type variable
			return (Box<T>) new NullBox();
		}
	}

	public abstract String getDisplayValue();

	/**
	 * for String type. (excluding null value)
	 * @author LLT
	 *
	 */
	public static class StringBox extends Box<String> {

		public StringBox(String value) {
			super(value);
		}

		@Override
		public boolean evaluate(ObjectInfo objectInfo) {
			String value = TzuYuPrimtiveTypes.getString(String.class,
					(int) objectInfo.getNumericValue());

			return this.value.equals(value);
		}

		@Override
		public String getDisplayValue() {
			return "\"" + value + "\"";
		}
	}
	
	/**
	 * for boolean type (excluding null value)
	 * @author LLT
	 *
	 */
	public static class BooleanBox extends Box<Boolean> {
		
		public BooleanBox(Boolean value) {
			super(value);
		}

		@Override
		public boolean evaluate(ObjectInfo objectInfo) {
			return TzuYuPrimtiveTypes.isBooleanTrue(boolean.class,
					(int) objectInfo.getNumericValue()) == value;
		}

		@Override
		public String getDisplayValue() {
			if (Boolean.TRUE.equals(value)) {
				return "true";
			}
			return "false";
		}
	}
	
	public static class CharBox extends Box<Character> {

		public CharBox(Character value) {
			super(value);
		}

		@Override
		public boolean evaluate(ObjectInfo objectInfo) {
			char unicodeValue = TzuYuPrimtiveTypes.getChar(char.class,
					(int) objectInfo.getNumericValue());
			return value == unicodeValue;
		}

		@Override
		public String getDisplayValue() {
			return "'" + value + "'";
		}
	}
	
	public static class EnumBox extends Box<Enum<?>> {

		public EnumBox(Enum<?> value) {
			super(value);
		}

		@Override
		public boolean evaluate(ObjectInfo objectInfo) {
			Object value = TzuYuPrimtiveTypes.getEnum(objectInfo.getType()
					.getType(), (int) objectInfo.getNumericValue());

			return this.value == value;
		}

		@Override
		public String getDisplayValue() {
			return StringUtils.enumToString(value.getClass().getSimpleName(),
					value);
		}
	}
	
	public static class NullBox extends Box<Object> {

		public NullBox() {
			super(null);
		}
		
		@Override
		public boolean evaluate(ObjectInfo objectInfo) {
			return objectInfo.isValueNull();
		}

		@Override
		public String getDisplayValue() {
			return "null";
		}
		
		@Override
		public boolean isNullBox() {
			return true;
		}
		
	}
}
