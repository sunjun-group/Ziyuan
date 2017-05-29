/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.testdata;

import org.junit.Test;

import sav.common.core.ModuleEnum;

/**
 * @author LLT
 *
 */
public class SwitchSampleTest {

	@Test
	public void run() {
		SwitchSample sample = new SwitchSample();
		sample.getName(ModuleEnum.FALT_LOCALIZATION);
	}
}
