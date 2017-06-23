package sav.settings;

public class SAVTimer {
	public static boolean enableExecutionTimeout = false;
	public static long exeuctionTimeout = 600000;
	
	public static long startTime = 0;
	
	public static void startCount(){
		startTime = System.currentTimeMillis();
	}
	
	public static boolean isTimeOut() {
		if (enableExecutionTimeout) {
			long time = System.currentTimeMillis();
			if (time - startTime > exeuctionTimeout) {
				return true;
			}
		}
		return false;
	}

	public static long getExecutionTime() {
		long time = System.currentTimeMillis();
		return time - startTime;
	}
}
