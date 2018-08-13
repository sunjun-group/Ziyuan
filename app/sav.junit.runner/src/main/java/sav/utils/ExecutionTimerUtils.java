package sav.utils;

public class ExecutionTimerUtils {
	private ExecutionTimerUtils() {}
	
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
				public void shutdown() {
					
				}
			};
		} else {
			timer = new CountDownExecutionTimer();
		}
		return timer;
	}
}
