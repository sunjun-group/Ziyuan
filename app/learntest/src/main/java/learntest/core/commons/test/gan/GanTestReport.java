/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;

import learntest.plugin.export.io.excel.common.ExcelHeader;
import learntest.plugin.export.io.excel.common.ExcelSettings;
import learntest.plugin.export.io.excel.common.SimpleExcelHandler;
import learntest.plugin.export.io.excel.common.SimpleExcelReader;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;

/**
 * @author LLT
 *
 */
public class GanTestReport extends SimpleExcelHandler<GanTrial> {
	private static final String SHEET_NAME = "gan";

	public GanTestReport(ExcelSettings settings) throws Exception {
		super(settings);
	}

	@Override
	protected SimpleExcelReader initExcelReader() {
		return new SimpleExcelReader(SHEET_NAME, Header.values());
	}

	@Override
	protected SimpleExcelWriter<GanTrial> initExcelWriter(File file) throws Exception {
		return new SimpleExcelWriter<GanTrial>(file) {

			@Override
			protected void addRowData(Row row, GanTrial rowData) throws IOException {
				// TODO Auto-generated method stub
			}
		};
	}

	private static enum Header implements ExcelHeader {
		METHOD_ID("Method name"),
		;
		
		private String title;
		private Header(String title) {
			this.title = title;
		}
		
		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public int getCellIdx() {
			return ordinal();
		}
		
	}
}
