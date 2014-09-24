/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import java.util.Arrays;
import java.util.List;

import main.IDataProvider;
import icsetlv.AbstractTest;
import icsetlv.TestConfiguration;

/**
 * @author LLT
 *
 */
public class AbstractFLTest extends AbstractTest {
	
	protected IDataProvider getDataProvider() {
		return new MockDataProvider() {
			
			@Override
			protected List<String> getProjectClasspath() {
				TestConfiguration testConfig = TestConfiguration.getInstance();
				return Arrays.asList(
						testConfig.getTarget(
								TestConfiguration.FALTLOCALISATION),
						testConfig.getTestTarget(
								TestConfiguration.FALTLOCALISATION),
						testConfig.getJunitLib()
						);
			}
		};
	}
}
