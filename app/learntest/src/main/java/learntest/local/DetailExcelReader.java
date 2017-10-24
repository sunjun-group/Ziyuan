package learntest.local;
import static learntest.plugin.export.io.excel.TrialHeader.FIFTH_L2T_WORSE_THAN_RAND;
import static learntest.plugin.export.io.excel.TrialHeader.FIFTH_RAND_WORSE_THAN_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.FIFTH_TRIAL_ADV;
import static learntest.plugin.export.io.excel.TrialHeader.FIFTH_TRIAL_L;
import static learntest.plugin.export.io.excel.TrialHeader.FIFTH_TRIAL_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.FIFTH_TRIAL_R;
import static learntest.plugin.export.io.excel.TrialHeader.FIRST_L2T_WORSE_THAN_RAND;
import static learntest.plugin.export.io.excel.TrialHeader.FIRST_RAND_WORSE_THAN_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.FIRST_TRIAL_ADV;
import static learntest.plugin.export.io.excel.TrialHeader.FIRST_TRIAL_L;
import static learntest.plugin.export.io.excel.TrialHeader.FIRST_TRIAL_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.FIRST_TRIAL_R;
import static learntest.plugin.export.io.excel.TrialHeader.FORTH_L2T_WORSE_THAN_RAND;
import static learntest.plugin.export.io.excel.TrialHeader.FORTH_RAND_WORSE_THAN_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.FORTH_TRIAL_ADV;
import static learntest.plugin.export.io.excel.TrialHeader.FORTH_TRIAL_L;
import static learntest.plugin.export.io.excel.TrialHeader.FORTH_TRIAL_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.FORTH_TRIAL_R;
import static learntest.plugin.export.io.excel.TrialHeader.SECOND_L2T_WORSE_THAN_RAND;
import static learntest.plugin.export.io.excel.TrialHeader.SECOND_RAND_WORSE_THAN_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.SECOND_TRIAL_ADV;
import static learntest.plugin.export.io.excel.TrialHeader.SECOND_TRIAL_L;
import static learntest.plugin.export.io.excel.TrialHeader.SECOND_TRIAL_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.SECOND_TRIAL_R;
import static learntest.plugin.export.io.excel.TrialHeader.THIRD_L2T_WORSE_THAN_RAND;
import static learntest.plugin.export.io.excel.TrialHeader.THIRD_RAND_WORSE_THAN_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.THIRD_TRIAL_ADV;
import static learntest.plugin.export.io.excel.TrialHeader.THIRD_TRIAL_L;
import static learntest.plugin.export.io.excel.TrialHeader.THIRD_TRIAL_L2T;
import static learntest.plugin.export.io.excel.TrialHeader.THIRD_TRIAL_R;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import learntest.core.commons.exception.LearnTestException;
import learntest.plugin.export.io.excel.TrialExcelConstants;
import learntest.plugin.export.io.excel.TrialHeader;
import learntest.plugin.export.io.excel.common.ExcelReader;
import learntest.plugin.export.io.excel.common.ExcelSettings;
import sav.common.core.utils.Assert;

/**
 * @author ZhangHr
 */
public class DetailExcelReader extends ExcelReader {

	private Sheet dataSheet;

	public DetailExcelReader(File file) throws Exception {
		super(file);
	}

	@Override
	public void reset(File file) throws Exception {
		super.reset(file);
		dataSheet = workbook.getSheet(TrialExcelConstants.DATA_SHEET_NAME);
		if (dataSheet == null) {
			throw new LearnTestException("invalid experimental file! (Cannot get data sheet)");
		}
	}

