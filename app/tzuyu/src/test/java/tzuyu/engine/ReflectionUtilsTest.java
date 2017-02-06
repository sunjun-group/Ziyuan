/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import java.util.List;

import tzuyu.AbstractTest;
import tzuyu.engine.utils.ReflectionUtils;

/**
 * @author LLT
 *
 */
public class ReflectionUtilsTest extends AbstractTest {
	
	public void testCanBe() {
		ReflectionUtils.canBePassedAsArgument(null, List.class);
	}
}
