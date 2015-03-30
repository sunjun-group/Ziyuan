package icsetlv;

import java.io.IOException;

import libsvm.core.Category;
import libsvm.core.KernelType;
import libsvm.core.Machine;
import libsvm.core.MachineType;
import libsvm.core.Parameter;

import org.junit.Test;

import sav.commons.AbstractTest;

public class SVMTest extends AbstractTest {
	
	@Test
	public void svmTest() throws IOException{
		final Machine machine = setupMachine(new Machine(), 3);
		for(int i = 0; i < 100; i++){
			machine.addDataPoint(Category.POSITIVE, Math.random(),Math.random(),Math.random());
		}
		for(int i = 0; i < 100; i++){
			machine.addDataPoint(Category.NEGATIVE, Math.random(),Math.random(),Math.random());
		}
		
		machine.train();
		System.out.println(machine.getLearnedLogic());
		System.out.println(machine.getModelAccuracy());
	}
	
	protected Machine setupMachine(Machine defaultMachine, int numberOfFeatures) {
		return defaultMachine.setNumberOfFeatures(numberOfFeatures).setParameter(
				new Parameter().setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR)
						.setEps(1.0).setUseShrinking(false).setPredictProbability(false).setC(Double.MAX_VALUE));
	}

}
