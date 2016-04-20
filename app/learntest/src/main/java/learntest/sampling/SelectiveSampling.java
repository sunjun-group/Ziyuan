package learntest.sampling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.sampling.IlpSolver;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import libsvm.core.Machine.DataPoint;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;

public class SelectiveSampling{
	private static Logger log = LoggerFactory.getLogger(SelectiveSampling.class);
	private TestcasesExecutorwithLoopTimes tcExecutor;
	private BreakPoint bkp;
	
	public SelectiveSampling(TestcasesExecutorwithLoopTimes tcExecutor) {
		this.tcExecutor = tcExecutor;
	}
	
	public void setUp(BreakPoint bkp) {
		//TODO entry BKP
		this.bkp = bkp;		
	}

	public BreakpointData selectData(Formula formula, List<String> labels, List<DataPoint> datapoints) throws SavException {
		BreakpointData bkpData = null;		
		if (formula == null) {
			return bkpData;
		}
		
		Map<String, Pair<Double, Double>> minMax = calculateValRange(labels, datapoints);
		
		IlpSolver solver = new IlpSolver(minMax, true);
		formula.accept(solver);
		List<List<Eq<?>>> assignments = solver.getResult();
		log.debug("Instrument values: ");
		for (List<Eq<?>> valSet : assignments) {
			tcExecutor.setDebugMode(toInstrVarMap(valSet));
			tcExecutor.run(CollectionUtils.listOf(bkp));
			List<BreakpointData> result = tcExecutor.getResult();
			if (result.isEmpty()) {
				continue;
			}
			BreakpointData breakpointData = result.get(0);
			if(bkpData == null) {
				bkpData = breakpointData;
			} else if (!bkpData.merge(breakpointData)) {
				log.error("Wrong location: " + breakpointData.getLocation());
			}
		}		
		return bkpData;
	}
	
	private Map<String, Pair<Double, Double>> calculateValRange(
			List<String> dataLabels, List<DataPoint> dataPoints) {
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		for (DataPoint dp : dataPoints) {
			for (int i = 0; i < dataLabels.size(); i++) {
				double val = dp.getValue(i);
				String label = dataLabels.get(i);
				Pair<Double, Double> mm = minMax.get(label);
				if (mm == null) {
					mm = new Pair<Double, Double>(val, val);
					minMax.put(label, mm);
				}
				/* min */
				if (mm.a.doubleValue() > val) {
					mm.a = val;
				}
				/* max */
				if (mm.b.doubleValue() < val) {
					mm.b = val;
				}
			}
		}
		return minMax;
	}
	
	private Map<String, Object> toInstrVarMap(List<Eq<?>> assignments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Eq<?> asgt : assignments) {
			map.put(asgt.getVar().getLabel(), asgt.getValue());
		}
		return map;
	}

}
