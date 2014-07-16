/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import lstar.IReportHandler;
import sav.common.core.iface.ILogger;
import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.junit.JFileWriter;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.Pair;

/**
 * @author LLT
 *
 */
public abstract class TzReportHandler implements IReportHandler<TzuYuAlphabet>{
	private TzConfiguration config;
	private List<File> testFiles;
	
	public TzReportHandler(TzConfiguration config) {
		this.config = config;
	}
	
	public List<File> writeJUnitTestCases(List<Sequence> allTestCases,
			TzClass project, boolean passTcs, int firstFileIdx)
			throws TzException {
		JFileWriter writer = new JFileWriter(project.getConfiguration(),
				project.getClassName(), passTcs);
		writer.setOutStream(getOutStream(OutputType.JUNIT_GENERATION));
		testFiles = writer.createJUnitTestFiles(allTestCases, firstFileIdx);
		return testFiles;
	}
	
	/*
	 *  pair.a : pass testcases,
	 *  pair.b : fail testcases
	 **/
	public void writeTestCases(Pair<List<Sequence>, List<Sequence>> allSeqs,
			TzClass project) throws TzException {
		getOutStream(OutputType.TZ_OUTPUT).println(
				"------ Start generating junit test -------");
		int firstFileIdx = 0; 
		if (!allSeqs.a.isEmpty()) {
			firstFileIdx = writeJUnitTestCases(allSeqs.a, project, true,
					firstFileIdx).size();
		}
		if (!allSeqs.b.isEmpty()) {
			writeJUnitTestCases(allSeqs.b, project, false, firstFileIdx);
		}
	}
	
	public List<File> getTestFiles() {
		return testFiles;
	}

	public PrintStream getSystemOutStream() {
		return Globals.tcExStream;
	}
	
	public abstract ILogger<?> getLogger();

	public boolean isInterrupted() {
		return false;
	}
}
