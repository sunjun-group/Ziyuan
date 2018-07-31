package learntest.activelearning.core.progress;

import learntest.plugin.export.io.excel.common.ExcelHeader;

public enum ProgressHeader implements ExcelHeader {	
	EXCEL_HEADER("MethodProgress")
	;
	
	private String title;

	private ProgressHeader(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public int getCellIdx() {
		return ordinal();
	}


}
