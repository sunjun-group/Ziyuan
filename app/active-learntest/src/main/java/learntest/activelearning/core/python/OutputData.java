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
	private Dataset dataSet;

	public OutputData(RequestType requestType) {
		this.requestType = requestType;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	
	public static OutputData boundaryRemainingOuput(BufferedReader br) {
		OutputData outputData = new OutputData(RequestType.BOUNDARY_REMAINING);
		
		String jsonStr;
		try {
			jsonStr = br.readLine();
			JSONObject obj = new JSONObject(jsonStr);
			Dataset dataSet = new Dataset(obj.getString(JsLabels.PATH_ID));
			dataSet.setCoveredData(parseDatapoints(obj.getJSONArray(JsLabels.COVERED_DATA_POINTS)));
			dataSet.setUncoveredData(parseDatapoints(obj.getJSONArray(JsLabels.UNCOVERED_DATA_POINTS)));
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		
		return outputData;
	}
	
	public static List<double[]> parseDatapoints(JSONArray arr) {
		List<double[]> datapoints = new ArrayList<double[]>(arr.length());
		for (int i = 0; i < arr.length(); i++) {
			JSONArray dpArr = arr.getJSONArray(i);
			double[] dps = new double[dpArr.length()];
			for (int j = 0; j < dpArr.length(); j++) {
				dps[j] = dpArr.getDouble(j);
			}
			datapoints.add(dps);
		}
		return datapoints;
	}
	
	public Dataset getDataSet() {
		return dataSet;
	}

}
