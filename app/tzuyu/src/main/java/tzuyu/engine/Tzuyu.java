/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import lstar.LStar;
import lstar.LStarException;
import refiner.TzuYuRefiner;
import tester.ITCGStrategy;
import tester.TzuYuTester;
import tzuyu.engine.algorithm.iface.Learner;
import tzuyu.engine.algorithm.iface.Refiner;
import tzuyu.engine.algorithm.iface.Teacher;
import tzuyu.engine.algorithm.iface.Tester;
import tzuyu.engine.iface.IAlgorithmFactory;
import tzuyu.engine.iface.IReferencesAnalyzer;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.iface.TzuyuEngine;
import tzuyu.engine.lstar.TeacherImpl;
import tzuyu.engine.model.TzuYuAlphabet;

/**
 * @author LLT 
 * Driver of the Tzuyu engine.
 */
public class Tzuyu implements TzuyuEngine, IAlgorithmFactory<TzuYuAlphabet> {

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

	public Tzuyu(TzClass project, TzReportHandler reporter,
			IReferencesAnalyzer refAnalyzer) {
		init(project, reporter, refAnalyzer);
	}

	private void init(TzClass project, TzReportHandler reporter,
			IReferencesAnalyzer refAnalyzer) {
		this.project = project;
		this.reporter = reporter;
		this.refAnalyzer = refAnalyzer;
		tester = new TzuYuTester(this);
		refiner = new TzuYuRefiner();
		teacher = new TeacherImpl(this);
		learner = new LStar<TzuYuAlphabet>(this);
	}

	/**
	 * this function execute the main flow of tzuyu engine.
	 */
	public void run() {
		reporter.getLogger().info("============Start of Statistics for",
				project.getTarget().getSimpleName(), "============");
		// TODO [LLT]: time measuring.
		try {
			learner.startLearning(new TzuYuAlphabet(project));
		} catch (LStarException e) {
			// TODO [LLT]: exception handling.
			reporter.getLogger().info("Exception::", e.getType());
		}
		learner.report(reporter);
		reporter.done();
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
}
