package testdata.boundedStack.tzuyu.pass;

import org.junit.Test;

import testdata.boundedStack.BoundedStack;

public class BoundedStack4 { 
	public static boolean debug = false;

	@Test
	public void test1() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test1");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-279);
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = (-194);
		boolean b1 = boundedStack0.push(i1);
	}

	@Test
	public void test2() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test2");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		BoundedStack boundedStack1 = new BoundedStack();
		int i1 = boundedStack1.size();
		boolean b0 = boundedStack0.push((Integer)i1);
		Integer i2 = boundedStack0.pop();
	}

	@Test
	public void test3() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test3");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		int i0 = boundedStack0.size();
		Integer i1 = (-100);
		boolean b0 = boundedStack0.push(i1);
		int i2 = boundedStack0.size();
	}

	@Test
	public void test4() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test4");
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
	}

	@Test
	public void test5() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test5");
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
		int i5 = boundedStack0.size();
	}

	@Test
	public void test6() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test6");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 170;
		boolean b0 = boundedStack0.push(i0);
		Integer i1 = boundedStack0.pop();
	}

	@Test
	public void test7() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test7");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 862;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = (-329);
		boolean b1 = boundedStack0.push(i2);
		Integer i3 = 916;
		boolean b2 = boundedStack0.push(i3);
		Integer i4 = boundedStack0.pop();
	}

	@Test
	public void test8() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test8");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 466;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		int i2 = boundedStack0.size();
		Integer i3 = (-622);
		boolean b1 = boundedStack0.push(i3);
		Integer i4 = (-124);
		boolean b2 = boundedStack0.push(i4);
	}

	@Test
	public void test9() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test9");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = 364;
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		Integer i2 = boundedStack0.pop();
	}

	@Test
	public void test10() throws Throwable {
		if (debug) {
			System.out.println("%nBoundedStack4.test10");
		}
		BoundedStack boundedStack0 = new BoundedStack();
		Integer i0 = (-407);
		boolean b0 = boundedStack0.push(i0);
		int i1 = boundedStack0.size();
		int i2 = boundedStack0.size();
	}


}