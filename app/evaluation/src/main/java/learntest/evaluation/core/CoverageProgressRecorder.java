package learntest.evaluation.core;

import java.io.File;

import learntest.activelearning.core.progress.ProgressExcelWriter;
import learntest.activelearning.core.progress.ProgressRow;
import learntest.core.commons.data.classinfo.MethodInfo;

public class CoverageProgressRecorder {
	
	public void record(MethodInfo targetMethod, double[] progress) throws Exception {
		ProgressExcelWriter writer = new ProgressExcelWriter(new File("D:/progress.xlsx"));
		ProgressRow trial = new ProgressRow();
		trial.setMethodName(targetMethod.getMethodFullName() + '.' + targetMethod.getLineNum());
		trial.setProgress(progress);
		writer.addRowData(trial);
	}
}
