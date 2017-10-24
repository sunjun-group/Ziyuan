/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel.common;

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
	
	protected ExcelWriter() throws Exception {
	}
	
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
	
	public Sheet createSheet(String name, ExcelHeader[] headers, int headerRowIdx) {
		Sheet sheet = createSheet(name);
		initDataSheetHeader(sheet, headers, headerRowIdx);
		return sheet;
	}
	
	public void initDataSheetHeader(Sheet sheet, ExcelHeader[] headers, int headerRowIdx) {
		Row headerRow = newDataSheetRow(sheet, headerRowIdx);
		for (ExcelHeader header : headers) {
			addCell(headerRow, header, header.getTitle());
		}
	}
	
	protected Row newDataSheetRow(Sheet dataSheet, int headerRowIdx) {
		return dataSheet.createRow(headerRowIdx);
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
	
	public void addCell(Row row, int cellIdx, double value) {
		row.createCell(cellIdx).setCellValue(value);
	}

	public void addCell(Row row, int cellIdx, String value) {
		row.createCell(cellIdx).setCellValue(value);
	}
	
	public Sheet getSheet(String name, ExcelHeader[] headers, int headerRowIdx) {
		Sheet sheet = workbook.getSheet(name);
		if (sheet == null) {
			sheet = createSheet(name, headers, headerRowIdx);
		}
		return sheet;
	}

}
