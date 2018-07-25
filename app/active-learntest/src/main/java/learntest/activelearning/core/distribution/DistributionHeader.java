package learntest.activelearning.core.distribution;

import learntest.plugin.export.io.excel.common.ExcelHeader;

public enum  DistributionHeader implements ExcelHeader{
	EXCEL_HEADER("MethodDistribution")
	;
	
	private String title;

	private DistributionHeader(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public int getCellIdx() {
		return ordinal();
	}

}
