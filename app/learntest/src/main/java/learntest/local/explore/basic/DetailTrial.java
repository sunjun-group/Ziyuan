package learntest.local.explore.basic;
/** 
* @author ZhangHr 
* 
* information about one trial
*/
public class DetailTrial {
	private String methodName;
	private int line;
	private double randoop, l2t, advantage, jdart;
	private String l2tBetter, ranBetter;
	private int learnedState;
	private int index; //ith trial
	private int jdartSolveTimes , l2tSolveTimes;
	
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
	public double getRandoop() {
		return randoop;
	}
	public void setRandoop(double randoop) {
		this.randoop = randoop;
	}
	public double getL2t() {
		return l2t;
	}
	public void setL2t(double l2t) {
		this.l2t = l2t;
	}
	public double getAdvantage() {
		return advantage;
	}
	public void setAdvantage(double advantage) {
		this.advantage = advantage;
	}
	public String getL2tBetter() {
		return l2tBetter;
	}
	public void setL2tBetter(String l2tBetter) {
		this.l2tBetter = l2tBetter;
	}
	public String getRanBetter() {
		return ranBetter;
	}
	public void setRanBetter(String ranBetter) {
		this.ranBetter = ranBetter;
	}
	public int getLearnedState() {
		return learnedState;
	}
	public void setLearnedState(int learnedState) {
		this.learnedState = learnedState;
	}
	public double getJdart() {
		return jdart;
	}
	public void setJdart(double jdart) {
		this.jdart = jdart;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getJdartSolveTimes() {
		return jdartSolveTimes;
	}
	public void setJdartSolveTimes(int jdartSolveTimes) {
		this.jdartSolveTimes = jdartSolveTimes;
	}
	public int getL2tSolveTimes() {
		return l2tSolveTimes;
	}
	public void setL2tSolveTimes(int l2tSolveTimes) {
		this.l2tSolveTimes = l2tSolveTimes;
	}
	
}
