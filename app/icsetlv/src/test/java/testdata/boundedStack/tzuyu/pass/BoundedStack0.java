package testdata.boundedStack.tzuyu.pass;

import org.junit.Test;

import testdata.boundedStack.BoundedStack;

public class BoundedStack0 { 
	public static boolean debug = false;

	@Test
	public void test1() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test1");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		int i1 = boundedStack0.size();
		int i2 = boundedStack0.size();
	}

	@Test
	public void test2() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test2");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-272);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = 847;
		boolean b1 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
		int i3 = boundedStack0.size();
	}

	@Test
	public void test3() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test3");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 984;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = boundedStack0.pop();
	}

	@Test
	public void test4() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test4");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-272);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = 847;
		boolean b1 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
	}

	@Test
	public void test5() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test5");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-728);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
		int i2 = boundedStack0.size();
	}

	@Test
	public void test6() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test6");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = 163;
		boolean b0 = boundedStack0.push(i1);
	}

	@Test
	public void test7() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test7");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i1 = boundedStack1.size();
		boolean b0 = boundedStack0.push((Integer)i1);
		int i2 = boundedStack0.size();
		int i3 = boundedStack0.size();
		Integer i4 = boundedStack0.pop();
	}

	@Test
	public void test8() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test8");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-279);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = (-194);
		boolean b1 = boundedStack0.push(i1);
		Integer i2 = boundedStack0.pop();
		int i3 = boundedStack0.size();
	}

	@Test
	public void test9() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test9");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-518);
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = 2089351445;
		boolean b1 = boundedStack0.push(i2);
		int i3 = boundedStack0.size();
	}

	@Test
	public void test10() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack0.test10");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 364;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = boundedStack0.pop();
		int i3 = boundedStack0.size();
	}


}