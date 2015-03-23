/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.inject;

import java.util.Properties;

/**
 * @author LLT
 *
 */
public class PropertiesApplicationData {
	private Properties properties;
	
	public boolean getBoolean(String key) {
		return Boolean.valueOf(getProperty(key));
	}

	public <T extends Enum<T>> T getEnum(String key, Class<T> enumType) {
		return Enum.valueOf(enumType, getProperty(key));
	}
	
	private String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
}
