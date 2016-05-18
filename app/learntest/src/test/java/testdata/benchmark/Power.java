package testdata.benchmark;

public class Power {

	 public int PowerTest(int baseNumber, int exponent)
     {
			if(exponent < 0){
				System.out.println("Power cannot be called with an exponent less than 0");
				return -1;
			}

         if (exponent == 0)
         {
             return 1; // n^0 = 1
         }

         int power = baseNumber;
         while (exponent > 1)
         {
             power *= baseNumber;
             exponent--;
         }

         return power;
     }
}
