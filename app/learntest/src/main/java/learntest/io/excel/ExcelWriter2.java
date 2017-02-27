package learntest.io.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter2 {
	private File file;
	private Sheet sheet;
	private Workbook book;
	public ExcelWriter2(){
		
		String fileName = "experimentresults.xlsx";
		file = new File(fileName);
		
		if(file.exists()){
			InputStream excelFileToRead;
			try {
				excelFileToRead = new FileInputStream(file);
				book = new XSSFWorkbook(excelFileToRead);
				sheet = book.getSheetAt(1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

		
	public void export(int rowNum, int value, String method) {
		Row row = sheet.getRow(rowNum);
		
		boolean doNotWrite = false;
		
		while(row.getCell(5).getNumericCellValue() != -1){
			rowNum++;
			row = sheet.getRow(rowNum);
			
			String nextMethod = row.getCell(0).getStringCellValue();
			if(!nextMethod.equals(method)){
				doNotWrite = true;
				break;
			}
		}
		
		if(!doNotWrite){
			row.getCell(5).setCellValue(value);
			writeToExcel(book, file.getName());			
		}
	}

	private void writeToExcel(Workbook book, String fileName){
		try {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			book.write(fileOut); 
			fileOut.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
