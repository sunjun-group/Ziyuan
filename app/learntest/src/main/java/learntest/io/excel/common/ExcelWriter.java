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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author LLT
 *
 */
public class ExcelWriter {
	protected Workbook workbook;
	private File file;
	
	public ExcelWriter(File file) throws Exception {
		reset(file);
	}

	public void reset(File file) throws Exception {
		this.file = file;
		if (!file.exists()) {
			initFromNewFile(file);
			writeWorkbook();
		} else {
			initFromExistingFile(file);
		}
	}
	
	protected void initFromExistingFile(File file) throws Exception {
		InputStream inp = new FileInputStream(file);
		workbook = WorkbookFactory.create(inp);
	}

	protected void initFromNewFile(File file) {
		workbook = new XSSFWorkbook();
	}

	public Sheet createSheet(String name) {
		return workbook.createSheet(name);
	}
	
	public void writeWorkbook() throws IOException{
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			workbook.write(out); 
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
	}
	
	public void addCell(Row row, ExcelHeader title, double value) {
		row.createCell(title.getCellIdx()).setCellValue(value);
	}

	public void addCell(Row row, ExcelHeader title, String value) {
		row.createCell(title.getCellIdx()).setCellValue(value);
	}
	
}
