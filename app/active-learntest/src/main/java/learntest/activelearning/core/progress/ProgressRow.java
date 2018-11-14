package learntest.activelearning.core.progress;

public class ProgressRow {
	
	private double[] progress;
	private String progressMethodName;
	private String errorMessage = "";
	
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


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	


}
