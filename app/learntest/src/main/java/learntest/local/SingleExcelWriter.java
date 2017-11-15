package learntest.local;

import static learntest.local.SingleHeader.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import learntest.plugin.export.io.excel.common.SimpleExcelWriter;
import mosek.Env.networkdetect;

public class SingleExcelWriter extends SimpleExcelWriter<MethodTrial> {

	public SingleExcelWriter(File file) throws Exception {
		super(file, SingleHeader.values());
	}

	protected void addRowData(Row row, MethodTrial trial) throws IOException {
		export5Trials(row, trial.getTrials());
		writeWorkbook();
	}

	private void export5Trials(Row row, List<DetailTrial> trials) {
		for (int i = 0; i < trials.size() && i < 5; i++) {
			DetailTrial trial = trials.get(i);
			addCell(row, TRIAL, trial.getMethodName()+"."+(i+1));
			addCell(row, L2T_COVERAGE, trial.getL2t());
			addCell(row, LEARNSTATE, trial.getLearnedState()>0 ? 1:0);
			addCell(row, RANDOOP_COVERAGE, trial.getRandoop());
			addCell(row, JDART_COVERAGE, trial.getJdart());
		}
	}
	
	public void export(String oldXlsx){
		DetailExcelReader reader;
		try {
			reader = new DetailExcelReader(new File(oldXlsx));
			List<MethodTrial> methodTrials = reader.readDataSheet();
			for (MethodTrial methodTrial : methodTrials) {
				addRowData(methodTrial);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String output = "";
		String input = "";
		try {
			SingleExcelWriter writer = new SingleExcelWriter(new File(output));
			writer.export(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
