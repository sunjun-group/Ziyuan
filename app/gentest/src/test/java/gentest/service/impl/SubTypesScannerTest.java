/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.service.impl;

import org.junit.Assert;
import org.junit.Test;

import gentest.core.data.type.SubTypesScanner;

/**
 * @author LLT
 *
 */
public class SubTypesScannerTest {
	private SubTypesScannerMock scanner = new SubTypesScannerMock();
	
	@Test
	public void areClosePkgs() {
		SubTypesScannerMock sc = new SubTypesScannerMock();
		Assert.assertTrue(sc.areClosePkgs("java.util", "java"));
		Assert.assertTrue(sc.areClosePkgs("java", "java"));
		Assert.assertTrue(sc.areClosePkgs("java.util", "java.lang"));
		Assert.assertTrue(sc.areClosePkgs("java.util", "java.util.jar"));
		Assert.assertFalse(sc.areClosePkgs("java", "jar"));
		Assert.assertFalse(sc.areClosePkgs("java", "jav"));
	}
	
	private static class SubTypesScannerMock extends SubTypesScanner {
		@Override
		protected boolean areClosePkgs(String subTypePkg, String typePkg) {
			return super.areClosePkgs(subTypePkg, typePkg);
		}
	}
}
