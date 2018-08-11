package sav.utils;

public class ExecutionTimerUtils {
	private ExecutionTimerUtils() {}
	
	public static IExecutionTimer getExecutionTimer(long timeout) {
		IExecutionTimer timer;
		if (timeout <= 0) {
			timer = new IExecutionTimer() {
				
				@Override
				public boolean run(TestRunner target, long timeout) {
					target.run();
					return true;
				}
			};
		} else {
			timer = new CountDownExecutionTimer();
		}
		return timer;
	}
}
