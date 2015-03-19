package icsetlv;

import java.io.IOException;

import libsvm.core.Category;
import libsvm.core.Machine;

import org.junit.Test;

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
	
}
