/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata;

import sav.commons.TestConfiguration;
import sav.commons.utils.TestConfigUtils;

/**
 * @author LLT
 *
 */
public class TestDataConstants {
	public static final String TEST_DATA_FOLDER = TestConfigUtils
			.getConfig(TestConfiguration.PROPERTY_TESTCASE_BASE);
	public static final String TEST_PROJECT_LIBS = "libs";
}
