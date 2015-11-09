package icsetlv.trial.model;

public class TraceNode {
	private String className;
	private int lineNum;
	
	private ProgramState state;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public ProgramState getState() {
		return state;
	}

	public void setState(ProgramState state) {
		this.state = state;
	}
	
	
}
