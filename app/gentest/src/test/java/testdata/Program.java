package testdata;

import japa.parser.ast.type.PrimitiveType.Primitive;

import java.util.ArrayList;

import org.junit.Assert;



public class Program {
	
	public void testBoundedStack(BoundedStack stack) {
		Assert.assertTrue(SampleProgram.checkMax(1, 3, 3, 3));
		stack.push(23);
		System.out.println(stack.toString());
	}
	
	public void testBoundedStack(ArrayList<?> list) {
		System.out.println(list.toString());
	}
	
	public void print() {
		String original = "sdfDSF";
		String str = new String(original);
		int beginIndex = 0;
		int endIndex = 3;
		str.substring(beginIndex , endIndex);
		String substring = str.substring(beginIndex , endIndex);
		Long c = null;
		Integer a = 1;
		Enum<?> b = Primitive.Boolean;
		System.out.println(b.getClass().getSimpleName());
		System.out.println(Enum.class.isEnum());
	}
	
	public void Test(int a, int b) throws Exception {
		int result = Mid(a, b);

		if (a < b) {
			if (result != b) {
				throw new Exception("");
			}
		} else {
			if (result != a) {
				throw new Exception("");
			}
		}
	}

	public int Mid(int a, int b) {
		System.out.println("a = " + a + ", b = " + b);
		if (a < b) {
			return b;
		} else {
			return b + 1;
		}

	}
	
}
