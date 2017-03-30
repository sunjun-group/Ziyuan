package learntest.io.excel;

import static learntest.io.excel.TrialHeader.*;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import learntest.io.excel.common.ExcelWriter;

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
		addCell(row, L2T_TIME, trial.getL2tTime());
		addCell(row, L2T_COVERAGE, trial.getL2tCoverage());
		addCell(row, L2T_TEST_CNT, trial.getL2tTestCnt());
		addCell(row, RANDOOP_TIME, trial.getRandoopTime());
		addCell(row, RANDOOP_COVERAGE, trial.getRandoopCoverage());
		addCell(row, RANDOOP_TEST_CNT, trial.getRandoopTestCnt());
		addCell(row, ADVANTAGE, trial.getL2tCoverage()-trial.getRandoopCoverage());
		addCell(row, METHOD_LENGTH, trial.getMethodLength());
		addCell(row, METHOD_START_LINE, trial.getMethodStartLine());
		writeWorkbook();
		return lastDataSheetRow;
	}

	private Row newDataSheetRow() {
		return dataSheet.createRow(++lastDataSheetRow);
	}
	
}
