package learntest.activelearning.core.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import learntest.activelearning.core.model.TestInputData;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import sav.common.core.SavException;
import sav.strategies.vm.interprocess.python.PythonVmConfiguration;
import sav.strategies.vm.interprocess.python.PythonVmRunner;

public class PythonCommunicator {
	private InputDataWriter inputWriter;
	private OutputDataReader outputReader;
	private PythonVmRunner vmRunner;
	private long timeout = -1;

	public PythonCommunicator() {
		// init vm configuration
		inputWriter = new InputDataWriter();
		outputReader = new OutputDataReader();
	}
	
	public void start() throws SavException {
		inputWriter.open();
		outputReader.open();
		vmRunner = new PythonVmRunner(inputWriter, outputReader, true);
		vmRunner.setTimeout(timeout);
		PythonVmConfiguration vmConfig = new PythonVmConfiguration();
		vmConfig.setPythonHome("C:\\Program Files\\Python36\\python.exe");
		vmConfig.setLaunchClass("E:\\linyun\\git_space\\nn_active_learning\\nn_learntest.py");
		vmRunner.start(vmConfig);
	}
	
	public void requestInput(Branch branch){
		InputData.createBranchRequest(branch);
//		inputWriter.request(InputData.forBoundaryRemaining(pathCoverage));
//		OutputData output = outputReader.readOutput();
//		return output.getDataSet().getCoveredData();
	}
	
	public void requestTraining(Branch branch, List<TestInputData> positiveData, 
			List<TestInputData> negativeData){
		InputData typeData = InputData.createInputType(RequestType.$TRAINING);
		inputWriter.send(typeData);
		
		InputData data = InputData.createTrainingRequest(branch, positiveData, negativeData);
		inputWriter.send(data);
		
//		OutputData d = outputReader.readOutput();
		System.currentTimeMillis();
	}
	
	public void startTrainingMethod(String methodName) {
		inputWriter.send(InputData.createStartMethodRequest(methodName));
	}
	
	public void stop() {
		vmRunner.stop();
	}
	
	public void setVmTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public List<double[]> boundaryRemaining(Dataset pathCoverage) {
		inputWriter.send(InputData.forBoundaryRemaining(pathCoverage));
		OutputData output = outputReader.readOutput();
		return output.getDataSet().getCoveredData();
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
