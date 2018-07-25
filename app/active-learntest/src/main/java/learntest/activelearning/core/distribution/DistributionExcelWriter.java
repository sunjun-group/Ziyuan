package learntest.activelearning.core.distribution;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;

import learntest.plugin.export.io.excel.common.ExcelHeader;
import learntest.plugin.export.io.excel.common.ExcelWriter;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;

import learntest.activelearning.core.distribution.DistributionHeader;

public class DistributionExcelWriter extends SimpleExcelWriter<DistributionRow> {

	public DistributionExcelWriter(File file) throws Exception {
		super(file, DistributionHeader.values());
	}
	
	@Override
	public int addRowData(DistributionRow rowData) throws IOException {
		Row row = newDataSheetRow();
		addRowData(row, rowData);
		return 0;
	}

	@Override
	protected void addRowData(Row row, DistributionRow rowData) throws IOException {
		// TODO Auto-generated method stub
		addCell(row, 0, rowData.getMethodName());
		for(int i=0; i < rowData.getDistribution().length; i++) {
			addCell(row, i+1, rowData.getDistribution()[i]);
			//System.out.println(rowData.getDistribution()[i]);
		}
		writeWorkbook();
	}


}
