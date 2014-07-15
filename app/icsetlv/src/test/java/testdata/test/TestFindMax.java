package testdata.test;

import java.util.ArrayList;
import java.util.Random;

public class TestFindMax {
	
	static int maxElement = 0;
	static ArrayList<Integer> testArray = new ArrayList<Integer>();

	public static void main(String[] args) {
		
		for (int i=0; i<10; i++){
			TestFindMax.testArray.add(grandom());
		}
		TestFindMax.maxElement = FindMax.findMax(testArray);
		assert TestFindMax.maxElement > 1000;
		System.out.println("The maximum element of the array is " + TestFindMax.maxElement);
	}
	
	public static int grandom(){
		Random random = new Random();
		return random.nextInt();
	}

}


