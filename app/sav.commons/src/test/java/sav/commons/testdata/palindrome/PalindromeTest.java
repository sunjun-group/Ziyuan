/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata.palindrome;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import sav.commons.testdata.SimplePrograms;

/**
 * @author LLT
 *
 */
public class PalindromeTest {
	
	@Test
	public void test1() {
		SimplePrograms program = new SimplePrograms();
		assertEquals(true, program.isPalindrome1(1));
	}
	
	@Test
	public void test2() {
		SimplePrograms program = new SimplePrograms();
		assertEquals(false, program.isPalindrome1(12));
	}
	
	@Test
	public void test3() {
		SimplePrograms program = new SimplePrograms();
		assertEquals(true, program.isPalindrome1(121));
	}
	
	@Test
	public void test4() {
		SimplePrograms program = new SimplePrograms();
		assertEquals(false, program.isPalindrome1(123));
	}
	
	@Test
	public void test5() {
		SimplePrograms program = new SimplePrograms();
		assertEquals(false, program.isPalindrome1(10000));
	}
}
