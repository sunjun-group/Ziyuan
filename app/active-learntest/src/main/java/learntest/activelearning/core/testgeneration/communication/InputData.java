package learntest.activelearning.core.testgeneration.communication;

import java.io.PrintWriter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointValue;
import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.testgeneration.Dataset;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class InputData /*implements IInputData*/ {
	private Logger log = LoggerFactory.getLogger(InputData.class);
	private RequestType requestType;
	private JSONObject obj = new JSONObject();

//	@Override
	public void writeData(PrintWriter pw) {
		if (obj == null) {
			return;
		}
		log.debug("write data: {}, {}", requestType, obj);
		pw.println(String.valueOf(requestType));
		pw.println(obj);
//		pw.flush();
	}
	
	public static InputData createStartMethodRequest(String methodId) {
		InputData inputData = new InputData();
		inputData.requestType = RequestType.$TRAINING;
		inputData.obj.put(JSLabels.METHOD_ID, methodId);
		return inputData;
	}

	public static InputData createTrainingRequest(MethodInfo targetMethod, Branch branch, List<TestInputData> positiveData, 
			List<TestInputData> negativeData, int pointNumberLimit) {
		InputData inputData = new InputData();
		inputData.requestType = RequestType.$TRAINING;
		
		inputData.obj.put(JSLabels.METHOD_ID, targetMethod.getMethodId());
		inputData.obj.put(JSLabels.BRANCH_ID, branch.getBranchID());
		inputData.obj.put(JSLabels.POINT_NUMBER_LIMIT, pointNumberLimit);
		
		JSONArray positiveArray = transferToJsonArray(positiveData);
		inputData.obj.put(JSLabels.POSITIVE_DATA, positiveArray);
		
		JSONArray negativeArray = transferToJsonArray(negativeData);
		inputData.obj.put(JSLabels.NEGATIVE_DATA, negativeArray);
		
		return inputData;
	}
	
	public static InputData createBoundaryExplorationRequest(String methodID, Branch branch, List<TestInputData> testData) {
		InputData inputData = new InputData();
		inputData.requestType = RequestType.$BOUNDARY_EXPLORATION;
		inputData.obj.put(JSLabels.METHOD_ID, methodID);
		
		String branchID = branch==null ? "EMPTY" : branch.getBranchID(); 
		inputData.obj.put(JSLabels.BRANCH_ID, branchID);
		
		JSONArray jArray = transferToJsonArray(testData);
		inputData.obj.put(JSLabels.TEST_DATA, jArray);
		
		return inputData;
	}
	
	public static InputData createModelCheckRequest(Branch parentBranch, String methodID) {
		InputData inputData = new InputData();
		inputData.requestType = RequestType.$MODEL_CHECK;
		inputData.obj.put(JSLabels.BRANCH_ID, parentBranch.getBranchID());
		inputData.obj.put(JSLabels.METHOD_ID, methodID);
		
		return inputData;
	}
	
	public static InputData transferToJSON(MethodInfo targetMethod, DataPoints points) {
		InputData inputData = new InputData();
		inputData.requestType = RequestType.$SEND_LABEL;
		JSONArray array = new JSONArray();
//		inputData.obj.put(JsLabels.METHOD_ID, targetMethod.getMethodFullName());
		
		for(int i=0; i<points.values.size(); i++){
			JSONArray point = new JSONArray();
			for(int j=0; j<points.varList.size(); j++){
				ExecVar var = points.getVarList().get(j);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put(JSLabels.NAME, var.getVarId());
				jsonObj.put(JSLabels.VALUE, points.values.get(i)[j]);
				jsonObj.put(JSLabels.TYPE, points.varList.get(j).getType());
				boolean label = points.getLabels().get(i);
				jsonObj.put(JSLabels.LABEL, label);
				point.put(jsonObj);
			}
			array.put(point);
		}
		
		inputData.obj.put(JSLabels.RESULT, array);
		
		return inputData;
	}

	private static JSONArray transferToJsonArray(List<TestInputData> positiveData) {
		JSONArray arrayObj = new JSONArray();
		for(TestInputData testInput: positiveData){
			
			BreakpointValue bpv = testInput.getInputValue();
			JSONArray positiveObj = new JSONArray();
			for(int i=0; i<bpv.getChildren().size(); i++){
				ExecValue value = bpv.getChildren().get(i);
				JSONObject param = new JSONObject();
				param.put(JSLabels.TYPE, value.getType());
				param.put(JSLabels.VALUE, value.getStrVal());
				param.put(JSLabels.NAME, value.getVarId());
				
				positiveObj.put(param);
			}
			arrayObj.put(positiveObj);
		}
		return arrayObj;
	}

	public static InputData createInputType(RequestType training) {
		InputData inputData = new InputData();
		inputData.requestType = RequestType.$TRAINING;
		return inputData;
	}

	


}
