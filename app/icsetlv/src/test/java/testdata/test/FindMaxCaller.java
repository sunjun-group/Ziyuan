package testdata.test;

import java.util.ArrayList;
import java.util.Random;

public class FindMaxCaller {

  static ArrayList<Integer> testArray = new ArrayList<Integer>();

  public void execute() {
    Random random = new Random();
    // init array
    for (int i = 0; i < 10; i++) {
      testArray.add(random.nextInt(100));
    }
    testArray.add(120);

    int max = FindMax.findMax(testArray);
    System.out.println("The maximum element of the array is " + max);
  }

}
