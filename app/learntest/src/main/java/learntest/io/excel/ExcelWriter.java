package learntest.io.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {
	private File file;
	private Sheet sheet;
	private Workbook book;
	private int lastRowNum = 1;
	
	private int filePage = 0;
	private int trialNumberLimitPerFile = 3000;
	
	private String projectName;
	
	public ExcelWriter(String projectName) throws IOException{
		this.projectName = projectName;
		
		String fileName = projectName + "_" + filePage + ".xlsx";
		file = new File(fileName);
		
		while(file.exists()){
			InputStream excelFileToRead = new FileInputStream(file);
			book = new XSSFWorkbook(excelFileToRead);
			sheet = book.getSheetAt(0);
			
			lastRowNum = sheet.getPhysicalNumberOfRows();
			if(lastRowNum > trialNumberLimitPerFile){
				filePage++;
				fileName = projectName + filePage + ".xlsx";
				file = new File(fileName);
			}
			else{
				break;
			}
		}
		
		if(!file.exists()){
			initializeNewExcel();
		}
	}

	private void initializeNewExcel() {
		lastRowNum = 1;
		
		book = new XSSFWorkbook();
		sheet = book.createSheet("data");
		
		List<String> titles = new ArrayList<String>();
		titles.add("method name");
		titles.add("l2t time");
		titles.add("l2t coverage");
		titles.add("l2t test cnt");
		titles.add("randoop time");
		titles.add("randoop coverage");
		titles.add("randoop test cnt");
		
		Row row = sheet.createRow(0);
		for(int i = 0; i < titles.size(); i++){
			row.createCell(i).setCellValue(titles.get(i)); 
		}
	}
	
	public void export(Trial trial) {
		Row row = sheet.createRow(lastRowNum);
		
		fillRowInformation(row, trial);
		
		writeToExcel(book, file.getName());
        
        lastRowNum++;
        
        if(lastRowNum > trialNumberLimitPerFile){
        	filePage++;
        	String fileName = projectName + filePage + ".xlsx";
			file = new File(fileName);
			
			initializeNewExcel();
        }
	}

	private void fillRowInformation(Row row, Trial trial) {
		row.createCell(0).setCellValue(trial.getMethodName());
		row.createCell(1).setCellValue(trial.getL2tTime());
		row.createCell(2).setCellValue(trial.getL2tCoverage());
		row.createCell(3).setCellValue(trial.getL2tTestCnt());
		row.createCell(4).setCellValue(trial.getRandoopTime());
		row.createCell(5).setCellValue(trial.getRandoopCoverage());
		row.createCell(6).setCellValue(trial.getRandoopTestCnt());
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
