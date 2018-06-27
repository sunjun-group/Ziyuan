package svm.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import sav.common.core.utils.ResourceUtils;
import svm.evaluation.SvmEvaluator.LearnInfo;
import svm.evaluation.SvmEvaluator.PolynomialFunction;

/**
 * @author LLT
 *
 */
public class Report {
	
	public void storeCsv(String csvFilePath, PolynomialFunction f, LearnInfo svmInfo, LearnInfo activeSvmInfo) {
		CSVPrinter csvPrinter = null;
		try {
			File csvFile = new File(csvFilePath);
			CSVFormat format = CSVFormat.EXCEL;
			if (!csvFile.exists()) {
				format = format.withHeader(Column.allColumns());
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true));
			csvPrinter = new CSVPrinter(writer, format);
			csvPrinter.printRecord(f.toString(),
					" " + svmInfo.classifier, 
					svmInfo.acc,
					svmInfo.inputAcc,
					" " + activeSvmInfo.classifier,
					activeSvmInfo.acc,
					activeSvmInfo.inputAcc,
					activeSvmInfo.iterations);

			csvPrinter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ResourceUtils.closeQuitely(csvPrinter);
		}
	}
	
	public static enum Column {
		FUNCTION,
		SVM_CLASIFIER,
		SVM_ACC,
		SVM_INPUT_ACC,
		ACTIVE_SVM_CLASIFIER,
		ACTIVE_SVM_ACC,
		ACTIVE_SVM_INPUT_ACC,
		ACTIVE_SVM_ITERATIONS;
		
		public static String[] allColumns() {
			Column[] values = values();
			String[] cols = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				cols[i] = values[i].name();
			}
			return cols;
		}
	}
}
