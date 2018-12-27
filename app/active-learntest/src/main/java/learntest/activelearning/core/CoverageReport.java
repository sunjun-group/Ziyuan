package learntest.activelearning.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.progress.ProgressExcelWriter;
import learntest.activelearning.core.progress.ProgressRow;

public class CoverageReport {
	public static void generateCoverageReport(MethodInfo targetMethod, String exceptionMessage,
			List<Double> progressCoverages, List<Integer> tcsNum) throws Exception, IOException {
		System.out.println("Total tcs: " + tcsNum);
		ProgressExcelWriter coverageWriter = new ProgressExcelWriter(new File("E:/linyun/coverage_report.xlsx"));
		ProgressRow trial = new ProgressRow();
		trial.setMethodName(targetMethod.getMethodFullName() + '.' + targetMethod.getLineNum());
		trial.setErrorMessage(exceptionMessage);
		double[] progress = new double[progressCoverages.size()];
		int i = 0;
		for (Double cvg : progressCoverages) {
			progress[i++] = cvg;
		}
		trial.setProgress(progress);
		coverageWriter.addRowData(trial);
		
		ProgressExcelWriter testNumWriter = new ProgressExcelWriter(new File("E:/linyun/test_number_report.xlsx"));
		trial.setMethodName(targetMethod.getMethodFullName() + '.' + targetMethod.getLineNum());
		double[] tcsnum = new double[tcsNum.size()];
		i = 0;
		for (Integer num : tcsNum) {
			tcsnum[i++] = (double)(num.intValue()); 
		}
		trial.setProgress(tcsnum);
		testNumWriter.addRowData(trial);
	}
}
