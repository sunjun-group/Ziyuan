
package mutation.mutator;

public class VariableSubstitutionClass {

	public int a;
	public double b;
	private int c;
	public String d;
	
	public void method1(){
		int x1 = 0;
		x1 = x1 + a;
		int x2 = 1;
		x2 = x1 + x2;
	}
	
	public void method2(int para1){
		int x1 = 0;
		x1 = a + c;
		
		for(int i = 0; i < 10; i++){
			double x2 = x1 * 3;
		}
	}
}
