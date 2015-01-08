package icsetlv;

import icsetlv.svm.LibSVM;

import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import org.junit.Test;

public class SVMTest {
	
	
	@Test
	public void svmTest() throws IOException{
		
		Dataset ds = new DefaultDataset();
		for(int i = 0; i < 100; i++){
			double[] d = new double[]{Math.random(),Math.random(),Math.random()};
			Instance ti = new DenseInstance(d);
			ti.setClassValue("Positive");
			ds.add(ti);
		}
		for(int i = 0; i < 100; i++){
			double[] d = new double[]{Math.random(),Math.random(),Math.random()};
			Instance ti = new DenseInstance(d);
			ti.setClassValue("Negative");
			ds.add(ti);
		}
		
		LibSVM runsvm = new LibSVM();
		runsvm.buildClassifier(ds);
		double[] dd = runsvm.getWeights();
		for(double d1 : dd){
			System.out.println(d1);
		}
		System.out.println(runsvm.getExplicitDivider().toString());
		System.out.println(runsvm.modelAccuracy());
	}
	
	
}
