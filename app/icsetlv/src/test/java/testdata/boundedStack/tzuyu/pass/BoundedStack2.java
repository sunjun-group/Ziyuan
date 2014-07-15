package testdata.boundedStack.tzuyu.pass;

import org.junit.Test;

import testdata.boundedStack.BoundedStack;

public class BoundedStack2 { 
	public static boolean debug = false;

	@Test
	public void test1() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test1");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 267;
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
		Integer i2 = 152;
		boolean b1 = boundedStack0.push(i2);
		int i3 = boundedStack0.size();
	}

	@Test
	public void test2() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test2");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-279);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = (-194);
		boolean b1 = boundedStack0.push(i1);
		Integer i2 = boundedStack0.pop();
	}

	@Test
	public void test3() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test3");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 608;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = (-212);
		boolean b1 = boundedStack0.push(i2);
		Integer i3 = boundedStack0.pop();
		Integer i4 = 451;
		boolean b2 = boundedStack0.push(i4);
	}

	@Test
	public void test4() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test4");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = 815;
		boolean b0 = boundedStack0.push(i1);
		Integer i2 = 536;
		boolean b1 = boundedStack0.push(i2);
		Integer i3 = boundedStack0.pop();
	}

	@Test
	public void test5() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test5");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = 815;
		boolean b0 = boundedStack0.push(i1);
		Integer i2 = 536;
		boolean b1 = boundedStack0.push(i2);
		Integer i3 = boundedStack0.pop();
		int i4 = boundedStack0.size();
	}

	@Test
	public void test6() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test6");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-518);
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
	}

	@Test
	public void test7() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test7");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 862;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = (-329);
		boolean b1 = boundedStack0.push(i2);
		Integer i3 = 916;
		boolean b2 = boundedStack0.push(i3);
	}

	@Test
	public void test8() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test8");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = (-669);
		boolean b0 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
		Integer i3 = boundedStack0.pop();
		Integer i4 = 298;
		boolean b1 = boundedStack0.push(i4);
	}

	@Test
	public void test9() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test9");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = (-422);
		boolean b0 = boundedStack0.push(i1);
		Integer i2 = (-377);
		boolean b1 = boundedStack0.push(i2);
	}

	@Test
	public void test10() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack2.test10");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 785;
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = 872;
		boolean b1 = boundedStack0.push(i1);
		BoundedStack boundedStack1 = new BoundedStack();
		Integer i2 = 785;
		boolean b2 = boundedStack0.push(i2);
	}


}