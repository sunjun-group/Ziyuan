package testdata.benchmark;

public class Factorial {

	  public  int FactorialTest( int number)
      {
			if(number < 0){
				System.out.println("Factorial cannot be called with an integer value less than 0");
				return -1;
			}

          if (number < 2)
          {
              return 1;
          }

          int factorial = 1;
          for (int i = 2; i <= number; i++)
          {
              factorial *= i;
          }

          return factorial;
      }
}
