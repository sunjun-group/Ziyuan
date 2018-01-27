package learntest.core.time;

import java.util.TimerTask;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import learntest.core.commons.utils.CoverageUtils;
import sav.common.core.Pair;
import sav.settings.SAVTimer;

public class CovTimeTask extends TimerTask{
	CfgCoverage cfgCoverage;
	CovTimer timer;
	
	public CovTimeTask(CfgCoverage cfgCoverage, CovTimer timer) {
		this.cfgCoverage = cfgCoverage;
		this.timer = timer;
	}

	@Override
	public void run() {
		timer.recordCovTimeLine(cfgCoverage);
		timer.checkTime();
	}

}
