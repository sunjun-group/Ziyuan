/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

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

	private Learner<TzuYuAlphabet> learner = TzuyuFactory.getLearner();
	private Teacher<TzuYuAlphabet> teacher = TzuyuFactory.getTeacher();
	private TzProject project;
	private TzReportHandler reporter;

	public Tzuyu() {

	}

	public Tzuyu(TzProject project, TzReportHandler reporter) {
		init(project, reporter);
	}

	private void init(TzProject project, TzReportHandler reporter) {
		this.project = project;
		this.reporter = reporter;
		learner.setTeacher(teacher);
		learner.setAlphabet(new TzuYuAlphabet(project));
	}

	/**
	 * this function execute the main flow of tzuyu engine.
	 */
	public void run() {
		TzLogger.log().info("============Start of Statistics for",
				project.getTarget().getSimpleName(), "============");
		// TODO [LLT]: time measuring.
		learner.startLearning();
		learner.report(reporter);
	}
}
