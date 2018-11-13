/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.testgeneration.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.testgeneration.Dataset;


/**
 * @author LLT
 *
 */
public class Message {
	private static Logger log = LoggerFactory.getLogger(Message.class);
	
	private RequestType requestType;
	private Object messageBody;

	public Message(RequestType requestType) {
		this.requestType = requestType;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	
	public static Message parseTrainingFinish(BufferedReader br) {
		Message message = new Message(RequestType.$TRAINING_FINISH);
		return message;
	}
	
	public static Message parseBoundaryExplorationFinish(BufferedReader br) {
		Message message = new Message(RequestType.$BOUNDARY_EXPLORATION);
		return message;
	}
	
	public static Message parseUnlabeledDataPoints(BufferedReader br) {
		String jsonStr;
		try {
			jsonStr = br.readLine();
			DataPoints values = JSONParser.parseUnlabeledDataPoints(jsonStr);
			Message message = new Message(RequestType.$REQUEST_LABEL);
			message.messageBody = values;
			
			return message;
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		
		return null;
	}
	
	
	public static Message boundaryRemainingOuput(BufferedReader br) {
		Message outputData = new Message(RequestType.$BOUNDARY_REMAINING);
		
		String jsonStr;
		try {
			jsonStr = br.readLine();
			JSONObject obj = new JSONObject(jsonStr);
			Dataset dataSet = new Dataset(obj.getString(JSLabels.BRANCH_ID));
			dataSet.setCoveredData(parseDatapoints(obj.getJSONArray(JSLabels.COVERED_DATA_POINTS)));
			dataSet.setUncoveredData(parseDatapoints(obj.getJSONArray(JSLabels.UNCOVERED_DATA_POINTS)));
//			outputData.dataSet = dataSet;
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		
		return outputData;
	}
	
	public static Message parseModelCheck(BufferedReader br) {
		Message outputData = new Message(RequestType.$MODEL_CHECK);
		String jsonStr;
		try {
			jsonStr = br.readLine();
			JSONObject obj = new JSONObject(jsonStr);
			String existence = obj.getString(JSLabels.EXISTENCE);
			outputData.messageBody = existence;
			return outputData;
//			outputData.dataSet = dataSet;
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		
		return null;
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

	public Object getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(Object message) {
		this.messageBody = message;
	}

	public static Message parseBoundaryExplorationPoints(BufferedReader br) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Message parseUnmaskedDataPoints(BufferedReader br) {
		String jsonStr;
		try {
			jsonStr = br.readLine();
			DataPoints values = JSONParser.parseUnmaskedDataPoints(jsonStr);
			Message message = new Message(RequestType.$REQUEST_MASK_RESULT);
			message.messageBody = values;
			
			return message;
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		
		return null;
	}

	public static Message parseBoundaryRemainingDataPoints(BufferedReader br) {
		String jsonStr;
		try {
			jsonStr = br.readLine();
			DataPoints values = JSONParser.parseBoundaryRemainingPoints(jsonStr);
			Message message = new Message(RequestType.$SEND_BOUNDARY_REMAINING_POINTS);
			message.messageBody = values;
			
			return message;
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
		return null;
	}


}