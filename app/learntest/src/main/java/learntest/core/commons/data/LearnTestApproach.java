/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data;

import gov.nasa.jpf.jdart.JDart;

/**
 * @author LLT
 *
 */
public enum LearnTestApproach {
	L2T ("L2T"),
	RANDOOP ("RandomTest"),
	JDART("Jdart"),
	GAN("L2T_GAN");
	
	private String name;
	private LearnTestApproach(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
