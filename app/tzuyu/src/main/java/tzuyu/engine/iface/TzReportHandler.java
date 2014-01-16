/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lstar.ReportHandler;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.TzClass;
import tzuyu.engine.experiment.JUnitFileWriter;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.utils.Globals;

/**
 * @author LLT
 *
 */
public abstract class TzReportHandler implements ReportHandler<TzuYuAlphabet>{
	private TzConfiguration config;
	
	public TzReportHandler(TzConfiguration config) {
		this.config = config;
	}
	
	public List<File> writeJUnitTestCases(List<Sequence> allTestCases, TzClass project) {
		List<File> junitFiles = new ArrayList<File>();

		String targetClass = getTargetClassName(project);
		String dir = Globals.userDir + Globals.fileSep + "testcases";
		int size = allTestCases.size();
		// We group all test cases into one file. The file name ends with the
		// suffix of the file number.
		JUnitFileWriter writer = new JUnitFileWriter(dir, "", targetClass, size);
		writer.config(config);
		junitFiles.addAll(writer.createJUnitTestFiles(allTestCases));
		return junitFiles;
	}
	
	public void writeTestCases(List<Sequence> allTestCases, TzClass project) {
		writeJUnitTestCases(allTestCases, project);
	}
	

	protected String getTargetClassName(TzClass project) {
		return project.getClassName();
	}
}
