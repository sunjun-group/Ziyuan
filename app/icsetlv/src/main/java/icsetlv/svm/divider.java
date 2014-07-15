package icsetlv.svm;

public class divider {
	double[] thetas;
	double theta0;
	
	public divider(double[] tts, double tt0){
		this.thetas = tts;
		this.theta0 = tt0;
	}
	
	public String toString(){
		int thlength = thetas.length;
		StringBuilder dsb = new StringBuilder();
		for(int i = 0; i < thlength; i++){
			if(i == thlength - 1){
				dsb.append(thetas[i] + " * x" + i + " " + theta0 + " = 0;");
			}
			else{
				dsb.append(thetas[i] + " * x" + i + " +");
			}
		}
		return dsb.toString();
	}
}
