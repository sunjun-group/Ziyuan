/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation;

import java.util.ArrayList;
import java.util.HashMap;
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
		Map<String, List<ClassLocation>> classLocationMap = new HashMap<String, List<ClassLocation>>();
		String clazzName = MutationTestData.class.getName();
		List<ClassLocation> value = new ArrayList<ClassLocation>();
		value.add(new ClassLocation(clazzName, null, 27));
		classLocationMap.put(clazzName, value);
		Map<String, MutationResult> result = mutator.mutate(classLocationMap,  "./src/test/java");
		System.out.println(result);
	}
}
