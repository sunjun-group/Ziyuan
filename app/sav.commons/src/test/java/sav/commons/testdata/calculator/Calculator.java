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
	
	public static int getSumArray(int [] A){
		if(A[0] > 0){
			A[0]++;
		}
		
		return A[0] + A[1];
	}
	
	public static boolean validateGetSumArray(int [] A, int sum){
		int expectSum = 0;
		for(int a: A){
			expectSum += a;
		}
		
		return expectSum == sum;
	}

}
