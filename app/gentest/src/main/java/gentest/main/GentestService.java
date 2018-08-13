package gentest.main;

import gentest.core.execution.VariableRuntimeExecutor;
import sav.common.core.utils.CachePoolExecutionTimer;

public class GentestService {
	
	public static void cleanupThread() {
		CachePoolExecutionTimer executionTimer = VariableRuntimeExecutor.executionTimer;
		if (executionTimer.hasUnStoppableTask()) {
			executionTimer.shutdown();
		}
	}
	
	public static void reset() {
		VariableRuntimeExecutor.executionTimer.shutdown();
	}
}
