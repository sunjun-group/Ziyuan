package learntest.local.timer;

import static learntest.local.timer.TimerTrialHeader.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import com.sun.tools.classfile.Annotation.element_value;

import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;
import mosek.Env.boundkey;
import sav.common.core.Pair;

public class TimerTrialExcelWriter extends SimpleExcelWriter<Trial> {
	boolean L2T_FLAG = true;
	boolean RAND_FLAG = false;
	
	public TimerTrialExcelWriter(File file) throws Exception {
		super(file, TimerTrialHeader.values());
	}
	
	protected void addRowData(Row row, Trial trial) throws IOException {
		addCell(row, METHOD_NAME, trial.getMethodName());
		
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
		addCell(row, RAND_WORSE_THAN_L2T, trial.getRandWorseThanl2t());
		addCell(row,L2T_WORSE_THAN_RAND, trial.getL2tWorseThanRand());
		addCell(row, TRIAL_L, trial.getL2tRtInfo().getLearnState());
		addCell(row, RAND_WORSE_THAN_L2T_B, trial.getRandWorseBranches());
		addCell(row, L2T_WORSE_THAN_RAND_B, trial.getL2tWorseBranches());
		addCell(row, VAR_TYPE, trial.getVarType() == MethodInfo.ALL_PT_VAR ? 1 :0);
		
		List<Pair<Integer, Double>> l2tTl = trial.getL2tRtInfo().getCovTimeLine();
		addTimeLineCell(row, l2tTl, L2T_FLAG);
		
		List<Pair<Integer, Double>> randTl = trial.getRanRtInfo().getCovTimeLine();
		addTimeLineCell(row, randTl, RAND_FLAG);
		
		writeWorkbook();
	}

	private void addTimeLineCell(Row row, List<Pair<Integer, Double>> list, boolean flag) {
		int steps = 10;
		if (list.size() < steps && list.get(list.size()-1).b == 1) { // fill in short time line
			Pair<Integer, Double> pair = list.get(list.size()-1);
			 while (list.size() < steps) {
				list.add(pair);				
			}
		}
		for (int i = 1; i < steps && i < list.size(); i++) {
			Pair<Integer, Double> pair = list.get(i);
			TimerTrialHeader header = null;
			switch (i) {
			case 1:
				if (flag == L2T_FLAG) {
					header = L2T_S1;
				}else {
					header = RANDOOP_S1;
				}
				break;
			case 2:
				if (flag == L2T_FLAG) {
					header = L2T_S2;
				}else {
					header = RANDOOP_S2;	
				}				
				break;
			case 3:
				if (flag == L2T_FLAG) {
					header = L2T_S3;
				}else {
					header = RANDOOP_S3;	
				}				
				break;
			case 4:
				if (flag == L2T_FLAG) {
					header = L2T_S4;
				}else {
					header = RANDOOP_S4;	
				}				
				break;
			case 5:
				if (flag == L2T_FLAG) {
					header = L2T_S5;
				}else {
					header = RANDOOP_S5;	
				}				
				break;
			case 6:
				if (flag == L2T_FLAG) {
					header = L2T_S6;
				}else {
					header = RANDOOP_S6;	
				}				
				break;
			case 7:
				if (flag == L2T_FLAG) {
					header = L2T_S7;
				}else {
					header = RANDOOP_S7;	
				}				
				break;
			case 8:
				if (flag == L2T_FLAG) {
					header = L2T_S8;
				}else {
					header = RANDOOP_S8;	
				}				
				break;
			case 9:
				if (flag == L2T_FLAG) {
					header = L2T_S9;
				}else {
					header = RANDOOP_S9;	
				}
				break;
			default:
				break;
			}
			addCell(row, header, pair.b);
		}
		StringBuffer sBuffer = new StringBuffer();
		for (Pair<Integer, Double> pair : list) {
			sBuffer.append(pair.toString()+ "//");
		}
		if (flag == L2T_FLAG) {
			addCell(row, L2T_TIMELINE, sBuffer.toString());
		}else {
			addCell(row, RANDOOP_TIMELINE, sBuffer.toString());
		}
	}

}
