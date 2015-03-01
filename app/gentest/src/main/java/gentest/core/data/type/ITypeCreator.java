/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.lang.reflect.Type;

/**
 * @author LLT
 * 
 */
public interface ITypeCreator {
	public IType forClass(Class<?> type);

	public IType[] forType(Type[] genericParameterTypes);
}
