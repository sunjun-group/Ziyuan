/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gan.vm;

import java.util.List;

import learntest.core.gan.vm.NodeDataSet.Category;
import sav.common.core.SavException;
import sav.strategies.vm.interprocess.python.PythonVmConfiguration;
import sav.strategies.vm.interprocess.python.PythonVmRunner;

/**
 * @author LLT
 *
 */
public class GanMachine {
	private GanInputWriter inputWriter;
	private GanOutputReader outputReader;
	private PythonVmRunner vmRunner;
	private long timeout = -1;
	
	public GanMachine() {
		// init vm configuration
		inputWriter = new GanInputWriter();
		outputReader = new GanOutputReader();
	}
	
	public void start() throws SavException {
		inputWriter.open();
		outputReader.open();
		vmRunner = new PythonVmRunner(inputWriter, outputReader, true);
		vmRunner.setTimeout(timeout);
		PythonVmConfiguration vmConfig = new PythonVmConfiguration();
		vmConfig.setPythonHome("/Users/lylytran/tensorflow/bin/python");
		vmConfig.setLaunchClass("/Users/lylytran/Projects/Ziyuan-branches/NeuralTest/neuraltest/GanVM.py");
		vmRunner.start(vmConfig);
	}
	
	public void stop() {
		vmRunner.stop();
	}
	
	public void setVmTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public void startTrainingMethod(String methodName) {
		inputWriter.request(GanInput.createStartMethodRequest(methodName));
	}
	
	public void train(int nodeIdx, NodeDataSet trainingData) {
		inputWriter.request(GanInput.createTrainingRequest(toNodeId(nodeIdx), trainingData));
	}

	private String toNodeId(int nodeIdx) {
		return String.valueOf(nodeIdx);
	}
	
	public NodeDataSet requestData(int nodeIdx, List<String> labels, Category category) {
		Category[] categories;
		if (category == null) {
			categories = Category.values();
		} else {
			categories = new Category[] { category };
		}
		inputWriter.request(GanInput.createGeneratingRequest(toNodeId(nodeIdx), labels, categories));
		GanOutput output = outputReader.readOutput();
		if (output == null) {
			return null;
		}
		return output.getGeneratedDataSet();
	}
}
