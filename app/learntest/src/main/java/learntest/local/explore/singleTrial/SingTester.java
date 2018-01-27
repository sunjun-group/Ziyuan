package learntest.local.explore.singleTrial;

import java.io.File;

public class SingTester {
	public static void main(String[] args) {
		String root = "E:\\172\\SUTD\\statistic\\";
		String input = "jscience_1.xlsx";
//		input = "jblas_0.xlsx";
		input = "colt_2.xlsx";
		input = "apache-common-math-2.2_1117_0955.xlsx";
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
