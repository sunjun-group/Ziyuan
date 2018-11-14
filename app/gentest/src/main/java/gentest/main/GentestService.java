package gentest.main;

import gentest.core.data.type.SubTypesScanner;
import gentest.core.execution.VariableRuntimeExecutor;
import sav.common.core.utils.CachePoolExecutionTimer;

public class GentestService {
	
	public static void cleanupThread() {
		CachePoolExecutionTimer executionTimer = VariableRuntimeExecutor.getExecutionTimer();
		executionTimer.cleanUpThreads();
	}
	
	public static void reset() {
		VariableRuntimeExecutor.getExecutionTimer().shutdown();
		SubTypesScanner.getInstance().clear();
	}
}
