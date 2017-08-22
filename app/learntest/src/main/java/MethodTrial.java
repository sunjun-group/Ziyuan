import java.util.ArrayList;
import java.util.List;

/** 
* @author ZhangHr 
*/
public class MethodTrial {
	private List<DetailTrial> trials = new ArrayList<>(5);
	private String methodName;
	private int line;
	private double validCoverageAdv;
	public List<DetailTrial> getTrials() {
		return trials;
	}
	public void setTrials(List<DetailTrial> trials) {
		this.trials = trials;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String method) {
		this.methodName = method;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public double getValidCoverageAdv() {
		return validCoverageAdv;
	}
	public void setValidCoverageAdv(double averageAdv) {
		this.validCoverageAdv = averageAdv;
	}
	
}
