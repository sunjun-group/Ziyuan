/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.AssertionDetector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * 
 */
@RunWith(Parameterized.class)
public class AssertionDetectorTest extends AbstractTest {
	@Parameter
	public List<String> assertionsClazzes;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays
				.asList(new Object[][] {
						{ CollectionUtils
								.listOf("F:/project/Tzuyu/app/icsetlv/src/test/java/testdata/boundedStack/BoundedStack.java") },
						{ CollectionUtils.listOf(TestConfiguration
								.getInstance().getSourcepath()
								+ "\\testdata\\slice\\FindMaxCallerFailTest1.java") } });
	}

	@Test
	public void testScan() throws IcsetlvException {
		List<BreakPoint> breakpoints = AssertionDetector.scan(assertionsClazzes);
		printBkps(breakpoints);
		Assert.assertEquals(breakpoints.size(), 2);
		
	}
}
