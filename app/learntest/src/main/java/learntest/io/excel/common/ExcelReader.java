/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.io.excel.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author LLT
 *
 */
public class ExcelReader {
	private InputStream in;
	protected Workbook workbook;
	
	public ExcelReader() {
		
	}
	
	public ExcelReader(File file) throws Exception {
		reset(file);
	}

	public void reset(File file) throws Exception {
		close();
		in = new FileInputStream(file);
		workbook = new XSSFWorkbook(in);
	}
	
	protected String getStringCellValue(Row row, ExcelHeader header) {
		return row.getCell(header.getCellIdx()).getStringCellValue();
	}
	
	protected double getDoubleCellValue(Row row, ExcelHeader header) {
		return row.getCell(header.getCellIdx()).getNumericCellValue();
	}
	
	protected int getIntCellValue(Row row, ExcelHeader header) {
		return (int)getDoubleCellValue(row, header);
	}
	
	protected long getLongCellValue(Row row, ExcelHeader header) {
		return (long)getDoubleCellValue(row, header);
	}

	public void close() throws IOException {
		if (in != null) {
			in.close();
		}
	}
}
