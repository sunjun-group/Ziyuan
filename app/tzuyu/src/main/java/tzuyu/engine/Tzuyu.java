/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import libsvm.libsvm.svm;
import lstar.IReportHandler.OutputType;
import lstar.LStar;
import lstar.LStarException;
import refiner.TzuYuRefiner;
import tester.ITCGStrategy;
import tester.TzuYuTester;
import tzuyu.engine.algorithm.iface.Learner;
import tzuyu.engine.algorithm.iface.Refiner;
import tzuyu.engine.algorithm.iface.Teacher;
import tzuyu.engine.algorithm.iface.Tester;
import tzuyu.engine.iface.ILogger;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.iface.IReferencesAnalyzer;
import tzuyu.engine.iface.ITzManager;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.iface.TzuyuEngine;
import tzuyu.engine.lstar.TeacherImpl;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.exception.ReportException;
import tzuyu.engine.model.exception.TzException;

/**
 * @author LLT 
 * Driver of the Tzuyu engine.
 */
public class Tzuyu implements TzuyuEngine, ITzManager<TzuYuAlphabet> {

	/* learner */
	private Learner<TzuYuAlphabet> learner;
	/* teacher */
	private Teacher<TzuYuAlphabet> teacher;
	/* tester */
	private Tester tester;
	private ITCGStrategy tcgStrategy;
	private IReferencesAnalyzer refAnalyzer;
	/* refiner */
	private Refiner<TzuYuAlphabet> refiner;
	
	private TzClass project;
	private TzReportHandler reporter;
	private IPrintStream tzOut;
	private static ILogger<?> logger;

	public Tzuyu(TzClass project, TzReportHandler reporter,
			IReferencesAnalyzer refAnalyzer) {
		init(project, reporter, refAnalyzer);
	}

	private void init(TzClass project, TzReportHandler reporter,
			IReferencesAnalyzer refAnalyzer) {
		this.project = project;
		this.reporter = reporter;
		this.refAnalyzer = refAnalyzer;
		// set print stream for output
		// svm
		svm.svm_set_print_string_function(reporter.getOutStream(OutputType.SVM));
		// system out, or error (output from the tested classes)
		if (reporter.getSystemOutStream() != null) {
			System.setOut(reporter.getSystemOutStream());
			System.setErr(reporter.getSystemOutStream());
		}
		tzOut = reporter.getOutStream(OutputType.TZ_OUTPUT);
		tester = new TzuYuTester(this);
		refiner = new TzuYuRefiner(this);
		teacher = new TeacherImpl(this);
		learner = new LStar<TzuYuAlphabet>(this);
		logger = reporter.getLogger();
	}

	/**
	 * this function execute the main flow of tzuyu engine.
	 */
	public void run() throws ReportException, InterruptedException, TzException {
		tzOut.writeln(
				"============Start of Statistics for" + 
				project.getTarget().getSimpleName()+ "============");
		// divide all class methods into 2 groups: static and non-static
		// and start learning separately.
		TzuYuAlphabet alphabet = TzuYuAlphabet.forNonStaticGroup(project);
		// UNCOMMENT TO TEST STATIC GROUP
//		alphabet = TzuYuAlphabet.forStaticGroup(project);
		// UNCOMMENT TO TEST THE WHOLE CLASS
//		alphabet = TzuYuAlphabet.forClass(project);
		alphabet.setOutStream(tzOut);
		run(alphabet);
		try {
			learner.startLearning(alphabet);
		} catch (LStarException e) {
			getLogger().logEx(e, "Error when running Lstar module");
			throw new TzException(e);
		} catch (InterruptedException e) {
			tzOut.writeln("").writeln("The progress has been cancelled!");
			throw e;
		} finally {
			learner.report(reporter);
			reporter.comit();
		}
		tzOut.writeln("================= End of TzuYu run ====================");
	}

	private void run(TzuYuAlphabet alphabet) throws ReportException, InterruptedException {
	}

	public Learner<TzuYuAlphabet> getLearner() {
		return learner;
	}

	@Override
	public Teacher<TzuYuAlphabet> getTeacher() {
		return teacher;
	}

	public Tester getTester() {
		return tester;
	}

	public TzClass getProject() {
		return project;
	}

	public TzReportHandler getReporter() {
		return reporter;
	}

	@Override
	public IReferencesAnalyzer getRefAnalyzer() {
		return refAnalyzer;
	}

	@Override
	public Refiner<TzuYuAlphabet> getRefiner() {
		return refiner;
	}
	
	@Override
	public ITCGStrategy getTCGStrategy() {
		return tcgStrategy;
	}
	
	@Override
	public IPrintStream getOutStream() {
		return tzOut;
	}
	
	@Override
	public ILogger<?> getLogger() {
		return logger;
	}
	
	public static ILogger<?> getLog() {
		return logger;
	}

	@Override
	public void checkProgress() throws InterruptedException {
		if (reporter.isInterrupted()) {
			throw new InterruptedException("User cancel request!!");
		}
	}
}
