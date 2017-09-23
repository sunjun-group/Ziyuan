/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store.iface;

import gentest.core.data.typeinitilizer.TypeInitializer;

/**
 * @author LLT
 *
 */
public interface ITypeInitializerStore {

	TypeInitializer load(Class<?> type);

}
