/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata.opensource;

import static sav.common.core.utils.ResourceUtils.appendPath;
import static sav.commons.testdata.TestDataConstants.TEST_DATA_FOLDER;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.commons.TestConfiguration;

/**
 * @author LLT
 *
 */
public class TestPackage {
	private static final String ITEM_SEPARATOR = ";";
	private static final int TESTDATA_START_RECORD = 4;
	
	private static Map<String, TestPackage> allTestData;
	private Map<TestDataColumn, Object> packageData;
	
	static {
		try {
			allTestData = loadTestData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TestPackage() {
		packageData = new HashMap<TestPackage.TestDataColumn, Object>();
	}
	
	public static Map<String, TestPackage> loadTestData() throws IOException {
		CSVFormat format = CSVFormat.EXCEL.withHeader(TestDataColumn.allColumns());
		CSVParser parser = CSVParser.parse(new File(
				TestConfiguration.TESTDATA_CSV), Charset.forName("UTF-8"), format);
		List<CSVRecord> records = parser.getRecords();
		Map<String, TestPackage> allTests = new HashMap<String, TestPackage>();
		for (int i = TESTDATA_START_RECORD; i < records.size(); i++) {
			TestPackage pkg = new TestPackage();
			CSVRecord record = records.get(i);
			for (TestDataColumn col : TestDataColumn.values()) {
				if (col.multi) {
					pkg.packageData.put(col, CollectionUtils
							.toArrayList(org.apache.commons.lang.StringUtils
									.split(record.get(col), ITEM_SEPARATOR)));
				} else {
					pkg.packageData.put(col, record.get(col));
				}
			}
			allTests.put(getPkgId(record), pkg);
		}
		return allTests;
	}
	
	public String getValue(TestDataColumn col) {
		return (String) packageData.get(col);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getValues(TestDataColumn col) {
		return (List<String>) packageData.get(col);
	}
	
	private static String getPkgId(CSVRecord record) {
		return getPkgId(record.get(TestDataColumn.PROJECT_NAME),
				record.get(TestDataColumn.BUG_NUMBER));
	}
	
	private static String getPkgId(String prjName, String bugNo) {
		return StringUtils.join(":", prjName, bugNo);
	}
	
	public static TestPackage getPackage(String projectName, String bugNo) {
		TestPackage pkg = allTestData.get(getPkgId(projectName, bugNo));
		if (pkg == null) {
			throw new IllegalArgumentException(
					String.format(
							"cannot find the description for %s of project %s in testdata file",
							bugNo, projectName));
		}
		return pkg;
	}

	public static Map<String, TestPackage> getAllTestData() {
		return allTestData;
	}

	public List<String> getClassPaths() {
		return toAbsolutePaths(getValues(TestDataColumn.CLASS_PATH));
	}
	
	private List<String> toAbsolutePaths(List<String> paths) {
		List<String> abPaths = new ArrayList<String>();
		String prjPath = appendPath(TEST_DATA_FOLDER,
				getValue(TestDataColumn.PROJECT_NAME));
		for (String path : paths) {
			abPaths.add(appendPath(prjPath, path));
		}
		return abPaths;
	}
	
	public List<String> getLibFolders() {
		return toAbsolutePaths(getValues(TestDataColumn.LIB_FOLDERS));
	}
	
	public static enum TestDataColumn {
		PROJECT_NAME (false), // String
		BUG_NUMBER (false), // String
		CLASS_PATH (true), // List<String>
		LIB_FOLDERS (true), 
		TEST_CLASSES (true), // List<String>
		ANALYZING_CLASSES (true),
		ANALYZING_PACKAGES (true), // List<String>
		EXPECTED_BUG_LOCATION (true); // List<String>
		
		private boolean multi;
		
		private TestDataColumn(boolean multi) {
			this.multi = multi;
		}
		
		public static String[] allColumns() {
			TestDataColumn[] values = values();
			String[] cols = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				cols[i] = values[i].name();
			}
			return cols;
		}
	}

}
