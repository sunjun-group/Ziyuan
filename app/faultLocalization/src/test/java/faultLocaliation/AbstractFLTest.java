/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import icsetlv.AbstractTest;

import java.util.Arrays;
import java.util.List;

import main.IDataProvider;

import org.junit.BeforeClass;

import commons.TestConfiguration;

import sav.common.core.Logger;

/**
 * @author LLT
 *
 */
public class AbstractFLTest extends AbstractTest {
	
	@BeforeClass
	public static void setup() {
		Logger.debug = true;
	}
	
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
