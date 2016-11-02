package learntest.io.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import learntest.main.LearnTestConfig;

public class ExcelReader {
	
	private HashSet<String> parsedMethodSet;
	
	@SuppressWarnings("resource")
	public void readXLSX() throws IOException {
		
		String projectName = LearnTestConfig.projectName;
		int filePage = 0;

		String fileName = projectName + "_" + filePage + ".xlsx";
		
		File file = new File(fileName);
		while(file.exists()) {
			InputStream excelFileToRead = new FileInputStream(file);
			
			XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;

			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				row = (XSSFRow) rows.next();

				if (row.getRowNum() > 0) {
					cell = row.getCell(0);
					String methodName = cell.getStringCellValue();
					parsedMethodSet.add(methodName);
				}
			}
			
			filePage++;
			fileName = projectName + filePage + ".xlsx";
			file = new File(fileName);
		}
	}

	public HashSet<String> getParsedMethodSet() {
		return parsedMethodSet;
	}

	public void setParsedMethodSet(HashSet<String> parsedMethodSet) {
		this.parsedMethodSet = parsedMethodSet;
	}
}
