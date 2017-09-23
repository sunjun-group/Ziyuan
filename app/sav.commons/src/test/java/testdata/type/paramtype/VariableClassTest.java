/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package testdata.type.paramtype;

import org.junit.Test;

/**
 * @author LLT
 *
 */
public class VariableClassTest {

	@Test
	public void test1() {
		VariableClass<ParamClassImpl> varClass = new VariableClass<ParamClassImpl>();
		ParamClassImpl[] param = new ParamClassImpl[]{new ParamClassImpl()};
		varClass.method(param);
	}
}
