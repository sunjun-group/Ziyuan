package learntest.local;
import java.util.ArrayList;
import java.util.List;

import mosek.Env.parametertype;

/** 
* @author ZhangHr 
*/
public class MethodTrial {
	private List<DetailTrial> trials = new ArrayList<>(5);
	private String methodName;
	private int line;
	private double validAveCoverageAdv; // the coverage 
	private int jdartTime;
	private double jdartCov;
	private int jdartCnt;
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
	public double getValidAveCoverageAdv() {
		return validAveCoverageAdv;
	}
	public void setValidAveCoverageAdv(double averageAdv) {
		this.validAveCoverageAdv = averageAdv;
	}
	public int getJdartTime() {
		return jdartTime;
	}
	public void setJdartTime(int jdartTime) {
		this.jdartTime = jdartTime;
	}
	public double getJdartCov() {
		return jdartCov;
	}
	public void setJdartCov(double jdartCov) {
		this.jdartCov = jdartCov;
	}
	public int getJdartCnt() {
		return jdartCnt;
	}
	public void setJdartCnt(int jdartCnt) {
		this.jdartCnt = jdartCnt;
	}
	
	
}
