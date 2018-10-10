package sav.utils;

public class ExecutionTimerUtils {
	private ExecutionTimerUtils() {}
	private static IExecutionTimer execTimer;
	
	public static IExecutionTimer getExecutionTimer(boolean enableTimeout) {
		IExecutionTimer timer;
		if (!enableTimeout) {
			timer = new IExecutionTimer() {
				
				@Override
				public boolean run(TestRunner target, long timeout) {
					target.run();
					return true;
				}
				
				@Override
				public void refresh() {
					
				}
			};
		} else {
			if (execTimer == null) {
				execTimer = new CountDownExecutionTimer();
			}
			timer = execTimer;
		}
		return timer;
	}
}
