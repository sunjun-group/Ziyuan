/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.Properties;

import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 * 
 */
public class ApplicationData {
	private Properties properties;
	
	private SpectrumAlgorithm suspiciousCalculAlgo;

	public SpectrumAlgorithm getSuspiciousCalculAlgo() {
		return suspiciousCalculAlgo;
	}

	public void setSuspiciousCalculAlgo(
			SpectrumAlgorithm suspiciousCalculAlgo) {
		this.suspiciousCalculAlgo = suspiciousCalculAlgo;
	}
	
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
