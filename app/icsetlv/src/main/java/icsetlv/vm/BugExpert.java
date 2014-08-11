/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.vm;

import icsetlv.common.dto.VariablesExtractorResult.BreakpointResult;
import icsetlv.iface.IBugExpert;
import icsetlv.svm.DatasetBuilder;
import icsetlv.svm.LibSVM;

/**
 * @author LLT
 *
 */
public class BugExpert implements IBugExpert {
	
	@Override
	public boolean isRootCause(BreakpointResult bkp) {
		if (bkp.getFailValues().isEmpty() || bkp.getPassValues().isEmpty()) {
			return false;
		}
		DatasetBuilder db = new DatasetBuilder(bkp);
		LibSVM svmer = new LibSVM();
		svmer.buildClassifier(db.buildDataset());
		Metric metric = new Metric(svmer.modelAccuracy());
		return bugFoundOrNot(metric);
	}

	/*
	 * Metric for assertion generation using svm For now, we use classification
	 * accuracy and set a hard threshold
	 */
	private class Metric {
		double modelAccuracy;

		public Metric(double macc) {
			this.modelAccuracy = macc;
		}
	}

	/*
	 * Check if we can still generate assertions. Set the threshold for
	 * classification accuracy
	 */
	private boolean bugFoundOrNot(Metric metric) {
		if (metric.modelAccuracy > 0.7) {
			return false;
		}
		return true;
	}
}
