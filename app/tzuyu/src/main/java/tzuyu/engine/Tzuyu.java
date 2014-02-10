/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import lstar.LStarException;
import lstar.Teacher;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.iface.TzuyuEngine;
import tzuyu.engine.iface.algorithm.Learner;
import tzuyu.engine.model.TzuYuAlphabet;

/**
 * @author LLT 
 * Driver of the Tzuyu engine.
 */
public class Tzuyu implements TzuyuEngine {

	private Learner<TzuYuAlphabet> learner = TzFactory.getLearner();
	private Teacher<TzuYuAlphabet> teacher = TzFactory.getTeacher();
	private TzClass project;
	private TzReportHandler reporter;

	public Tzuyu() {

	}

	public Tzuyu(TzClass project, TzReportHandler reporter) {
		init(project, reporter);
	}

	private void init(TzClass project, TzReportHandler reporter) {
		this.project = project;
		this.reporter = reporter;
		learner.setTeacher(teacher);
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
}
