package learntest.plugin.export.io.excel;

import static learntest.plugin.export.io.excel.TrialHeader.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import learntest.core.RunTimeInfo;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;

public class TrialExcelWriter extends SimpleExcelWriter<Trial> {
	
	public TrialExcelWriter(File file) throws Exception {
		super(file, TrialHeader.values());
	}
	
	protected void addRowData(Row row, Trial trial) throws IOException {
		addCell(row, METHOD_NAME, trial.getMethodName());
		
		if (trial.getJdartRtInfo() != null) {
			addCell(row, JDART_TIME, trial.getJdartRtInfo().getTime());
			addCell(row, JDART_COVERAGE, trial.getJdartRtInfo().getCoverage());
			addCell(row, JDART_TEST_CNT, trial.getJdartRtInfo().getTestCnt());
		}
		
		if (trial.getL2tRtInfo() != null) {
			addCell(row, L2T_TIME, trial.getL2tRtInfo().getTime());
			addCell(row, L2T_COVERAGE, trial.getL2tRtInfo().getCoverage());
			addCell(row, L2T_TEST_CNT, trial.getL2tRtInfo().getTestCnt());
		}
		
		if (trial.getRanRtInfo() != null) {
			addCell(row, RANDOOP_TIME, trial.getRanRtInfo().getTime());
			addCell(row, RANDOOP_COVERAGE, trial.getRanRtInfo().getCoverage());
			addCell(row, RANDOOP_TEST_CNT, trial.getRanRtInfo().getTestCnt());
		}
		
		addCell(row, ADVANTAGE, trial.getAdvantage());
		addCell(row, METHOD_LENGTH, trial.getMethodLength());
		addCell(row, METHOD_START_LINE, trial.getMethodStartLine());
		
		addCell(row, L2T_VALID_COVERAGE, trial.getL2tRtInfo().getValidCoverage());
		addCell(row, RANDOOP_VALID_COVERAGE, trial.getRanRtInfo().getValidCoverage());
		addCell(row, VALID_COVERAGE_ADV, trial.getL2tRtInfo().getValidCoverage()-trial.getRanRtInfo().getValidCoverage());
		addCell(row, AVE_COVERAGE_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());
		
		if (trial instanceof MultiTrial) {
			MultiTrial multiTrial = (MultiTrial) trial;
			addCell(row, L2T_BEST_COVERAGE, multiTrial.getBestL2tRtCoverage());
			addCell(row, RANDOOP_BEST_COVERAGE, multiTrial.getBestRanRtCoverage());
			addCell(row, VALID_NUM, multiTrial.getValidNum());
			List<Trial> trials = ((MultiTrial) trial).getTrials();
			export5Trials(row, trials);
		}
		writeWorkbook();
	}

	private void export5Trials(Row row, List<Trial> trials) {
		for (int i = 0; i < trials.size() && i < 5; i++) {
			Trial trial = trials.get(i);
			RunTimeInfo l2tInfo = trial.getL2tRtInfo();
			RunTimeInfo jdartInfo = trial.getJdartRtInfo();
			
			switch (i) {
			case 0:
				addCell(row, FIRST_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, FIRST_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, FIRST_TRIAL_L, trial.getL2tRtInfo().getLearnState());	
				addCell(row, FIRST_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());
				addCell(row, FIRST_RAND_WORSE_THAN_L2T, trial.randWorseThanl2t);
				addCell(row, FIRST_L2T_WORSE_THAN_RAND, trial.l2tWorseThanRand);
				addCell(row, FIRST_TRIAL_JDART, trial.getJdartRtInfo().getCoverage());
				addCell(row, FIRST_TRIAL_JDART_CNT, trial.getJdartRtInfo().getTestCnt());
				addCell(row, FIRST_SYMBOLIC_TIMES, l2tInfo.getSymbolicTimes());
				addCell(row, FIRST_TRIAL_JDART_SOLVE_TIMES, jdartInfo.getSymbolicTimes());
				break;
			case 1:
				addCell(row, SECOND_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, SECOND_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, SECOND_TRIAL_L, trial.getL2tRtInfo().getLearnState());		
				addCell(row, SECOND_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());
				addCell(row, SECOND_RAND_WORSE_THAN_L2T, trial.randWorseThanl2t);	
				addCell(row, SECOND_L2T_WORSE_THAN_RAND, trial.l2tWorseThanRand);	
				addCell(row, SECOND_TRIAL_JDART, trial.getJdartRtInfo().getCoverage());
				addCell(row, SECOND_TRIAL_JDART_CNT, trial.getJdartRtInfo().getTestCnt());
				addCell(row, SECOND_SYMBOLIC_TIMES, l2tInfo.getSymbolicTimes());
				addCell(row, SECOND_TRIAL_JDART_SOLVE_TIMES, jdartInfo.getSymbolicTimes());
				break;
			case 2:
				addCell(row, THIRD_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, THIRD_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, THIRD_TRIAL_L, trial.getL2tRtInfo().getLearnState());	
				addCell(row, THIRD_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());
				addCell(row, THIRD_RAND_WORSE_THAN_L2T, trial.randWorseThanl2t);
				addCell(row, THIRD_L2T_WORSE_THAN_RAND, trial.l2tWorseThanRand);	
				addCell(row, THIRD_TRIAL_JDART, trial.getJdartRtInfo().getCoverage());
				addCell(row, THIRD_TRIAL_JDART_CNT, trial.getJdartRtInfo().getTestCnt());
				addCell(row, THIRD_SYMBOLIC_TIMES, l2tInfo.getSymbolicTimes());
				addCell(row, THIRD_TRIAL_JDART_SOLVE_TIMES, jdartInfo.getSymbolicTimes());
				break;
			case 3:
				addCell(row, FORTH_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, FORTH_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, FORTH_TRIAL_L, trial.getL2tRtInfo().getLearnState());	
				addCell(row, FORTH_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());	
				addCell(row, FORTH_RAND_WORSE_THAN_L2T, trial.randWorseThanl2t);		
				addCell(row, FORTH_L2T_WORSE_THAN_RAND, trial.l2tWorseThanRand);	
				addCell(row, FORTH_TRIAL_JDART, trial.getJdartRtInfo().getCoverage());
				addCell(row, FORTH_TRIAL_JDART_CNT, trial.getJdartRtInfo().getTestCnt());
				addCell(row, FORTH_SYMBOLIC_TIMES, l2tInfo.getSymbolicTimes());
				addCell(row, FORTH_TRIAL_JDART_SOLVE_TIMES, jdartInfo.getSymbolicTimes());
				break;
			case 4:
				addCell(row, FIFTH_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, FIFTH_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, FIFTH_TRIAL_L, trial.getL2tRtInfo().getLearnState());	
				addCell(row, FIFTH_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());	
				addCell(row, FIFTH_RAND_WORSE_THAN_L2T, trial.randWorseThanl2t);
				addCell(row, FIFTH_L2T_WORSE_THAN_RAND, trial.l2tWorseThanRand);
				addCell(row, FIFTH_TRIAL_JDART, trial.getJdartRtInfo().getCoverage());
				addCell(row, FIFTH_TRIAL_JDART_CNT, trial.getJdartRtInfo().getTestCnt());	
				addCell(row, FIFTH_SYMBOLIC_TIMES, l2tInfo.getSymbolicTimes());
				addCell(row, FIFTH_TRIAL_JDART_SOLVE_TIMES, jdartInfo.getSymbolicTimes());
				break;

			default:
				break;
			}
		}
		
	}

}
