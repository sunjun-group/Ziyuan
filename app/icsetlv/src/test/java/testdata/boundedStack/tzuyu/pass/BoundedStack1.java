package testdata.boundedStack.tzuyu.pass;

import org.junit.Test;

import testdata.boundedStack.BoundedStack;

public class BoundedStack1 { 
	public static boolean debug = false;

	@Test
	public void test1() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test1");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-518);
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = 2089351445;
		boolean b1 = boundedStack0.push(i2);
	}

	@Test
	public void test2() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test2");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 364;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = boundedStack0.pop();
		int i3 = boundedStack0.size();
		int i4 = boundedStack0.size();
	}

	@Test
	public void test3() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test3");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-236);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
		Integer i2 = 615;
		boolean b1 = boundedStack0.push(i2);
	}

	@Test
	public void test4() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test4");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		int i1 = boundedStack0.size();
		int i2 = boundedStack0.size();
		Integer i3 = (-845);
		boolean b0 = boundedStack0.push(i3);
	}

	@Test
	public void test5() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test5");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 916;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		int i2 = boundedStack0.size();
		Integer i3 = boundedStack0.pop();
	}

	@Test
	public void test6() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test6");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = (-100);
		boolean b0 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
		Integer i3 = boundedStack0.pop();
	}

	@Test
	public void test7() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test7");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 440;
		boolean b0 = boundedStack0.push(i0);
	}

	@Test
	public void test8() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test8");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 608;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = (-212);
		boolean b1 = boundedStack0.push(i2);
	}

	@Test
	public void test9() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test9");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 267;
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
		Integer i2 = 152;
		boolean b1 = boundedStack0.push(i2);
		int i3 = boundedStack0.size();
		Integer i4 = 223;
		boolean b2 = boundedStack0.push(i4);
	}

	@Test
	public void test10() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack1.test10");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 267;
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
	}


}