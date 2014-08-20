/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.iface.IBugExpert;
import icsetlv.svm.LibSVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 *  @author Jingyi
 *
 */
public class BugExpert implements IBugExpert {
	
	@Override
	public boolean isRootCause(List<BreakpointValue> passValues,
			List<BreakpointValue> failValues) {
		if (passValues.isEmpty() || failValues.isEmpty()) {
			return false;
		}
		LibSVM svmer = new LibSVM();
		svmer.buildClassifier(buildDataset(passValues, failValues));
		Metric metric = new Metric(svmer.modelAccuracy());
		return bugFoundOrNot(metric);
	}

	public static Dataset buildDataset(List<BreakpointValue> passValues,
			List<BreakpointValue> failValues) {
		Dataset dataset = new DefaultDataset();
		int failStartIdx = passValues.size();
		List<BreakpointValue> passFailVals = new ArrayList<BreakpointValue>(
				passValues);
		passFailVals.addAll(failValues);

		Map<String, double[]> allLongsVals = new HashMap<String, double[]>();
		for (int i = 0; i < passFailVals.size(); i++) {
			BreakpointValue bkpVals = passFailVals.get(i);
			bkpVals.retrieveValue(allLongsVals, i, passFailVals.size());
		}
		int featureSize = allLongsVals.keySet().size();
		for (int i = 0; i < passFailVals.size(); i++) {
			double[] lineVals = new double[featureSize];
			int j = 0;
			for (String key : allLongsVals.keySet()) {
				lineVals[j++] = allLongsVals.get(key)[i];
			}
			Instance instance = new DenseInstance(lineVals);
			if (i < failStartIdx) {
				instance.setClassValue("positive");
			} else {
				instance.setClassValue("negative");
			}
			dataset.add(instance);
		}
		return dataset;
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
