package learntest.local.explore.singleTrial;

import static learntest.local.explore.singleTrial.SingleHeader.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import learntest.local.explore.basic.DetailExcelReader;
import learntest.local.explore.basic.DetailTrial;
import learntest.local.explore.basic.MethodTrial;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;
import mosek.Env.networkdetect;

public class SingleExcelWriter extends SimpleExcelWriter<DetailTrial> {
	int jdartSolveTimesTotal = 0;
	int l2tSolveTimesTotal = 0;
	
	public SingleExcelWriter(File file) throws Exception {
		super(file, SingleHeader.values());
	}

	protected void addRowData(Row row, DetailTrial trial) throws IOException {
		exportTrials(row, trial);
		writeWorkbook();
	}

	private void exportTrials(Row row, DetailTrial trial) {
		int jdartSolveTimes = trial.getJdartSolveTimes() >= 0 ? trial.getJdartSolveTimes() : 0;
		int l2tSolveTimes = trial.getL2tSolveTimes() >= 0 ? trial.getL2tSolveTimes() : 0;
		jdartSolveTimesTotal += jdartSolveTimes;
		l2tSolveTimesTotal += l2tSolveTimes;
		addCell(row, TRIAL, trial.getMethodName() + "." + trial.getIndex());
		addCell(row, L2T_COVERAGE, trial.getL2t());
		addCell(row, LEARNSTATE, trial.getLearnedState() > 0 ? 1 : 0);
		addCell(row, RANDOOP_COVERAGE, trial.getRandoop());
		addCell(row, JDART_COVERAGE, trial.getJdart());
	}

	public void export(String oldXlsx) {
		DetailExcelReader reader;
		try {
			reader = new DetailExcelReader(new File(oldXlsx));
			List<MethodTrial> methodTrials = reader.readDataSheet();
			for (MethodTrial methodTrial : methodTrials) {
				for (int i = 0; i < methodTrial.getTrials().size(); i++) {
					DetailTrial trial = methodTrial.getTrials().get(i);
					trial.setIndex(i+1);
					addRowData(trial);
				}
				System.out.println(methodTrial.getMethodName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String root = "E:\\172\\SUTD\\statistic\\";
		String input = "jscience_1.xlsx";
//		input = "jblas_0.xlsx";
//		input = "colt_2.xlsx";
//		input = "apache-common-math-2.2_17_0955.xlsx";
		String output = "single_" + input;
		try {
			File file = new File(root + output);
			if (file.exists()) {
				file.delete();
			}
			SingleExcelWriter writer = new SingleExcelWriter(file);
			writer.export(root + input);
			System.out.format("%s jdartSolves : %d, l2tSolves : %d%n", input, writer.jdartSolveTimesTotal, writer.l2tSolveTimesTotal);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
