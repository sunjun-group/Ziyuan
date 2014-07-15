package testdata.boundedStack.tzuyu.fail;

import org.junit.Test;

import testdata.boundedStack.BoundedStack;

public class BoundedStack7 { 
	public static boolean debug = false;

	@Test
	public void test1() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test1");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 319;
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
		Integer i2 = boundedStack0.pop();
	}

	@Test
	public void test2() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test2");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = boundedStack0.pop();
	}

	@Test
	public void test3() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test3");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 827188473;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		Integer i2 = 827188473;
		boolean b1 = boundedStack1.push(i2);
		boolean b2 = boundedStack0.push(i2);
		BoundedStack boundedStack2 = new BoundedStack();
		Integer i3 = 827188473;
		boolean b3 = boundedStack2.push(i3);
		int i4 = boundedStack2.size();
		boolean b4 = boundedStack0.push((Integer)i4);
		Integer i5 = 947;
		boolean b5 = boundedStack0.push(i5);
	}

	@Test
	public void test4() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test4");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-728);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
		int i2 = boundedStack0.size();
		Integer i3 = boundedStack0.pop();
	}

	@Test
	public void test5() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test5");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		int i1 = boundedStack0.size();
		int i2 = boundedStack0.size();
		Integer i3 = boundedStack0.pop();
	}

	@Test
	public void test6() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test6");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = boundedStack0.pop();
	}

	@Test
	public void test7() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test7");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		int i1 = boundedStack0.size();
		Integer i2 = boundedStack0.pop();
	}

	@Test
	public void test8() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test8");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-83);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
		Integer i2 = boundedStack0.pop();
	}

	@Test
	public void test9() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack7.test9");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = boundedStack0.pop();
	}


}