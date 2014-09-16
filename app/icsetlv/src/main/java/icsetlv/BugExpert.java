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
		/*--------------------*/
		
		for (String key : allLongsVals.keySet()) {
			double[] ds = allLongsVals.get(key);
			if (key.contains("result")) {
				ds = multi(ds, 10);
			}
//			allLongsVals.put(key, scale(ds));
		}
		/*--------------------*/
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
	
	private static double[] multi(double[] ds, int f) {
		for (int i = 0; i < ds.length; i++) {
			ds[i] = ds[i] * f;
		}
		return ds;
	}

	private static double[] scale(double[] ds) {
		if (ds.length <= 0) {
			return ds;
		}
		double[] result = new double[ds.length];
		double max = ds[0];
		double min = ds[0];
		for (double val : ds) {
			if (max < val) {
				max = val;
			}
			if (min > val) {
				min = val;
			}
		}
		double d = max - min;
		for (int i = 0; i < ds.length; i++) {
			result[i] = (ds[i] - min)/d;
		}
		return result;
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
