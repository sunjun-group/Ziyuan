package testdata.boundedStack.tzuyu.pass;

import org.junit.Test;

import testdata.boundedStack.BoundedStack;

public class BoundedStack6 { 
	public static boolean debug = false;

	@Test
	public void test1() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test1");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-407);
		boolean b0 = boundedStack0.push(i0);
	}

	@Test
	public void test2() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test2");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = 882;
		boolean b0 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i3 = boundedStack1.size();
		Integer i4 = 882;
		boolean b1 = boundedStack1.push(i4);
		boolean b2 = boundedStack0.push(i4);
		BoundedStack boundedStack2 = new BoundedStack();
		int i5 = boundedStack2.size();
		Integer i6 = 882;
		boolean b3 = boundedStack2.push(i6);
		int i7 = boundedStack2.size();
		BoundedStack boundedStack3 = new BoundedStack();
		int i8 = boundedStack3.size();
		Integer i9 = 882;
		boolean b4 = boundedStack0.push(i9);
	}

	@Test
	public void test3() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test3");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		int i1 = boundedStack0.size();
	}

	@Test
	public void test4() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test4");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
	}

	@Test
	public void test5() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test5");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = (-669);
		boolean b0 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
		Integer i3 = boundedStack0.pop();
	}

	@Test
	public void test6() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test6");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i1 = boundedStack1.size();
		boolean b0 = boundedStack0.push((Integer)i1);
		Integer i2 = boundedStack0.pop();
		Integer i3 = 730;
		boolean b1 = boundedStack0.push(i3);
		Integer i4 = boundedStack0.pop();
	}

	@Test
	public void test7() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test7");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i1 = boundedStack1.size();
		boolean b0 = boundedStack0.push((Integer)i1);
		BoundedStack boundedStack2 = new BoundedStack();
		int i2 = boundedStack2.size();
		BoundedStack boundedStack3 = new BoundedStack();
		int i3 = boundedStack3.size();
		boolean b1 = boundedStack0.push((Integer)i3);
		int i4 = boundedStack0.size();
	}

	@Test
	public void test8() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test8");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 267;
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
		Integer i2 = 152;
		boolean b1 = boundedStack0.push(i2);
	}

	@Test
	public void test9() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test9");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i1 = boundedStack1.size();
		boolean b0 = boundedStack0.push((Integer)i1);
		BoundedStack boundedStack2 = new BoundedStack();
		int i2 = boundedStack2.size();
		BoundedStack boundedStack3 = new BoundedStack();
		int i3 = boundedStack3.size();
		boolean b1 = boundedStack2.push((Integer)i3);
		boolean b2 = boundedStack0.push(i3);
		Integer i4 = boundedStack0.pop();
	}

	@Test
	public void test10() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack6.test10");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i1 = boundedStack1.size();
		boolean b0 = boundedStack0.push((Integer)i1);
	}


}