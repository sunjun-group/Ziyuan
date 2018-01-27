package learntest.core.time;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import learntest.core.commons.utils.CoverageUtils;
import sav.common.core.Pair;

public class CovTimer {
	public volatile static boolean stopFlag = false;
	public static int timeOUt = 150 * 1000;
	public Timer timer = new Timer();
	public long startTime = 0;
	CfgCoverage cfgCoverage;
	private List<Pair<Integer, Double>> covTimeLine = new LinkedList<>();
	
	public CovTimer(CfgCoverage cfgCoverage, long ranTime) { 
		covTimeLine = new LinkedList<>();
		this.cfgCoverage = cfgCoverage;
		double coverage = CoverageUtils.calculateCoverageByBranch(cfgCoverage);
		Pair<Integer, Double> pair = new Pair<Integer, Double>((int)ranTime, coverage);
		covTimeLine.add(pair);
		startTime = System.currentTimeMillis();
	}
	
	public void start(){
		stopFlag = false;
        timer = new Timer();
        timer.schedule(new CovTimeTask(cfgCoverage, this), 30 * 1000,  30 * 1000);
	}
	
	public void close(){
		timer.cancel();
		timer = null;
	}

	public Timer getTimer() {
		return timer;
	}
	
	public void recordCovTimeLine(CfgCoverage cfgCoverage){
		double coverage = CoverageUtils.calculateCoverageByBranch(cfgCoverage);
		int time = (int)(System.currentTimeMillis() - startTime);
		Pair<Integer, Double> pair = new Pair<Integer, Double>(time, coverage);
		covTimeLine.add(pair);
	}

	public List<Pair<Integer, Double>> getCovTimeLine() {
		return covTimeLine;
	}
	
	public void checkTime(){
		if ((int)(System.currentTimeMillis() - startTime) >= timeOUt) {
			stopFlag = true;
		}
	}
	
}
