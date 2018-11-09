package learntest.activelearning.core.data;

import java.util.List;

import sav.strategies.dto.execute.value.ExecVar;

/**
 * for centralization of global data.
 * */
public class LearnTestContext {
	private static LearnTestContext instance;
	private LearnDataSetMapper datasetMapper;
	
	public static LearnDataSetMapper getLearnDataSetMapper() {
		return instance.datasetMapper;
	}

	public static void setDatasetMapper(List<ExecVar> learningVarsSet, int learnArraySizeThreshold) {
		instance.datasetMapper = new LearnDataSetMapper(learningVarsSet, learnArraySizeThreshold);
	}

	public static void init() {
		instance = new LearnTestContext();
	}
	
	public static void dispose() {
		instance = null;
	}
}
