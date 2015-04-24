/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mutation.mutator.MutationResult;
import mutation.mutator.Mutator;

import org.junit.Before;
import org.junit.Test;

import sav.strategies.dto.ClassLocation;
import testdata.mutator.MutationTestData;

/**
 * @author LLT
 *
 */
public class MutatorTest {
	private Mutator mutator;
	
	@Before
	public void setup() {
		mutator = new Mutator();
	}
	
	@Test
	public void testMutator() {
		String clazzName = MutationTestData.class.getName();
		List<ClassLocation> value = new ArrayList<ClassLocation>();
		value.add(new ClassLocation(clazzName, null, 27));
		Map<String, MutationResult> result = mutator.mutate(value,  "./src/test/java");
		System.out.println(result);
	}
}
