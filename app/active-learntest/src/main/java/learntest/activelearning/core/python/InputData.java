package learntest.activelearning.core.python;

import java.io.PrintWriter;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.python.BranchDataSet.Category;
import sav.strategies.vm.interprocess.InputDataWriter.IInputData;

/**
 * @author LLT
 *
 */
public class InputData implements IInputData {
	private Logger log = LoggerFactory.getLogger(InputData.class);
	private RequestType requestType;
	private BranchDataSet dataset;

	@Override
	public void writeData(PrintWriter pw) {
		if (dataset == null) {
			return;
		}
		pw.println(String.valueOf(requestType));
		JSONObject jsObj = new JSONObject();
		jsObj.append(JsLabels.DATASET, dataset.getDataset());
		pw.print(jsObj);
		log.debug("write data: {}, {}", requestType, jsObj);
	}

	public static IInputData forBoundaryRemaining(List<double[]> coveredInput, List<double[]> uncoveredInput) {
		InputData inputData = new InputData();
		inputData.requestType = RequestType.BOUNDARY_REMAINING;
		inputData.dataset = new BranchDataSet();
		inputData.dataset.setDatapoints(Category.TRUE, coveredInput);
		inputData.dataset.setDatapoints(Category.FALSE, uncoveredInput);
		return inputData;
	}

}
