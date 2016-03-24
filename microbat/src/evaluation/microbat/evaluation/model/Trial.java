package microbat.evaluation.model;

import java.util.List;

public class Trial {
	private String testCaseName;
	private boolean isBugFound;
	private int totalSteps;
	private String mutatedFile;
	private int mutatedLineNumber;
	private List<String> jumpSteps;

	public Trial(){
		
	}
	
	public Trial(String testCaseName, int mutatedLineNumber, String mutatedFile,
			boolean isBugFound, List<String> jumpSteps, int totalSteps) {
		super();
		this.testCaseName = testCaseName;
		this.mutatedFile = mutatedFile;
		this.mutatedLineNumber = mutatedLineNumber;
		this.isBugFound = isBugFound;
		this.jumpSteps = jumpSteps;
		this.totalSteps = totalSteps;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public int getMutatedLineNumber() {
		return mutatedLineNumber;
	}

	public void setMutatedLineNumber(int mutatedLineNumber) {
		this.mutatedLineNumber = mutatedLineNumber;
	}

	public boolean isBugFound() {
		return isBugFound;
	}

	public void setBugFound(boolean isBugFound) {
		this.isBugFound = isBugFound;
	}

	public List<String> getJumpSteps() {
		return jumpSteps;
	}

	public void setJumpSteps(List<String> jumpSteps) {
		this.jumpSteps = jumpSteps;
	}

	public int getTotalSteps() {
		return totalSteps;
	}

	public void setTotalSteps(int totalSteps) {
		this.totalSteps = totalSteps;
	}

	@Override
	public String toString() {
		return "Trial [testCaseName=" + testCaseName + ", mutatedLineNumber="
				+ mutatedLineNumber + ", isBugFound=" + isBugFound
				+ ", jumpSteps=" + jumpSteps + ", totalSteps=" + totalSteps
				+ "]";
	}

	public String getMutatedFile() {
		return mutatedFile;
	}

	public void setMutatedFile(String mutatedFile) {
		this.mutatedFile = mutatedFile;
	}

	
}
