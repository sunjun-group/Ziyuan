package learntest.activelearning.core.testgeneration.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.testgeneration.InputDataWriter;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import sav.common.core.SavException;
import sav.strategies.vm.interprocess.python.PythonVmConfiguration;
import sav.strategies.vm.interprocess.python.PythonVmRunner;

public class PythonCommunicator {
	private InputDataWriter inputWriter;
	private OutputDataReader outputReader;
	private PythonVmRunner vmRunner;
	private long timeout = -1;
	
	private MethodInfo targetMethod;

	public PythonCommunicator(MethodInfo targetMethod) {
		// init vm configuration
		inputWriter = new InputDataWriter();
		outputReader = new OutputDataReader();
		this.targetMethod = targetMethod;
	}
	
	public void start() throws SavException {
		inputWriter.open();
		outputReader.open();
		vmRunner = new PythonVmRunner(inputWriter, outputReader, true);
		vmRunner.setTimeout(timeout);
		PythonVmConfiguration vmConfig = new PythonVmConfiguration();
		vmConfig.setPythonHome("C:\\Program Files\\Python36\\python.exe");
		vmConfig.setLaunchClass("E:\\linyun\\git_space\\nn_active_learning\\nn_learntest.py");
//		vmConfig.setLaunchClass("E:\\linyun\\git_space\\nn_active_learning\\test.py");
		vmRunner.setWorkingDir("E:\\linyun\\git_space\\nn_active_learning");
		vmRunner.start(vmConfig);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Message requestTraining(Branch branch, List<TestInputData> positiveData, 
			List<TestInputData> negativeData, int pointNumberLimit){
		InputData data = InputData.createTrainingRequest(targetMethod, branch, positiveData, negativeData, pointNumberLimit);
		inputWriter.send(data, vmRunner);
		
		Message output = outputReader.readOutput(-1, vmRunner);
		return output;
	}
	
	public Message sendLabel(DataPoints points) {
		InputData data = InputData.transferToLabelJSON(targetMethod, points);
		inputWriter.send(data, vmRunner);
		
		Message output = outputReader.readOutput(-1, vmRunner);
		return output;
	}
	
	public Message sendMaskResult(DataPoints points) {
		InputData data = InputData.transferToMaskJSON(targetMethod, points);
		inputWriter.send(data, vmRunner);
		
		Message output = outputReader.readOutput(-1, vmRunner);
		return output;
	}
	
	public Message requestBoundaryExploration(String methodID, Branch branch, List<TestInputData> testData) {
		InputData data = InputData.createBoundaryExplorationRequest(methodID, branch, testData);
		inputWriter.send(data, vmRunner);
		
		Message output = outputReader.readOutput(-1, vmRunner);
		return output;
	}
	
	public Message requestModelCheck(Branch parentBranch, String methodID) {
		InputData data = InputData.createModelCheckRequest(parentBranch, methodID);
		inputWriter.send(data, vmRunner);
		
		Message output = outputReader.readOutput(-1, vmRunner);
		return output;
	}
	
	public void stop() {
		vmRunner.stop();
	}
	
	public void setVmTimeout(long timeout) {
		this.timeout = timeout;
	}

	private boolean printErrorStream(InputStream error) {
		BufferedReader reader = new BufferedReader (new InputStreamReader(error));
		String line = null;
		int avail = 0;
		try {
			avail = error.available();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(avail==0){
			return false;
		}
		
    	try {
			while ((line = reader.readLine ()) != null) {
				System.err.println ("Std error: " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return true;
	}

	
}
