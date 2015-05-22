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

import org.junit.Before;
import org.junit.Test;

import sav.strategies.dto.ClassLocation;
import testdata.insertion.InsertTestData;

import mutation.mutator.Mutator;
import mutation.mutator.insertdebugline.DebugLineInsertionResult;


/**
 * @author LLT
 *
 */
public class DebugLineInsertionTest {
	private Mutator mutator;
	
	@Before
	public void setup() {
		mutator = new Mutator();
	}
	
	@Test
	public void testInsertion() {
		Map<String, List<ClassLocation>> classLocationMap = new HashMap<String, List<ClassLocation>>();
		String clazzName = InsertTestData.class.getName();
		List<ClassLocation> value = new ArrayList<ClassLocation>();
		value.add(new ClassLocation(clazzName, null, 25));
		value.add(new ClassLocation(clazzName, null, 50));
		value.add(new ClassLocation(clazzName, null, 27));
		value.add(new ClassLocation(clazzName, null, 46));
		value.add(new ClassLocation(clazzName, null, 31));
		value.add(new ClassLocation(clazzName, null, 32));
		value.add(new ClassLocation(clazzName, null, 42));
		value.add(new ClassLocation(clazzName, null, 30));
		classLocationMap.put(clazzName, value);
		Map<String, DebugLineInsertionResult> result = mutator.insertDebugLine(
				classLocationMap, "./src/test/java");
		System.out.println(result);
		/*
		 * 50=56, 32=36, 42=46, 25=26, 27=29, 46=50, 31=34, 30=32
		 */
	}
	
}
