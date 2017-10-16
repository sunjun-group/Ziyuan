/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gan.vm;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import learntest.core.gan.vm.GanInputWriter.RequestType;
import learntest.core.gan.vm.NodeDataSet.Category;

/**
 * @author LLT
 *
 */
public class GanOutput {
	private RequestType requestType;
	private NodeDataSet generatedDataSet;

	public GanOutput(RequestType requestType) {
		this.requestType = requestType;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	
	public void parseJson(String jsonStr) {
		JSONObject obj = new JSONObject(jsonStr);
		NodeDataSet dataSet = new NodeDataSet();
		dataSet.setNodeId(obj.getString(JsLabel.NODE_ID));
//		JSONArray arr = obj.getJSONArray(JsLabel.LABELS);
//		List<String> labels = new ArrayList<String>(arr.length());
//		for (Iterator<Object> it = arr.iterator(); it.hasNext();) {
//			labels.add((String) it.next());
//		}
//		dataSet.setLabels(labels);
		JSONObject datasetObj = obj.getJSONObject(JsLabel.DATASET);
		parseDatapoints(dataSet, datasetObj, Category.TRUE);
		parseDatapoints(dataSet, datasetObj, Category.FALSE);
		this.generatedDataSet = dataSet;
	}

	public void parseDatapoints(NodeDataSet dataSet, JSONObject datasetObj, Category category) {
		if (datasetObj.has(category.name())) {
			JSONArray arr = datasetObj.getJSONArray(category.name());
			List<double[]> datapoints = new ArrayList<double[]>(arr.length());
			for (int i = 0; i < arr.length(); i++) {
				JSONArray dpArr = arr.getJSONArray(i);
				double[] dps = new double[dpArr.length()];
				for (int j = 0; j < dpArr.length(); j++) {
					dps[j] = dpArr.getDouble(j);
				}
				datapoints.add(dps);
			}
			dataSet.setDatapoints(category, datapoints);
		}
	}
	
	public NodeDataSet getGeneratedDataSet() {
		return generatedDataSet;
	}
	
	public void setGeneratedDataSet(NodeDataSet generatedDataSet) {
		this.generatedDataSet = generatedDataSet;
	}

}
