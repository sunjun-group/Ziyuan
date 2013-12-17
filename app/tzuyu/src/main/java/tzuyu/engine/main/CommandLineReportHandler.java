/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.main;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.experiment.JUnitFileWriter;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.utils.Globals;

/**
 * @author LLT
 * 
 */
public class CommandLineReportHandler implements TzReportHandler {
	private TzConfiguration config;
	
	public CommandLineReportHandler(TzConfiguration config) {
		this.config = config;
	}
	
	@Override
	public void reportDFA(DFA lastDFA, TzuYuAlphabet sigma) {
		saveDFA(lastDFA, sigma.getProject().getConfiguration());
	}

	@Override
	public void writeTestCases(List<Sequence> allTestCases) {
		writeJUnitTestCases(allTestCases);
	}

	private void saveDFA(DFA dfa, TzConfiguration config) {
		if (dfa != null) {
			String dot = dfa.createDotRepresentation();
			try {
				String fileName = config.getAbsoluteAddress(getTargetClassName() + ".dot");
				FileWriter writer = new FileWriter(fileName);
				writer.write(dot);
				writer.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private String getTargetClassName() {
//		Analytics.getTarget().getSimpleName();
		return "temp";
	}

	public List<File> writeJUnitTestCases(List<Sequence> allTestCases) {
		List<File> junitFiles = new ArrayList<File>();

		String targetClass = getTargetClassName();
		String dir = Globals.userDir + Globals.fileSep + "testcases";
		int size = allTestCases.size();
		// We group all test cases into one file. The file name ends with the
		// suffix of the file number.
		JUnitFileWriter writer = new JUnitFileWriter(dir, "", targetClass, size);
		writer.config(config);
		junitFiles.addAll(writer.createJUnitTestFiles(allTestCases));
		return junitFiles;
	}
	
	
}
