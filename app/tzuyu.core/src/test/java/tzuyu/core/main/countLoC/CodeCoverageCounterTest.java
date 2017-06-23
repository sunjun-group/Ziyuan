/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main.countLoC;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.utils.StringUtils;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;
import tzuyu.core.main.AbstractTzPackageTest;
import tzuyu.core.main.FaultLocateParams;

/**
 * @author LLT
 *
 */
public class CodeCoverageCounterTest extends AbstractTzPackageTest {
	private static Logger log = LoggerFactory.getLogger(CodeCoverageCounterTest.class);
	private CodeCoverageCounter counter;
	protected FaultLocateParams params;
	
	@Before
	public void setup() {
		super.setup();
		counter = new CodeCoverageCounter(context, appData);
		params = new FaultLocateParams();
	}
	
	@Override
	public List<String> prepare(TestPackage testPkg) throws Exception {
		List<String> result = super.prepare(testPkg);
		List<String> allClasses = listAllClasses(testPkg.getValue(TestDataColumn.SOURCE_FOLDER));
		params.setTestingClassNames(allClasses);
		return result;
	}
	
	@Test
	public void runjavaparser46() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "46");
		prepare(testPkg);
		params.setJunitClassNames(Arrays.asList("japa.parser.ast.test.TestIssue46"));
		counter.count(params);
	}
	
	@Test
	public void runjavaparser57() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "57");
		prepare(testPkg);
		params.setJunitClassNames(Arrays.asList("japa.parser.ast.test.TestIssue57"));
		counter.count(params);
	}
	
	@Test
	public void testDiffUtils10() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "10");
		prepare(testPkg);
		params.setJunitClassNames(Arrays.asList("issues.issue10.TestIssue10"));
		counter.count(params);
	}

	@Test
	public void testjodatime227() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "227");
		prepare(testPkg);
		params.setJunitClassNames(Arrays.asList("org.joda.time.issues.TestIssue227"));
		counter.count(params);
	}
	
	@Test
	public void testjodatime21() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "21");
		prepare(testPkg);
		params.setJunitClassNames(Arrays.asList("org.joda.time.issues.TestIssue21"));
		counter.count(params);
	}
	
	@Test
	public void testjodatime77() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "77");
		prepare(testPkg);
		params.setJunitClassNames(Arrays.asList("org.joda.time.issues.TestIssue77"));
		counter.count(params);
	}
	
	@Test
	public void testCommonsMath835() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "835");
		super.prepare(testPkg);
		params.setTestingClassNames(null);
		params.setJunitClassNames(Arrays.asList("org.apache.commons.math3.issues.TestIssue835"));
		counter.count(params);
	}
	
	@Test
	public void testCommonsMath1196() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1196");
		super.prepare(testPkg);
		params.setTestingClassNames(null);
		params.setJunitClassNames(Arrays.asList("org.apache.commons.math3.issues.TestIssue1196"));
		counter.count(params);
	}
	
	@Test
	public void testCommonsMath1005() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1005");
		super.prepare(testPkg);
		params.setTestingClassNames(null);
		params.setJunitClassNames(Arrays.asList("org.apache.commons.math3.issues.TestIssue1005"));
		counter.count(params);
	}
	
	@Test
	public void testListAllClasses() {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "46");
		List<String> allClasses = listAllClasses(testPkg.getValue(TestDataColumn.SOURCE_FOLDER));
		log.debug(StringUtils.join(allClasses, "\n"));
	}
	
	private List<String> listAllClasses(String... srcFolders) {
		List<String> allClasses = new ArrayList<String>();
		for (String srcFolder : srcFolders) {
			appendClassNames(StringUtils.EMPTY, new File(srcFolder), allClasses);
		}
		return allClasses;
	}
	
	private void appendClassNames(String curPkg, File folder, List<String> allClassNames) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory() && !file.getName().contains(".")) {
				appendClassNames(StringUtils.dotJoin(curPkg, file.getName()), file, allClassNames);
			} else if (file.getName().endsWith(".java")) {
				allClassNames.add(StringUtils.dotJoin(curPkg, file.getName().split(".java")[0]));
			}
		}
	}
}