	public List<MethodTrial> readDataSheet() {
		Assert.assertNotNull(dataSheet, "TrialExcelReader has not initialized!");
		Iterator<Row> it = dataSheet.rowIterator();
		Row header = it.next(); // ignore first row (header)
		Assert.assertTrue(isDataSheetHeader(header), "Data sheet is invalid!");
		List<MethodTrial> data = new LinkedList<>();
		try {
			while (it.hasNext()) {
				Row row = it.next();
				readDataSheetRow(row, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public boolean hasValidHeader() {
		Row header = dataSheet.iterator().next();
		return isDataSheetHeader(header);
	}

	private void readDataSheetRow(Row row, List<MethodTrial> data) {
		MethodTrial trial = new MethodTrial();
		trial.setMethodName(getStringCellValue(row, TrialHeader.METHOD_NAME));
		trial.setLine(getIntCellValue(row, TrialHeader.METHOD_START_LINE));
		trial.setValidAveCoverageAdv(getDoubleCellValue(row, TrialHeader.VALID_COVERAGE_ADV));
		trial.setJdartCnt(getIntCellValue(row, TrialHeader.JDART_TEST_CNT));
		trial.setJdartTime(getIntCellValue(row, TrialHeader.JDART_TIME));
		trial.setJdartCov(getDoubleCellValue(row, TrialHeader.JDART_COVERAGE));
		
		for (int i = 0; i < 5; i++) {
			DetailTrial dTrial = new DetailTrial();
			dTrial.setLine(trial.getLine());
			dTrial.setMethodName(trial.getMethodName());
			switch (i) {
			case 0:
				if (row.getCell(FIRST_TRIAL_ADV.getCellIdx()) != null) {
					dTrial.setLearnedState(getIntCellValue(row, FIRST_TRIAL_L));
					dTrial.setAdvantage(getDoubleCellValue(row, FIRST_TRIAL_ADV));
					dTrial.setL2t(getDoubleCellValue(row, FIRST_TRIAL_L2T));
					dTrial.setRandoop(getDoubleCellValue(row, FIRST_TRIAL_R));
					dTrial.setL2tBetter(getStringCellValue(row, FIRST_RAND_WORSE_THAN_L2T));
					dTrial.setRanBetter(getStringCellValue(row, FIRST_L2T_WORSE_THAN_RAND));
					trial.getTrials().add(dTrial);
				}
				break;
			case 1:
				if (row.getCell(SECOND_TRIAL_ADV.getCellIdx()) != null) {
					dTrial.setLearnedState(getIntCellValue(row, SECOND_TRIAL_L));
					dTrial.setAdvantage(getDoubleCellValue(row, SECOND_TRIAL_ADV));
					dTrial.setL2t(getDoubleCellValue(row, SECOND_TRIAL_L2T));
					dTrial.setRandoop(getDoubleCellValue(row, SECOND_TRIAL_R));
					dTrial.setL2tBetter(getStringCellValue(row, SECOND_RAND_WORSE_THAN_L2T));
					dTrial.setRanBetter(getStringCellValue(row, SECOND_L2T_WORSE_THAN_RAND));
					trial.getTrials().add(dTrial);
				}
				break;
			case 2:
				if (row.getCell(THIRD_TRIAL_ADV.getCellIdx()) != null) {
					dTrial.setLearnedState(getIntCellValue(row, THIRD_TRIAL_L));
					dTrial.setAdvantage(getDoubleCellValue(row, THIRD_TRIAL_ADV));
					dTrial.setL2t(getDoubleCellValue(row, THIRD_TRIAL_L2T));
					dTrial.setRandoop(getDoubleCellValue(row, THIRD_TRIAL_R));
					dTrial.setL2tBetter(getStringCellValue(row, THIRD_RAND_WORSE_THAN_L2T));
					dTrial.setRanBetter(getStringCellValue(row, THIRD_L2T_WORSE_THAN_RAND));
					trial.getTrials().add(dTrial);
				}
				break;
			case 3:
				if (row.getCell(FORTH_TRIAL_ADV.getCellIdx()) != null) {
					dTrial.setLearnedState(getIntCellValue(row, FORTH_TRIAL_L));
					dTrial.setAdvantage(getDoubleCellValue(row, FORTH_TRIAL_ADV));
					dTrial.setL2t(getDoubleCellValue(row, FORTH_TRIAL_L2T));
					dTrial.setRandoop(getDoubleCellValue(row, FORTH_TRIAL_R));
					dTrial.setL2tBetter(getStringCellValue(row, FORTH_RAND_WORSE_THAN_L2T));
					dTrial.setRanBetter(getStringCellValue(row, FORTH_L2T_WORSE_THAN_RAND));
					trial.getTrials().add(dTrial);
				}
				break;
			case 4:
				if (row.getCell(FIFTH_TRIAL_ADV.getCellIdx()) != null) {
					dTrial.setLearnedState(getIntCellValue(row, FIFTH_TRIAL_L));
					dTrial.setAdvantage(getDoubleCellValue(row, FIFTH_TRIAL_ADV));
					dTrial.setL2t(getDoubleCellValue(row, FIFTH_TRIAL_L2T));
					dTrial.setRandoop(getDoubleCellValue(row, FIFTH_TRIAL_R));
					dTrial.setL2tBetter(getStringCellValue(row, FIFTH_RAND_WORSE_THAN_L2T));
					dTrial.setRanBetter(getStringCellValue(row, FIFTH_L2T_WORSE_THAN_RAND));
					trial.getTrials().add(dTrial);
				}
				break;

			default:
				break;
			}
		}
		data.add(trial);

	}

	private boolean isDataSheetHeader(Row header) {
		if (header.getRowNum() != ExcelSettings.DATA_SHEET_HEADER_ROW_IDX) {
			return false;
		}
		for (TrialHeader title : TrialHeader.values()) {
			if (!title.getTitle().equals(header.getCell(title.getCellIdx()).getStringCellValue())) {
				return false;
			}
		}
		return true;
	}

	public int getLastDataSheetRow() {
		return dataSheet.getLastRowNum();
	}
}
