package learntest.local;

import static learntest.local.SingleHeader.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import learntest.plugin.export.io.excel.common.SimpleExcelWriter;
import mosek.Env.networkdetect;

public class SingleExcelWriter extends SimpleExcelWriter<DetailTrial> {

	public SingleExcelWriter(File file) throws Exception {
		super(file, SingleHeader.values());
	}

	protected void addRowData(Row row, DetailTrial trial) throws IOException {
		exportTrials(row, trial);
		writeWorkbook();
	}

	private void exportTrials(Row row, DetailTrial trial) {
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
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String root = "E:\\git\\test-projects\\jscience\\jscience-master\\learntest\\";
		String input = "colt_0.xlsx";
		String output = "single_" + input;
		try {
			SingleExcelWriter writer = new SingleExcelWriter(new File(root + output));
			writer.export(root + input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
