package testdata.benchmark;

public class ToHex {

	 public String ToHexTest(int value)
     {
			if(value < 0){
				System.out.println("Can only convert positive integers");
				return "-1";
			}

         StringBuilder sb = new StringBuilder();
         while (value > 0)
         {
             int result = value % 16;
             if (result < 10)
             {
                 sb.append(result);
             }
             else
             {
                 sb.append(GetHexSymbol(result));
             }

             value /= 16;
         }

         return sb.reverse().toString();
     }
	 
	 
	 private char GetHexSymbol(int result)
     {
         char symbol = ' ';

         // match relevent symbol with result
         switch (result)
         {
             case 10:
                 symbol = 'A';
                 break;
             case 11:
                 symbol = 'B';
                 break;
             case 12:
                 symbol = 'C';
                 break;
             case 13:
                 symbol = 'D';
                 break;
             case 14:
                 symbol = 'E';
                 break;
             case 15:
                 symbol = 'F';
                 break;
         }

         return symbol;
     }
}

