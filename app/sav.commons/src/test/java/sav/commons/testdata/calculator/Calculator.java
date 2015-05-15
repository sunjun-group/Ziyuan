package sav.commons.testdata.calculator;

public class Calculator {

	public static int getSum(int x, int y) {
		if(x > 3){
			x = x + 1;
		}
		
		return x + y;
	}
	
	public static boolean validateGetSum(int x, int y, int max) {
		return (max == x + y);
	}

}
