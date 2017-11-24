package dataPoint2Sample;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointValue;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.machinelearning.SampleExecutor;
import learntest.core.machinelearning.SelectiveSampling;
import sav.common.core.SavException;
import sav.strategies.dto.execute.value.ExecVar;

/** 
* @author ZhangHr 
*/
public class DataRunner {
	private static Logger log = LoggerFactory.getLogger(DataRunner.class);
	SelectiveSampling<SamplingResult> selectiveSampling;
	DecisionProbes inputProbes;
	
	public DataRunner(SimpleLearntest learntest) {
		LearningMediator mediator = learntest.mediator;
		inputProbes = learntest.initProbes;
		SampleExecutor sampleExecutor = new SampleExecutor(mediator, inputProbes);
		this.selectiveSampling = new SelectiveSampling<SamplingResult>(sampleExecutor, inputProbes);
	}
	
	public void runData(List<double[]> data, List<ExecVar> vars) {
		try {
			SamplingResult result = selectiveSampling.runData(data, vars);

			for (DecisionNodeProbe nodeProbe : inputProbes.getNodeProbes()) {
				INodeCoveredData newData = result.getNewData(nodeProbe);
				log.debug(nodeProbe.getNode().toString());
				Collection<BreakpointValue> trueV = newData.getTrueValues(), falseV = newData.getFalseValues();
				log.debug("	true data after selective sampling " + trueV.size());
				log.debug("	false data after selective sampling " + falseV.size());
			}
		} catch (SavException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
}
