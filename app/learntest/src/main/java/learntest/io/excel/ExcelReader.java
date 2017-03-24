package learntest.io.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import learntest.main.LearnTestConfig;

public class ExcelReader {
	
	private HashMap<String, List<Integer>> parsedMethodSet = new HashMap<>();
	
	@SuppressWarnings("resource")
	public void readXLSX() throws IOException {
		
		String projectName = LearnTestConfig.projectName;
		int filePage = 0;

		String fileName = "experimentresults.xlsx";
		
		File file = new File(fileName);
		while(file.exists()) {
			InputStream excelFileToRead = new FileInputStream(file);
			
			XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);
			XSSFSheet sheet = wb.getSheetAt(1);
			XSSFRow row;
			XSSFCell cell;

			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				row = (XSSFRow) rows.next();

				if (row.getRowNum() > 0) {
					cell = row.getCell(0);
					String methodName = cell.getStringCellValue();
					
					List<Integer> rowList = getParsedMethodSet().get(methodName);
					if(rowList==null){
						rowList = new ArrayList<>();
					}
					rowList.add(row.getRowNum());
					this.parsedMethodSet.put(methodName, rowList);
				}
			}
			
			filePage++;
			fileName = projectName + filePage + ".xlsx";
			file = new File(fileName);
		}
	}

	public HashMap<String, List<Integer>> getParsedMethodSet() {
		return parsedMethodSet;
	}

	public void setParsedMethodSet(HashMap<String, List<Integer>> parsedMethodSet) {
		this.parsedMethodSet = parsedMethodSet;
	}

}
