package microbat.evaluation.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import microbat.evaluation.model.Trial;
import microbat.util.Settings;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

public class ExcelReader {
	public Set<Trial> readXLSX() throws IOException {
		Set<Trial> set = new HashSet<>();

		String projectName = Settings.projectName;
		int num = 0;

		String fileName = projectName + num;
		File file = new File(fileName);
		while (file.exists()) {
			InputStream excelFileToRead = new FileInputStream(file);
			
			@SuppressWarnings("resource")
			HSSFWorkbook wb = new HSSFWorkbook(excelFileToRead);

			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;

			int rowNum = 0;

			Iterator<Row> rows = sheet.rowIterator();

			while (rows.hasNext()) {
				row = (HSSFRow) rows.next();

				if (rowNum > 0) {
					Trial trial = new Trial();
					for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
						cell = row.getCell(0);
						if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
							String content = cell.getStringCellValue();
							switch (i) {
							case 0:
								trial.setTestCaseName(content);
								break;
							case 1:
								boolean isBugFound = Boolean.valueOf(content);
								trial.setBugFound(isBugFound);
								break;
							case 4:
								trial.setMutatedFile(content);
								break;
							case 5:
								int linNum = Integer.valueOf(content);
								trial.setMutatedLineNumber(linNum);
								break;
							case 7:
								trial.setResult(content);
								break;
							}
						}
					}

//					Iterator<Cell> cells = row.cellIterator();
//					while (cells.hasNext()) {
//						cell = (HSSFCell) cells.next();
//
//						if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
//							String testCaseName = cell.getStringCellValue();
//						}
//
//					}

					rowNum++;
				}
			}

			num++;
			fileName = projectName + num;
			file = new File(fileName);
		}

		return set;
	}
}
