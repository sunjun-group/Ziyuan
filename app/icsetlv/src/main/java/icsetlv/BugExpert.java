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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.core.Category;
import libsvm.core.KernelType;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.core.MachineType;
import libsvm.core.Parameter;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 * @author Jingyi
 * 
 */
public class BugExpert implements IBugExpert {

	private static final double ACCURACY_THRESHOLD = 0.7;
	private static final String POSITIVE = "positive";
	private static final String NEGATIVE = "negative";

	@Override
	public boolean isRootCause(List<BreakpointValue> passValues, List<BreakpointValue> failValues) {
		if (passValues.isEmpty() || failValues.isEmpty()) {
			return false;
		}

		final Machine machine = new Machine().setParameter(new Parameter()
				.setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR).setC(1.0)
				.setCacheSize(100.0).setEps(1e-3).setUseShrinking(true).setPredictProbability(true)
				.setNrWeight(0).setWeight(new double[0]).setWeightLabel(new int[0]));

		// TODO NPN enhance this part
		// Build up the map between a variable and its values (for different
		// test cases)
		final int passTestCaseSize = passValues.size();
		final int failedTestCaseSize = failValues.size();
		final int dataSize = passTestCaseSize + failedTestCaseSize;
		final Map<String, double[]> variableValueMap = new HashMap<String, double[]>();
		int i = 0;
		for (BreakpointValue value : passValues) {
			value.retrieveValue(variableValueMap, i++, dataSize);
		}
		for (BreakpointValue value : failValues) {
			value.retrieveValue(variableValueMap, i++, dataSize);
		}
		// Number of all available variables a breakpoint
		final int numberOfFeatures = variableValueMap.keySet().size();

		machine.setNumberOfFeatures(numberOfFeatures);

		// Build data points
		List<DataPoint> dataPoints = new ArrayList<DataPoint>(dataSize);
		i = 0;
		for (int j = 0; j < passTestCaseSize; j++) {
			machine.addDataPoint(Category.POSITIVE, getVariableValueAtLine(variableValueMap, i++));
		}
		for (int j = 0; j < failedTestCaseSize; j++) {
			machine.addDataPoint(Category.NEGATIVE, getVariableValueAtLine(variableValueMap, i++));
		}

		// Train SVM
		return bugFoundOrNot(new Metric(machine.addDataPoints(dataPoints).train()
				.getModelAccuracy()));
	}

	private double[] getVariableValueAtLine(final Map<String, double[]> allLongsVals,
			final int index) {
		final int numberOfFeatures = allLongsVals.keySet().size();
		double[] lineVals = new double[numberOfFeatures];
		int j = 0;
		for (String key : allLongsVals.keySet()) {
			lineVals[j++] = allLongsVals.get(key)[index];
		}
		return lineVals;
	}

	public static Dataset buildDataset(List<BreakpointValue> passValues,
			List<BreakpointValue> failValues) {
		Dataset dataset = new DefaultDataset();
		int failStartIdx = passValues.size();
		List<BreakpointValue> passFailVals = new ArrayList<BreakpointValue>(passValues);
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
			// allLongsVals.put(key, scale(ds));
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
				instance.setClassValue(POSITIVE);
			} else {
				instance.setClassValue(NEGATIVE);
			}
			dataset.add(instance);
		}

		return dataset;
	}

	public static void addDataPoints(Machine machine, List<BreakpointValue> passValues,
			List<BreakpointValue> failValues) {
		final int size = passValues.size() + failValues.size();
		Map<String, double[]> allLongsVals = new HashMap<String, double[]>(size);
		for (int i = 0; i < passValues.size(); i++) {
			addDataPoint(machine, passValues, size, allLongsVals, i, Category.POSITIVE);
		}

		for (int i = 0; i < failValues.size(); i++) {
			addDataPoint(machine, failValues, size, allLongsVals, i, Category.NEGATIVE);
		}
	}

	private static void addDataPoint(Machine machine, List<BreakpointValue> values,
			final int size, Map<String, double[]> allLongsVals, int i, Category category) {
		BreakpointValue value = values.get(i);
		value.retrieveValue(allLongsVals, i, size);

		double[] lineVals = new double[machine.getNumberOfFeatures()];
		int j = 0;
		for (String key : allLongsVals.keySet()) {
			lineVals[j++] = allLongsVals.get(key)[i];
		}

		machine.addDataPoint(category, lineVals);
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
			result[i] = (ds[i] - min) / d;
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
		// TODO NPN
		// The idea is that if we can clearly divide the vectors then the line
		// at the breakpoint is bug-affected and likely not the cause of the
		// bug. We need to backtrack that line to find the first line at which
		// it is not possible to divide the vectors.
		return metric.modelAccuracy < ACCURACY_THRESHOLD;
	}
}
