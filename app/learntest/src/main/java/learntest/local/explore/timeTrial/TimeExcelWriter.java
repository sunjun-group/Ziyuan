package learntest.local.explore.timeTrial;

import static learntest.local.explore.timeTrial.TimeHeader.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import learntest.local.explore.basic.DetailExcelReader;
import learntest.local.explore.basic.DetailTrial;
import learntest.local.explore.basic.MethodTrial;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;

public class TimeExcelWriter extends SimpleExcelWriter<MethodTrial> {
	
	public TimeExcelWriter(File file) throws Exception {
		super(file, TimeHeader.values());
	}

	protected void addRowData(Row row, MethodTrial trial) throws IOException {
		exportTrials(row, trial);
		writeWorkbook();
	}

	private void exportTrials(Row row, MethodTrial trial) {
		addCell(row, METHOD, trial.getMethodName() + "_" + trial.getLine());
		addCell(row, L2T_ADV_TIME, trial.getL2tTime());
		addCell(row, RANDOOP_ADV_TIME, trial.getRandoopTime());
	}

	public void export(String oldXlsx) {
		DetailExcelReader reader;
		try {
			reader = new DetailExcelReader(new File(oldXlsx));
			List<MethodTrial> methodTrials = reader.readDataSheet();
			for (MethodTrial methodTrial : methodTrials) {
				boolean learnedState = false;
				for (int i = 0; i < methodTrial.getTrials().size(); i++) {
					DetailTrial trial = methodTrial.getTrials().get(i);
					if (trial.getLearnedState() > 0) {
						learnedState = true;
						break;
					}
				}
				if (learnedState) {
					addRowData(methodTrial);
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
		List<String> paths = new LinkedList<>();
		paths.add("jscience_1.xlsx");
		paths.add("jblas_0.xlsx");
		paths.add("colt_2.xlsx");
		paths.add("apache-common-math-2.2_1117_0955.xlsx");
		String lable = System.currentTimeMillis() + "";
		String output = "time_"+ lable + ".xlsx";
		for (String input : paths) {
			output = "time_" + input;
			TimeExcelWriter.explore(root + input, root + output);
		}

	}
	
	public static void explore(String input, String output){
		try {
			File file = new File(output);
			TimeExcelWriter writer = new TimeExcelWriter(file);
			writer.export(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
