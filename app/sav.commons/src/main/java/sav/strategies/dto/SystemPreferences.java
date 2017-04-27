/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto;

import java.util.HashMap;

import sav.common.core.SystemVariables;

/**
 * @author LLT
 *
 */
public class SystemPreferences {
	private HashMap<String, Object> variables = new HashMap<String, Object>();
	
	public SystemPreferences() {
		variables = new HashMap<String, Object>();
	}
	
	public Object set(String key, Object value) {
		return variables.put(key, value); 
	}
	
	public void set(SystemVariables var, Object value) {
		set(var.getName(), value);
	}

	public void setBoolean(SystemVariables var, boolean value) {
		set(var, value);
	}
	
	public void setEnum(SystemVariables var, Enum<?> value) {
		set(var.getName(), value.name());
	}
	
	private Object getValue(Object value, Object defIfNull) {
		if (value == null) {
			return defIfNull;
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Object def) {
		Object value = getValue(variables.get(key), def);
		return value == null ? null : (T) value; 
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		Object value = getValue(variables.get(key), null);
		return value == null ? null : (T) value; 
	}
	
	public <T> T get(SystemVariables var) {
		return get(var.getName(), var.getDefValue());
	}

	public Boolean getBoolean(SystemVariables var) {
		return get(var);
	}

	public String getString(SystemVariables var) {
		return get(var);
	}
}
