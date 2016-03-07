package microbat.evaluation.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import microbat.evaluation.model.Trial;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReporter {
	
	private Workbook book; 
	private Sheet sheet;
	
	private int bookSize = 0;
	
	public ExcelReporter(){
		this.bookSize = 0;
	}

	public void start(){
		book = new XSSFWorkbook(); 
		sheet = book.createSheet("data");  
        Row row = sheet.createRow((short) 0); 
        
        String titles[] = {"test case",
        		"is bug found",
        		"total steps",
        		"jump steps",
        		"mutaed file",
        		"mutated line number",
        		"jump steps"};
        for(int i = 0; i < titles.length; i++){
        	row.createCell(i).setCellValue(titles[i]); 
        }
        
        bookSize = 1;
	}
	
	public void export(List<Trial> trials, String fileName){
        try {
        	for(Trial trial: trials){
        		Row row = sheet.createRow(bookSize);
        		
        		row.createCell(0).setCellValue(trial.getTestCaseName());
        		row.createCell(0).setCellValue(trial.isBugFound());
        		row.createCell(0).setCellValue(trial.getTotalSteps());
        		row.createCell(0).setCellValue(trial.getJumpSteps().size());
        		row.createCell(0).setCellValue(trial.getMutatedFile());
        		row.createCell(0).setCellValue(trial.getMutatedLineNumber());
        		row.createCell(0).setCellValue(trial.getJumpSteps().toString());
        		
        		bookSize++;
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        writeToExcel(fileName);
	}

	private void writeToExcel(String fileName){
		try {
			FileOutputStream fileOut = new FileOutputStream(fileName + ".xlsx");
			book.write(fileOut); 
			fileOut.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
