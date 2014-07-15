package icsetlv.svm;

import libsvm.LibSVM;
import net.sf.javaml.core.Dataset;

public class ClassifierBuilder {
	
	
	LibSVM libsvm;
	
	public ClassifierBuilder(){
	}
	
	public void BuildClassifier(Dataset ds){
		libsvm.buildClassifier(ds);
	}
	
}
