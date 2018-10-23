package learntest.activelearning.core.progress;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;

import learntest.activelearning.core.utils.excel.SimpleExcelWriter;

public class ProgressExcelWriter extends SimpleExcelWriter<ProgressRow>{
	
	public ProgressExcelWriter(File file) throws Exception {
		super(file, ProgressHeader.values());
	}
	
	@Override
	public int addRowData(ProgressRow rowData) throws IOException {
		Row row = newDataSheetRow();
		addRowData(row, rowData);
		return 0;
	}

	@Override
	protected void addRowData(Row row, ProgressRow rowData) throws IOException {
		// TODO Auto-generated method stub
		addCell(row, 0, rowData.getMethodName());
		for(int i=0; i < rowData.getProgress().length; i++) {
			addCell(row, i+1, rowData.getProgress()[i]);
		}
		writeWorkbook();
	}



}
