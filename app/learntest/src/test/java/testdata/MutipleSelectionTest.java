package testdata;

import org.junit.Test;

public class MutipleSelectionTest {

	@Test
	public void test1() {
		MutipleSelection.test(1, 2);
	}
	
	@Test
	public void test2() {
		MutipleSelection.test(12, 12);
	}
	
	@Test
	public void test3() {
		MutipleSelection.test(12, 9);
	}
	
	@Test
	public void test4() {
		MutipleSelection.test(9, 16);
	}
	
	@Test
	public void test5() {
		MutipleSelection.test(10, 6);
	}
	
}
