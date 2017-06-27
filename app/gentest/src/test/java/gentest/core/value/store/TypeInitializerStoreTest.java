/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store;

import org.junit.Test;

import gentest.core.data.typeinitilizer.TypeInitializer;
import sav.commons.AbstractTest;
import testdata.gentest.core.value.store.DeprecatedClass;

/**
 * @author LLT
 *
 */
public class TypeInitializerStoreTest extends AbstractTest {

	@Test
	public void testIgnoreDeprecated() {
		TypeInitializerStore store = new TypeInitializerStore();
		store.setPrjClassLoader(this.getClass().getClassLoader());
		TypeInitializer initializer = store.load(DeprecatedClass.class);
		System.out.println(initializer);
	}
}
