/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store.iface;

import gentest.core.data.dto.TypeInitializer;

/**
 * @author LLT
 *
 */
public interface ITypeMethodCallStore {

	TypeInitializer loadConstructors(Class<?> type);

	void storeConstructors(Class<?> type, TypeInitializer constructors);

}
