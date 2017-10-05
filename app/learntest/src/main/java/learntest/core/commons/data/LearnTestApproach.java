/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data;

/**
 * @author LLT
 *
 */
public enum LearnTestApproach {
	L2T ("L2T"),
	RANDOOP ("RandomTest"),
	GAN("L2T_GAN");
	
	private String name;
	private LearnTestApproach(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
