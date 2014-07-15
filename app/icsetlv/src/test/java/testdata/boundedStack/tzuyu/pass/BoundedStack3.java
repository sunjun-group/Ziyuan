package testdata.boundedStack.tzuyu.pass;

import org.junit.Test;

import testdata.boundedStack.BoundedStack;

public class BoundedStack3 { 
	public static boolean debug = false;

	@Test
	public void test1() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test1");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 984;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = boundedStack0.pop();
		Integer i3 = 1831434049;
		boolean b1 = boundedStack0.push(i3);
		Integer i4 = boundedStack0.pop();
	}

	@Test
	public void test2() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test2");
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
		Integer i5 = boundedStack0.pop();
	}

	@Test
	public void test3() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test3");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-272);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = 847;
		boolean b1 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
		int i3 = boundedStack0.size();
		int i4 = boundedStack0.size();
	}

	@Test
	public void test4() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test4");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 608;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = (-212);
		boolean b1 = boundedStack0.push(i2);
		Integer i3 = boundedStack0.pop();
	}

	@Test
	public void test5() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test5");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i1 = boundedStack1.size();
		boolean b0 = boundedStack0.push((Integer)i1);
		Integer i2 = boundedStack0.pop();
		Integer i3 = 730;
		boolean b1 = boundedStack0.push(i3);
	}

	@Test
	public void test6() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test6");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-279);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = (-194);
		boolean b1 = boundedStack0.push(i1);
		Integer i2 = boundedStack0.pop();
		int i3 = boundedStack0.size();
		Integer i4 = boundedStack0.pop();
	}

	@Test
	public void test7() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test7");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = (-269);
		boolean b0 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
		Integer i3 = boundedStack0.pop();
		Integer i4 = 241;
		boolean b1 = boundedStack0.push(i4);
	}

	@Test
	public void test8() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test8");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 984;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = boundedStack0.pop();
		Integer i3 = 1831434049;
		boolean b1 = boundedStack0.push(i3);
	}

	@Test
	public void test9() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test9");
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
	}

	@Test
	public void test10() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack3.test10");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i1 = boundedStack1.size();
		boolean b0 = boundedStack0.push((Integer)i1);
		int i2 = boundedStack0.size();
		int i3 = boundedStack0.size();
	}


}