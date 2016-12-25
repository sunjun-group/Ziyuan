/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import org.junit.Before;
import org.junit.Test;

import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import sav.commons.testdata.opensource.TestPackage;

/**
 * @author LLT
 *
 */
public class ZiyuanPackageTest extends AbstractTzPackageTest {
	protected TzuyuCore tzCore;
	protected FaultLocateParams params;
	
	@Before
	public void setup() {
		super.setup();
		tzCore = new TzuyuCore(context);
		params = new FaultLocateParams();
		params.setMachineLearningEnable(true);
		params.setRankToExamine(3);
		params.setRunMutation(false);
		params.setUseSlicer(true);
		params.setValueRetrieveLevel(3);
		params.setVarNameCollectionMode(VarNameCollectionMode.FULL_NAME);
	}
	
	public void runFaultLocate(TestPackage testPkg) throws Exception {
		prepare(testPkg);
		params.setTestingClassNames(testingClassNames);
		params.setTestingPkgs(testingPackages);
		params.setJunitClassNames(junitClassNames);
		tzCore.ziyuan(params);
	}
	
	/**
	 * test part
	 */
	
	/**
	 * https://code.google.com/p/javaparser/issues/detail?id=46&colspec=ID%20Type%20Status%20Stars%20Summary
	 * ASTParserTokenManager: 2220
	 * & ASTParserTokenManager: 69
	 */
	@Test
	public void testjavaparser46() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "46");
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	/**
	 * https://code.google.com/p/javaparser/issues/detail?id=57&colspec=ID%20Type%20Status%20Stars%20Summary
	 * ASTParser.ClassOrInterfaceType:1810
	 * 
	 */
	@Test
	public void testjavaparser57() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "57");
		params.setRankToExamine(2);
		runFaultLocate(testPkg);
	}
	
	
	@Test
	public void countLocJavaParser57() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "57");
		params.setRankToExamine(2);
		params.setRunMutation(false);
		params.setMachineLearningEnable(true);
		runFaultLocate(testPkg);
	}
	
	
	@Test
	public void testjavaparser69() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "69");
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjodatime90() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "90");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjodatime194() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "194");
		params.setRankToExamine(7);
		runFaultLocate(testPkg);
	}
	
	/**
	 * https://github.com/JodaOrg/joda-time/issues/233
	 * fix: 
	 * https://github.com/JodaOrg/joda-time/commit/48b6ae85b02f41bec0fac7110ee47239c53eee9d
	 */
	@Test
	public void testjodatime233() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "233");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	/**
	 * https://github.com/JodaOrg/joda-time/issues/227
	 * fix:
	 * https://github.com/JodaOrg/joda-time/commit/b95ebe240aa65d2d28deb84b76d8a7edacf922f8
	 * bug at BasicMonthOfYearDateTimeField.add:212
	 * int curMonth0 = partial.getValue(0) - 1;
	 * => int curMonth0 = values[0] - 1;
	 */
	@Test
	public void testjodatime227() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "227");
		params.setValueRetrieveLevel(4);
		params.setUseSlicer(false);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjodatime21() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "21");
		params.setUseSlicer(false);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjodatime77() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "77");
		params.setUseSlicer(false);
		runFaultLocate(testPkg);
	}
	
	/**
	 * bug at MyersDiff.buildPath:137
	 * final int middle = (size + 1) / 2;
	 * => final int middle = size / 2;
	 */
	@Test
	public void testDiffUtils8() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "8");
		params.setRankToExamine(3);
		params.setRunMutation(false);
		params.setValueRetrieveLevel(2);
//		params.setGenTest(true);
		runFaultLocate(testPkg);
	}
	
	/**
	 * bug at DiffUtils.parseUnifiedDiff:137 (rev.25)
	 * add else block: rawChunk.add(new Object[] {" ", ""});
	 */
	@Test
	public void testDiffUtils10() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "10");
		params.setValueRetrieveLevel(2);
//		params.setGroupLines(true);
//		params.setRunMutation(true);
		runFaultLocate(testPkg);
	}
	
	/**
	 * bug in DiffUtils.generateUnifiedDiff:192 (rev.25) 
	 * add condition !patch.getDeltas().isEmpty()
	 */
	@Test
	public void testDiffUtils12() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "12");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	/**
	 * not really a bug, this result is acceptable because of the idea of myers algorithm
	 */
	@Test
	public void testDiffUtils18() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "18");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	/**
	 * Parsing add-only parts of unified diffs generated by "diff -U 0 ..." fails
	 * llt: not sure if this is really a bug, it depends on the requirement. 
	 * */
	@Test
	public void testDiffUtils20() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "20");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1196() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1196");
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath835() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "835");
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1127() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1127");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1005() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1005");
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1141() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1141");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1230() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1230");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1231() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1231");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1232() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1232");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1232b() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1232b");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1233() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1233");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1234() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1234");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1234b() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1234b");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testCommonsPrimitives100() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-primitives", "100");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsPrimitives100b() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-primitives", "100b");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testCommonsPrimitives101() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-primitives", "101");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsPrimitives102() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-primitives", "102");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsCollections100() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-collections", "100");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsCollections100b() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-collections", "100b");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsCollections101() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-collections", "101");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsCollections101b() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-collections", "101b");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsCollections102() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-collections", "102");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTimeAndMoney100() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("time-money", "100");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTimeAndMoney101() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("time-money", "101");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTimeAndMoney101b() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("time-money", "101b");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTimeAndMoney102() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("time-money", "102");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTimeAndMoney103() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("time-money", "103");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsCli233() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("commons-cli", "233-v1.2");
		params.setRankToExamine(3);
		runFaultLocate(testPkg);
	}
	
	/**
	 * bug at CSVParser.parseLine:265-267
	 * missing else:  inField = false;  
	 */
	@Test
	public void testOpenCsv102() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("opencsv", "102");
		params.setRankToExamine(4);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testOpenCsv108() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("opencsv", "108");
		params.setRankToExamine(5);
		params.setValueRetrieveLevel(2);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testOpenCsv106() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("opencsv", "106");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	
	////////////////////////////////////////////
	// New tests for defects4j
	////////////////////////////////////////////
	
	@Test
	public void testMath1() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "1");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath3() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "3");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath4() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "4");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath8() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "8");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath28() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "28");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath38() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "38");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath40() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "40");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath49() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "49");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath58() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "58");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath60() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "60");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath61() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "61");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath70() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "70");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath79() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "79");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath81() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "81");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath84() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("apache-math", "84");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testMath85() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "85");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath89() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "89");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath90() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "90");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath95() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "95");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath97() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "97");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath98() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "98");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath100() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "100");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testMath101() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-math", "101");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTime2() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "2");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTime5() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "5");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTime6() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "6");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testTime8() throws Exception {
		long start = System.currentTimeMillis();
		
		TestPackage testPkg = TestPackage.getPackage("joda-time", "8");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	@Test
	public void testTime10() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "10");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testChart2() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("chart", "2");
		params.setRankToExamine(4);
		params.setVarNameCollectionMode(VarNameCollectionMode.HIGHEST_LEVEL_VAR);
		runFaultLocate(testPkg);
	}
	
}
