package learntest.plugin.export.io.excel;

import static learntest.plugin.export.io.excel.TrialHeader.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import learntest.plugin.export.io.excel.common.ExcelWriter;

public class TrialExcelWriter extends ExcelWriter {
	private Sheet dataSheet;
	private int lastDataSheetRow;
	
	public TrialExcelWriter(File file) throws Exception {
		super(file);
	}

	@Override
	protected void initFromNewFile(File file) {
		super.initFromNewFile(file);
		lastDataSheetRow = TrialExcelConstants.DATA_SHEET_HEADER_ROW_IDX - 1;
		dataSheet = createSheet(TrialExcelConstants.DATA_SHEET_NAME);
		initDataSheetHeader();
	}
	
	@Override
	protected void initFromExistingFile(File file) throws Exception {
		super.initFromExistingFile(file);
		dataSheet = workbook.getSheet(TrialExcelConstants.DATA_SHEET_NAME);
		lastDataSheetRow = dataSheet.getLastRowNum();
	}
	
	private void initDataSheetHeader() {
		Row headerRow = newDataSheetRow();
		for (TrialHeader header : TrialHeader.values()) {
			addCell(headerRow, header, header.getTitle());
		}
	}

	public int addRowData(Trial trial) throws IOException {
		Row row = newDataSheetRow();
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
		return lastDataSheetRow;
	}

	private void export5Trials(Row row, List<Trial> trials) {
		for (int i = 0; i < trials.size() && i < 5; i++) {
			Trial trial = trials.get(i);
			switch (i) {
			case 0:
				addCell(row, FIRST_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, FIRST_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, FIRST_TRIAL_L, trial.getL2tRtInfo().getLearnState());	
				addCell(row, FIRST_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());
				break;
			case 1:
				addCell(row, SECOND_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, SECOND_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, SECOND_TRIAL_L, trial.getL2tRtInfo().getLearnState());		
				addCell(row, SECOND_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());				
				break;
			case 2:
				addCell(row, THIRD_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, THIRD_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, THIRD_TRIAL_L, trial.getL2tRtInfo().getLearnState());	
				addCell(row, THIRD_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());					
				break;
			case 3:
				addCell(row, FORTH_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, FORTH_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, FORTH_TRIAL_L, trial.getL2tRtInfo().getLearnState());	
				addCell(row, FORTH_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());					
				break;
			case 4:
				addCell(row, FIFTH_TRIAL_R, trial.getRanRtInfo().getCoverage());
				addCell(row, FIFTH_TRIAL_L2T, trial.getL2tRtInfo().getCoverage());
				addCell(row, FIFTH_TRIAL_L, trial.getL2tRtInfo().getLearnState());	
				addCell(row, FIFTH_TRIAL_ADV, trial.getL2tRtInfo().getCoverage()-trial.getRanRtInfo().getCoverage());					
				break;

			default:
				break;
			}
		}
		
	}

	private Row newDataSheetRow() {
		return dataSheet.createRow(++lastDataSheetRow);
	}
	
}
