/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.gan.vm.BranchDataSet;
import learntest.core.gan.vm.BranchDataSet.Category;
import learntest.core.gan.vm.JsLabel;


/**
 * @author LLT
 *
 */
public class OutputData {
	private static Logger log = LoggerFactory.getLogger(OutputData.class);
	
	private RequestType requestType;
	private BranchDataSet generatedDataSet;

	public OutputData(RequestType requestType) {
		this.requestType = requestType;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	
	public BranchDataSet getGeneratedDataSet() {
		return generatedDataSet;
	}
	
	public void setGeneratedDataSet(BranchDataSet generatedDataSet) {
		this.generatedDataSet = generatedDataSet;
	}

	public static OutputData boundaryRemainingOuput(BufferedReader br) {
		OutputData outputData = new OutputData(RequestType.BOUNDARY_REMAINING);
		BranchDataSet dataSet = new BranchDataSet();
		outputData.generatedDataSet = dataSet;
		
		String jsonStr;
		try {
			jsonStr = br.readLine();
			JSONObject obj = new JSONObject(jsonStr);
			JSONObject datasetObj = obj.getJSONObject(JsLabel.DATASET);
			parseDatapoints(dataSet, datasetObj, Category.TRUE);
			parseDatapoints(dataSet, datasetObj, Category.FALSE);
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		
		return outputData;
	}
	
	public static void parseDatapoints(BranchDataSet dataSet, JSONObject datasetObj, Category category) {
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

}
