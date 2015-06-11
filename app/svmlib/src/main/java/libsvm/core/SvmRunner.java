package libsvm.core;

import java.util.concurrent.Callable;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SvmRunner implements Callable<svm_model> {

	private final svm_problem problem;
	private final svm_parameter parameter;

	public SvmRunner(final svm_problem problem, final svm_parameter parameter) {
		this.problem = problem;
		this.parameter = parameter;
	}

	@Override
	public svm_model call() throws Exception {
		return svm.svm_train(problem, parameter);
	}
}
