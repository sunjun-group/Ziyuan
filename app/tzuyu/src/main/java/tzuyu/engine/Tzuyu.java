/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.iface.algorithm.Learner;

/**
 * @author LLT Driver of the Tzuyu engine.
 */
public class Tzuyu {

	private Learner learner;
	private TzProject project;
	private TzReportHandler reporter;

	public Tzuyu(TzProject project, TzReportHandler reporter) {
		this.project = project;
		this.reporter = reporter;
		learner = TzuyuAlgorithmFactory.getLearner(project);
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
