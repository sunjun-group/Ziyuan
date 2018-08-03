package evosuite.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import learntest.activelearning.core.coverage.CoverageUtils;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import sav.common.core.utils.ResourceUtils;

public class GraphCoverageCsvRecorder {
	private String filePath;
	
	public GraphCoverageCsvRecorder(String graphCoverageFilePath) {
		this.filePath = graphCoverageFilePath;
	}

	public void record(String classMethod, int line, CoverageOutput graphCoverage) {
		CSVPrinter csvPrinter = null;
		BufferedWriter writer = null;
		try {
			File csvFile = new File(filePath);
			CSVFormat format = CSVFormat.EXCEL;
			if (!csvFile.exists()) {
				format = format.withHeader(Column.allColumns());
			}
			writer = new BufferedWriter(new FileWriter(csvFile, true));
			csvPrinter = new CSVPrinter(writer, format);
			String methodId = String.format("%s.%s", classMethod, line);
			csvPrinter.printRecord(methodId,
					CoverageUtils.getBranchCoverage(graphCoverage.getCoverageGraph(), methodId));
			csvPrinter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ResourceUtils.closeQuitely(csvPrinter);
			ResourceUtils.closeQuitely(writer);
		}
	}

	public static enum Column {
		target_method,
		graph_coverage;
		
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
