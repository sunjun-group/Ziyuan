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
 * @author LLT
 * Driver of the Tzuyu engine.
 */
public class Tzuyu {

	private Learner learner;
	private TzProject project;
	private TzConfiguration config;
	private TzReportHandler reporter;

	public Tzuyu(TzProject project, TzConfiguration config,
			TzReportHandler reporter) {
		learner = TzuyuAlgorithmFactory.getLearner();
		this.project = project;
		this.config = config;
		this.reporter = reporter;
	}

	/**
	 * this function execute the main flow of tzuyu engine.
	 */
	public void run() {
		learner.startLearning();
		learner.report(reporter);
	}
}
