package learntest.plugin.utils;

public class Settings {
	private static int bound = 1000;
	private static int arrayLengthBound = 500;
	
	public static int  getBound(){
		return bound;
	}
	
	public static int getArrayLengthBound() {
		return arrayLengthBound;
	}

	private static int selectiveNumber = 20;
	public static double formulaAccThreshold = 0.5;
	public static int getSelectiveNumber() {
		return selectiveNumber;
	}
}
