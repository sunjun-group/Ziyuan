package libsvm.core;

import libsvm.svm_model;

/**
 * Wrapper for libsvm.svm_model
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class Model {

	private svm_model model;

	public Model(final svm_model model) {
		this.model = model;
	}

}
