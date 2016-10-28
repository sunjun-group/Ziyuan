package libsvm.core;

import libsvm.svm_model;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import libsvm.extension.SVMTimeOutException;
import libsvm.extension.svm;

public class SvmRunner implements Runnable {
	private final svm_problem problem;
	private final svm_parameter parameter;
	private svm_model result;
	
	public SvmRunner(final svm_problem problem, final svm_parameter parameter) {
		this.problem = problem;
		this.parameter = parameter;
	}

	@Override
	public void run() {
		try {
			result = svm.svm_train(problem, parameter);
		} catch (SVMTimeOutException e) {
			e.printStackTrace();
			result = null;
		}
	}
	
	public svm_model getResult() {
		return result;
	}
}
