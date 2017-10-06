/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gan.vm;

import java.io.PrintWriter;
import java.util.List;

import org.json.JSONObject;

import learntest.core.gan.vm.GanInputWriter.IGanInput;
import learntest.core.gan.vm.GanInputWriter.RequestType;
import learntest.core.gan.vm.NodeDataSet.Category;

/**
 * @author LLT
 *
 */
public class GanInput implements IGanInput {
	private RequestType type;
	private JSONObject obj = new JSONObject();
	
	public GanInput(RequestType type) {
		this.type = type;
	}
	
	@Override
	public void writeData(PrintWriter pw) {
		pw.println(obj);
	}

	@Override
	public RequestType getRequestType() {
		return type;
	}

	public static GanInput createStartMethodRequest(String methodName) {
		GanInput input = new GanInput(RequestType.START_TRAINING_FOR_METHOD);
		input.obj.put(JsLabel.METHOD_ID, methodName);
		return input;
	}
	
	private GanInput appendNodeId(String nodeId) {
		obj.put(JsLabel.NODE_ID, nodeId);
		return this;
	}
	
	private GanInput appendLabel(List<String> labels) {
		obj.put(JsLabel.LABELS, labels);
		return this;
	}
	
	public static GanInput createTrainingRequest(String nodeId, NodeDataSet dataSet) {
		GanInput input = new GanInput(RequestType.TRAIN)
					.appendNodeId(nodeId);
		input.obj.put(JsLabel.NODE_ID, nodeId);
		input.obj.put(JsLabel.LABELS, dataSet.getLabels());
		input.obj.put(JsLabel.DATASET, dataSet.getDataset());
		return input;
	}
	

	public static GanInput createGeneratingRequest(String nodeId, List<String> labels, Category[] categories) {
		GanInput input = new GanInput(RequestType.GENERATE_DATA)
					.appendNodeId(nodeId)
					.appendLabel(labels);
		input.obj.put(JsLabel.CATEGORY, categories);
		return input;
	}
}
