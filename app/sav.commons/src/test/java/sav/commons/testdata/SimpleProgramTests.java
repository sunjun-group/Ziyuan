/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author khanh
 *
 */
public class SimpleProgramTests {
	@Test
	public void test1() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(true, simpleProgram.isPalindrome1(1));
	}
	
	@Test
	public void test2() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(false, simpleProgram.isPalindrome1(12));
	}
	
	@Test
	public void test3() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(true, simpleProgram.isPalindrome1(121));
	}
	
	@Test
	public void test4() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(false, simpleProgram.isPalindrome1(123));
	}
	
	@Test
	public void test5() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(false, simpleProgram.isPalindrome1(10000));
	}
	
	//--------------------------
	@Test
	public void test6() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(true, simpleProgram.isPalindrome2(1));
	}
	
	@Test
	public void test7() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(false, simpleProgram.isPalindrome2(12));
	}
	
	@Test
	public void test8() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(true, simpleProgram.isPalindrome2(121));
	}
	
	@Test
	public void test9() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(false, simpleProgram.isPalindrome2(123));
	}
	
	@Test
	public void test10() {
		SimplePrograms simpleProgram = new SimplePrograms();
		assertEquals(false, simpleProgram.isPalindrome2(10000));
	}
	
	//-------------------------------------
	@Test
	public void test11() {
		SimplePrograms simpleProgram = new SimplePrograms();
		int duplicateNumber = simpleProgram.duplicatedNumber(new int[]{0});
		assertEquals(-1, duplicateNumber);
	}
	
	@Test
	public void test12() {
		SimplePrograms simpleProgram = new SimplePrograms();
		int duplicateNumber = simpleProgram.duplicatedNumber(new int[]{0,1});
		assertEquals(-1, duplicateNumber);
	}
	
	@Test
	public void test13() {
		SimplePrograms simpleProgram = new SimplePrograms();
		int duplicateNumber = simpleProgram.duplicatedNumber(new int[]{0,1,3,1});
		assertEquals(1, duplicateNumber);
	}
	
	@Test
	public void test14() {
		SimplePrograms simpleProgram = new SimplePrograms();
		int duplicateNumber = simpleProgram.duplicatedNumber(new int[]{0, 2, 4, 2, 3});
		assertEquals(1, duplicateNumber);
	}
	
	@Test
	public void test15() {
		SimplePrograms simpleProgram = new SimplePrograms();
		int duplicateNumber = simpleProgram.duplicatedNumber(new int[]{0, 4, 2, 4, 2});
		assertEquals(true, duplicateNumber == 2 || duplicateNumber == 4);
	}
	
	@Test
	public void test16() {
		SimplePrograms simpleProgram = new SimplePrograms();
		int duplicateNumber = simpleProgram.duplicatedNumber(new int[]{0, 0, 0, 0, 0, 0, 0});
		assertEquals(0, duplicateNumber);
	}
	
	//--------------------
	
	@Test
	public void test17() {
		SimplePrograms simpleProgram = new SimplePrograms();
		boolean isFound = simpleProgram.searchInSortingMatrix1(new int[][]{{1,3,5},{7,9,11},{13,15,17}}, 11);
		assertEquals(true, isFound);
	}
	
	@Test
	public void test18() {
		SimplePrograms simpleProgram = new SimplePrograms();
		boolean isFound = simpleProgram.searchInSortingMatrix1(new int[][]{{1,3,5},{7,9,11},{13,15,17}}, 12);
		assertEquals(false, isFound);
	}
	
	@Test
	public void test19() {
		SimplePrograms simpleProgram = new SimplePrograms();
		boolean isFound = simpleProgram.searchInSortingMatrix1(new int[][]{{1}}, 1);
		assertEquals(true, isFound);
	}
	
	@Test
	public void test20() {
		SimplePrograms simpleProgram = new SimplePrograms();
		boolean isFound = simpleProgram.searchInSortingMatrix1(new int[][]{{1}}, 0);
		assertEquals(false, isFound);
	}
	
	@Test
	public void test21() {
		SimplePrograms simpleProgram = new SimplePrograms();
		boolean isFound = simpleProgram.searchInSortingMatrix1(new int[][]{{1,3,5},{7,9,11},{13,15,17}}, 1);
		assertEquals(true, isFound);
	}
	
	@Test
	public void test22() {
		SimplePrograms simpleProgram = new SimplePrograms();
		boolean isFound = simpleProgram.searchInSortingMatrix1(new int[][]{{1,3,5},{7,9,11},{13,15,17}}, 17);
		assertEquals(false, isFound);
	}
}
