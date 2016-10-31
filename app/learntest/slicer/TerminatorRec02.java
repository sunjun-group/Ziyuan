package testdata.numeric;

public class TerminatorRec02 {
	
	public static int fact(int x) {
		 if (x > 1) {
			 int y = fact(x - 1);
			 return y * x;
		 }
		 return 1;
	 }
	
	public static void main(String[] args) {
		TerminatorRec02.fact(5);
	}

}
