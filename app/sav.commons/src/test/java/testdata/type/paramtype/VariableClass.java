/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package testdata.type.paramtype;

/**
 * @author LLT
 *
 */
public class VariableClass<T extends ParamClassInterface<T>> {

	public void method(T[] param) {
		
	}
	
	public void method(int a) {
		
	}
}
