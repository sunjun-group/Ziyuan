/*
 * Input is an array of string of formatted file path and the number of parameters/variables
 * Train with libsvm.
 * Output a model, the corresponding predicate
 * */

package icsetlv.svm;

import java.io.IOException;

import libsvm.*;

public class svm_runner {

	String[] arg = null;
	svm_model model = null;
	int paraNumber = 0; // same as the feature size
	boolean scale = false;
	boolean trained = false;
	

	public svm_runner(String[] filePath, int paraNumber, boolean scale){
		this.arg = filePath;
		this.paraNumber = paraNumber;
		this.scale = scale;
	}

	public void scaleData() throws IOException{
		String[] filetoscale ={arg[0]}; 
		svm_scale.main(filetoscale);
	}
	
	public void train() throws IOException{
		if(scale == true){
			scaleData();
		}
		this.model = svm_train.main(arg);
		this.trained = true;
	}
	
	public svm_model getModel(){
		return model;
	}
	
	public divider getExplicitDivider(){
		assert trained == true;
		int pSize = this.paraNumber;
		double bias = 0;
		double[] wVec = new double[pSize];
		
		if(model.nr_class!=2){
			System.out.println("Can not be binary divided...");
		}
		
		int[] sIndex = new int[model.nr_class];	
		for (int i = 1; i < model.nr_class; i++) {
			sIndex[i] = sIndex[i - 1] + model.nSV[i - 1];
		}
		int p = 0;
		for (int i = 0; i < model.nr_class; i++) {

			for (int j = i + 1; j < model.nr_class; j++) {
				int si = sIndex[i];
				int sj = sIndex[j];
				double[] coef1 = model.sv_coef[j - 1];
				double[] coef2 = model.sv_coef[i];

				for (int k = 0; k < model.nSV[i]; k++) {
					for (int m = 0; m < pSize; m++) {
						wVec[m] += coef1[si + k] * model.SV[si + k][m].value;
					}
				}

				for (int k = 0; k < model.nSV[j]; k++) {
					for (int m = 0; m < pSize; m++) {
						wVec[m] += coef2[sj + k] * model.SV[sj + k][m].value;
					}
				}
				
				bias = model.rho[p];
				p++;
			}
		}
		divider dv = new divider(wVec, bias);
		return dv;
	}

}
