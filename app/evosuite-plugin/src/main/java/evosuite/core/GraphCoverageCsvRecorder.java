package evosuite.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import evosuite.core.EvosuiteRunner.EvosuiteResult;
import learntest.activelearning.core.coverage.CoverageUtils;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import sav.common.core.utils.ResourceUtils;
import sav.common.core.utils.StringUtils;
import sav.common.core.utils.TextFormatUtils;

public class GraphCoverageCsvRecorder {
	private String filePath;
	
	public GraphCoverageCsvRecorder(String graphCoverageFilePath) {
		this.filePath = graphCoverageFilePath;
	}

	public void record(String classMethod, int line, CoverageOutput graphCoverage, EvosuiteResult result, CFGInstance cfgInstance) {
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
			double branchCoverage = -1;
			if (graphCoverage != null && graphCoverage.getCoverageGraph() != null) {
				branchCoverage = CoverageUtils.getBranchCoverage(graphCoverage.getCoverageGraph(), methodId);
			}
			csvPrinter.printRecord(methodId, branchCoverage, 
					StringUtils.join(CoverageUtils.getBranchCoverageDisplayTexts(graphCoverage.getCoverageGraph(), cfgInstance),  "\n"),
					result.branchCoverage,
					StringUtils.join(result.coverageInfo, "\n"));
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
		graph_coverage,
		graph_coverage_info,
		old_coverage,
		old_coverage_info;
		
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
