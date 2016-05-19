package testdata.benchmark;

public class ToOctal {

	 public  int ToOctalTest(int value)
     {
			if(value < 0){
				System.out.println("Can only convert positive integers");
				return -1;
			}

         StringBuilder sb = new StringBuilder();
         while (value > 0)
         {
             sb.append(value % 8);
             value /= 8;
         }
         

         //return Int32.Parse(sb.ToString().Reverse(), CultureInfo.InvariantCulture);
         return Integer.parseInt(sb.reverse().toString());
     }
}
