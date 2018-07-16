package learntest.activelearning.core.python;

import java.util.List;

import cfg.CfgNode;
import sav.common.core.SavException;
import sav.strategies.vm.interprocess.InputDataWriter;
import sav.strategies.vm.interprocess.python.PythonVmConfiguration;
import sav.strategies.vm.interprocess.python.PythonVmRunner;

/**
 * @author LLT
 *
 */
public class NeuralNetworkLearner {
	private InputDataWriter inputWriter;
	private OutputDataReader outputReader;
	private PythonVmRunner vmRunner;
	private long timeout = -1;
	
	public NeuralNetworkLearner() {
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
		vmConfig.setPythonHome("/Users/lylytran/tensorflow/bin/python");
		vmConfig.setLaunchClass("/Users/lylytran/Projects/nn_active_learning/nn_learntest.py");
		vmRunner.start(vmConfig);
	}
	
	public void stop() {
		vmRunner.stop();
	}
	
	public void setVmTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public List<double[]> boundaryRemaining(List<double[]> coveredInput, List<double[]> uncoveredInput,
			CfgNode branch) {
		inputWriter.request(InputData.forBoundaryRemaining(coveredInput, uncoveredInput));
		OutputData output = outputReader.readOutput();
		return output.getGeneratedDataSet().getAllDatapoints();
	}
}
