/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package common.cfg;

import java.util.HashMap;
import java.util.Map;

import sav.common.core.utils.EnumUtils;

/**
 * @author LLT
 *
 */
public class PropertiesContainer {
	private Map<String, Object> properties;
	
	public <T>void addProperty(String key, T value) {
		if (properties == null) {
			properties = new HashMap<String, Object>();
		}
		properties.put(key, value);
	}
	
	public <T>void addProperty(Enum<?> key, T value) {
		addProperty(EnumUtils.getName(key), value);
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public <T> T getProperty(Enum<?> key) {
		return getProperty(EnumUtils.getName(key));
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key) {
		return properties == null ? null : (T)properties.get(key);
	}
}
