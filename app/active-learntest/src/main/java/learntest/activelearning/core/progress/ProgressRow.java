package learntest.activelearning.core.progress;

public class ProgressRow {
	
	private double[] progress;
	private String progressMethodName;
	
	public ProgressRow()throws Exception{
		
	}
	

	public final void setProgress(double[] progressIn) {
		progress = progressIn;
	}
	
	public final void setMethodName(String methodName) {
		progressMethodName = methodName;
	}
	
	public final String getMethodName() {
		return progressMethodName;
	}
	
	public final double[] getProgress() {
		return progress;
	}
	


}
