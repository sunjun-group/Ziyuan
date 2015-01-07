/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author LLT
 *
 */
public class RandomnessTest {

	@Test
	public void testRandomSubList() {
		List<Integer> allList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		for (int i = 0; i < 100; i++) {
			System.out.println(Randomness.randomSubList(allList));
		}
	}
	
	@Test
	public void testRandomInt() {
		for (int i = 0; i < 100; i++) {
			System.out.println(Randomness.nextRandomInt(15));
		}
	}
}
